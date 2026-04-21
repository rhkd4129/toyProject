import fitz
import easyocr
import numpy as np
import os
import re
import json
from apiserver.services.utils import BaseProcessor
"""
    채무자별 메타데이터(이름, 사건번호, 페이지 수, 시작 페이지)만 추출해 metadata.json으로 저장하는 클래스.
    PDF 분리는 수행하지 않는다.
    """
class PDFMetadataExtractor(BaseProcessor):
    DIVISION_WORD = "채무자"
    CASE_WORD = "사건"

    def __init__(self, parameter_file:str): 
        self.metadata = self.load_json(parameter_file)
    
        self.pdf_path: str = self.metadata["PDF_PATH"]
        self.output_dir: str = os.path.join(os.path.dirname(self.pdf_path), "output")

        self.reader = easyocr.Reader(['ko', 'en'])
        self.doc = fitz.open(self.pdf_path)
        self.total_pages: int = len(self.doc)

        # (이름, 사건번호, [페이지 인덱스, ...]) 리스트
        self.people: list[tuple[str | None, str | None, list[int]]] = []


    def _ocr_page(self, page_index: int, fraction: int = None, section: str = "bottom") -> list:

        page = self.doc[page_index]
        pix = page.get_pixmap(dpi=300)
        img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(
            pix.height, pix.width, pix.n
        )

        if fraction is None:
            # 크롭 없이 전체 페이지 OCR
            cropped = img_array
        else:
            h = img_array.shape[0]
            if section == "bottom":
                # 하단 1/fraction 크롭
                cropped = img_array[h * (fraction - 1) // fraction:, :, :]
            else:
                # 상단 1/fraction 크롭
                cropped = img_array[:h // fraction, :, :]

        return self.reader.readtext(cropped)
    



    def _extract_page_number(self, results: list) -> tuple[int | None, int | None]:
        """
        OCR 결과에서 '현재쪽/전체쪽' 패턴을 찾아 (current, total)을 반환.
        쪽번호가 없으면 (None, None)을 반환한다.
        """
        full_text = " ".join(text for (_, text, _) in results)
        match = re.search(r'(\d+)\s*/\s*(\d+)', full_text)
        if match:
            return int(match.group(1)), int(match.group(2))
        return None, None
    
    def _extract_amount(self, results: list) -> str | None:
        full_text = " ".join(text for (_, text, _) in results)
        
        # 금 6,700,014원 / 금 6,700,014 원 / 금6,700,014원 전부 커버
        match = re.search(r'[금긍]\s*([\d,]+)\s*원?', full_text)
        if match:
            return match.group(1)  # 숫자만 반환 ex) "6,700,014"
        return None
    def _extract_decision_pay(self, results):
        full_text = " ".join(text for (_, text, _) in results).replace(" ", "")
        pattern = r'[청정]구[채재]권의표시'
        return bool(re.search(pattern, full_text))  # match → search로 변경
    
    def _extract_page_number(self, results: list) -> tuple[int | None, int | None]:
        """
        OCR 결과에서 '현재쪽/전체쪽' 패턴을 찾아 (current, total)을 반환.
        쪽번호가 없으면 (None, None)을 반환한다.
        """
        full_text = " ".join(text for (_, text, _) in results)
        match = re.search(r'(\d+)\s*/\s*(\d+)', full_text)
        if match:
            return int(match.group(1)), int(match.group(2))
        return None, None
    


    def _extract_name(self, results: list) -> str | None:
        """
        OCR 결과에서 '채무자' 블록 바로 다음 블록의 텍스트를 이름으로 반환.
        찾지 못하면 None을 반환한다.
        """
        prev_clean = ""
        for (_, text, _) in results:
            clean = text.replace(" ", "")
            if prev_clean == self.DIVISION_WORD:
                name = text.strip().split()[0] if text.strip() else text.strip()
                return name[0:3]
            prev_clean = clean
        return None

    def _extract_case_number(self, results: list) -> str | None:
        """
        사건번호
        """
        prev_prev_clean = ""
        prev_clean = ""
        for (_, text, _) in results:
            clean = text.replace(" ", "")
            combined = prev_prev_clean + prev_clean
            if prev_clean == self.CASE_WORD or combined == self.CASE_WORD:
                match = re.search(r'\d{4}[가-힣]+\d+', clean)
                if match:
                    return match.group(0)
            prev_prev_clean = prev_clean
            prev_clean = clean
        return None
   

    def parse(self) -> None:
        print(f"총 {self.total_pages}페이지 감지됨\n")

        current_name: str | None = None
        current_case: str | None = None
        current_pages: list[int] = []
        current_amount: str | None = None  # 타채 금액 저장용
        i = 0

        while i < self.total_pages:
            print(f"{i + 1}페이지 OCR 읽는 중...")
            results = self._ocr_page(i)  # 전체 페이지 (쪽번호/이름/사건번호 추출용)
            current, total = self._extract_page_number(results)

        
            


            if current == 1:
                if current_pages:
                    self.people.append((current_name, current_case, current_pages, current_amount))

                current_name = self._extract_name(results)
                current_case = self._extract_case_number(results)
                current_amount = None  # 새 사람 시작 시 초기화
                start_index = i

                if current_case and "타채" in current_case:
                    print(f"  → 타채 사건 감지! 이름: [{current_name}] / 사건번호: [{current_case}]")
                    current_pages = [i]
                    i += 1

                    while i < start_index + total:
                        print(f"  → {i + 1}페이지 상단  크롭 OCR 읽는 중...")
                        results = self._ocr_page(i, fraction=3, section="top")  # 상단 절반만 읽기

                        if self._extract_decision_pay(results):
                            # 청구채권 문구 발견 → 금액 추출 후 내부 루프 탈출
                            current_amount = self._extract_amount(results)
                            print(f"  → 청구채권 발견! 금액: [{current_amount}]")
                            break

                        current_pages.append(i)
                        i += 1

                    # 금액 발견 여부와 관계없이 total만큼 점프
                    i = start_index + total

                else:
                    current_pages = list(range(i, i + total))
                    print(f"  → 새 사람 시작! 이름: [{current_name}] / 사건번호: [{current_case}], 총 {total}페이지")
                    print(f"  → {i + 1}~{i + total}페이지 스킵 (본문)")
                    i += total

            elif current is None:
                single_name = self._extract_name(results)
                single_case = self._extract_case_number(results)
                print(f"  → 쪽번호 없음(한 장짜리 채무자), 이름: [{single_name}] / 사건번호: [{single_case}]")
                self.people.append((single_name, single_case, [i], None))
                i += 1

            else:
                current_pages.append(i)
                print(f"  → {current}/{total} (예외처리, 포함)")
                i += 1

        if current_pages:
            self.people.append((current_name, current_case, current_pages, current_amount))

        print(f"\n총 {len(self.people)}명 감지됨\n")


    def save_metadata(self) -> None:
        """파싱 결과를 metadata.json으로만 저장한다. PDF 분리는 수행하지 않는다."""
        os.makedirs(self.output_dir, exist_ok=True)

        metadata_list = []
        for name, case_number, pages, amount in self.people:
            metadata_list.append({
                "name": name,
                "case_number": case_number,
                "pages": len(pages),
                "page_indices": pages,   # PDFSplitter가 읽을 페이지 인덱스
                "amount": amount,        # 타채 청구채권 금액 (타채 아닌 경우 None)
            })
            print(f"   이름: {name} | 사건번호: {case_number} | {len(pages)}페이지 | 금액: {amount}")

        meta_path = os.path.join(self.output_dir, "metadata.json")
        with open(meta_path, 'w', encoding='utf-8') as f:
            json.dump(metadata_list, f, ensure_ascii=False, indent=2)

        print(f"\n 메타데이터 저장 완료 → {meta_path}")
    def __enter__(self):
            return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        pass
    def run(self):
        self.parse()
        self.save_metadata()

# with PDFMetadataExtractor("parameter.json") as reader:
#     reader.run()