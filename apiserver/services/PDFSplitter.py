import re
import os
import sys
import fitz
import easyocr
import numpy as np


class PDFSplitter:

    """
    채무자별로 PDF를 분리하는 클래스.
    OCR(EasyOCR)을 사용해 쪽번호와 채무자 이름을 인식하고,
    사람별로 페이지를 묶어 개별 PDF로 저장한다.
    """

    DIVISION_WORD = "채무자"

    def __init__(self):
        # 현재 스크립트가 위치한 디렉토리
        # 수정 (exe/py 둘 다 동작)
        if getattr(sys, 'frozen', False):
            self.base_dir = os.path.dirname(sys.executable)  # exe 위치
        else:
            self.base_dir = os.path.dirname(os.path.abspath(__file__))  # py 위치

        # 같은 폴더에서 PDF 파일 자동 탐색
        self.pdf_path: str = self._find_pdf()
        print(f"처리할 PDF: {self.pdf_path}")  # 이 PDF를 처리한다

        self.output_dir: str = os.path.join(self.base_dir, "output")

        self.reader = easyocr.Reader(['ko', 'en'])
        self.doc = fitz.open(self.pdf_path)
        self.total_pages: int = len(self.doc)

        # (이름, [페이지 인덱스, ...]) 리스트
        self.people: list[tuple[str | None, list[int]]] = []

    # ──────────────────────────────────────────
    # 초기화 헬퍼
    # ──────────────────────────────────────────

    def _find_pdf(self) -> str:
        """스크립트와 같은 폴더에 있는 PDF 파일을 찾아 경로를 반환한다."""
        pdfs = [f for f in os.listdir(self.base_dir) if f.lower().endswith('.pdf')]

        if len(pdfs) == 0:
            print("❌ PDF 파일을 찾을 수 없습니다.")
            sys.exit(1)
        if len(pdfs) > 1:
            print(f"❌ PDF 파일이 여러 개 감지됐습니다: {pdfs}\n하나만 남겨주세요.")
            sys.exit(1)

        return os.path.join(self.base_dir, pdfs[0])

    # ──────────────────────────────────────────
    # OCR 관련 메서드
    # ──────────────────────────────────────────

    def _ocr_page(self, page_index: int) -> list:
        """지정한 페이지를 300dpi로 래스터화한 뒤 OCR 결과를 반환한다."""
        page = self.doc[page_index]
        pix = page.get_pixmap(dpi=300)
        img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(pix.height, pix.width, pix.n)
        return self.reader.readtext(img_array)

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
        prev_text = ""
        for (_, text, _) in results:
            clean = text.replace(" ", "")
            if prev_text == self.DIVISION_WORD:
                return text
            prev_text = clean
        return None

    # ──────────────────────────────────────────
    # 핵심 처리 메서드
    # ──────────────────────────────────────────

    def parse(self) -> None:
        """전체 PDF를 순회하며 사람별 페이지 범위를 파싱한다."""
        print(f"총 {self.total_pages}페이지 감지됨\n")

        current_name: str | None = None
        current_pages: list[int] = []
        i = 0

        while i < self.total_pages:
            print(f"{i + 1}페이지 OCR 읽는 중...")
            results = self._ocr_page(i)
            current, total = self._extract_page_number(results)

            if current == 1:
                # ── 새로운 사람의 첫 페이지 (1/N) ──
                if current_pages:
                    self.people.append((current_name, current_pages))

                current_name = self._extract_name(results)
                current_pages = list(range(i, i + total))

                print(f"  → 새 사람 시작! 이름: [{current_name}], 총 {total}페이지")
                print(f"  → {i + 1}~{i + total}페이지 스킵 (본문)")
                i += total

            elif current is None:
                # ── 쪽번호 없음 = 한 장짜리 독립 채무자 파일 ──
                single_name = self._extract_name(results)
                print(f"  → 쪽번호 없음(한 장짜리 채무자), 이름: [{single_name}]")
                self.people.append((single_name, [i]))
                i += 1

            else:
                # ── 예외: 1이 아닌 쪽번호가 갑자기 등장 ──
                current_pages.append(i)
                print(f"  → {current}/{total} (예외처리, 포함)")
                i += 1

        # 마지막 사람 저장
        if current_pages:
            self.people.append((current_name, current_pages))

        print(f"\n총 {len(self.people)}명 감지됨\n")

    def save(self) -> None:
        """파싱 결과를 바탕으로 사람별 PDF를 output 폴더에 저장한다."""
        os.makedirs(self.output_dir, exist_ok=True)
        unknown_count = 0
        name_counter: dict[str, int] = {}  # 이름 등장 횟수 추적

        for name, pages in self.people:
            if name:
                file_name = name[:3]
            else:
                unknown_count += 1
                file_name = "미상" if unknown_count == 1 else f"미상_{unknown_count}"

            # 중복 이름 처리
            if file_name in name_counter:
                name_counter[file_name] += 1
                file_name = f"{file_name}_{name_counter[file_name]}"
            else:
                name_counter[file_name] = 1

            save_path = os.path.join(self.output_dir, f"{file_name}.pdf")

            new_pdf = fitz.open()
            for page_idx in pages:
                new_pdf.insert_pdf(self.doc, from_page=page_idx, to_page=page_idx)
            new_pdf.save(save_path)
            new_pdf.close()

            print(f" {file_name}.pdf 저장 완료 (총 {len(pages)}페이지)")

        print("\n모든 PDF 분리 완료!")

    def run(self) -> None:
        """parse → save를 순서대로 실행하는 편의 메서드."""
        self.parse()
        self.save()

    def close(self) -> None:
        """열려 있는 PDF 문서를 닫는다."""
        self.doc.close()

    # ──────────────────────────────────────────
    # 컨텍스트 매니저 지원
    # ──────────────────────────────────────────

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()


# ──────────────────────────────────────────
# 실행 진입점
# ──────────────────────────────────────────

if __name__ == "__main__":
    try:
        with PDFSplitter() as splitter:
            splitter.run()
    except Exception as e:
        print(f"\n❌ 오류 발생: {e}")
    finally:
        input("\n종료하려면 Enter를 누르세요...")