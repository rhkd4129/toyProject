import fitz
import easyocr
import numpy as np
import os
from datetime import datetime
import sys

"""
    채무자별 메타데이터(이름, 사건번호, 페이지 수, 시작 페이지)만 추출해 metadata.json으로 저장하는 클래스.
    PDF 분리는 수행하지 않는다.
    """
class A():
    DIVISION_WORD = "채무자"
    CASE_WORD = "사건"

    def __init__(self):
        # base_dir = os.path.dirname(sys.executable) if getattr(sys, 'frozen', False) else os.getcwd()
        # print(base_dir)
        if getattr(sys, 'frozen', False):
            base_dir = os.path.dirname(sys.executable)
        else:
            base_dir = os.getcwd()

        pdfs = [f for f in os.listdir(base_dir) if f.lower().endswith('.pdf')]

        if len(pdfs) == 0:
            print("PDF 파일이 없습니다.")
            sys.exit(1)
        if len(pdfs) > 1:
            print(f"PDF 파일이 2개 이상입니다: {pdfs}")
            sys.exit(1)

        self.pdf_path: str = os.path.join(base_dir, pdfs[0])
        print(f"PDF 파일 감지: {self.pdf_path}")

        today = datetime.now().strftime("%Y%m%d_%H")
        self.output_dir: str = os.path.join(base_dir, today)

        self.reader = easyocr.Reader(['ko', 'en'])
        self.doc = fitz.open(self.pdf_path)
        self.total_pages: int = len(self.doc)

        # (이름, 사건번호, [페이지 인덱스, ...]) 리스트
        self.people: list[tuple[str | None, list[int]]] = []


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
    def _debug_region(self, page_index: int, region: tuple[float, float, float, float]) -> None:
        """
        region 영역을 크롭해서 이미지 파일로 저장. 좌표 튜닝용.
        """
        import cv2

        page = self.doc[page_index]
        pix = page.get_pixmap(dpi=300)
        img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(
            pix.height, pix.width, pix.n
        )

        h, w = img_array.shape[:2]
        top    = int(h * region[0])
        bottom = int(h * region[1])
        left   = int(w * region[2])
        right  = int(w * region[3])

        cropped = img_array[top:bottom, left:right, :]
        cv2.imwrite("debug_region.png", cv2.cvtColor(cropped, cv2.COLOR_RGB2BGR))
        print(f"저장완료 → debug_region.png ({top}:{bottom}, {left}:{right})")

    def _ocr_page_region(self,page_index: int,region: tuple[float, float, float, float] ):
        """
        페이지의 특정 영역만 OCR. region은 (top, bottom, left, right) 비율로 지정.
        예) (0.8, 1.0, 0.0, 1.0) → 하단 20% 전체
        """
        page = self.doc[page_index]
        pix = page.get_pixmap(dpi=300)
        img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(
            pix.height, pix.width, pix.n
        )

        h, w = img_array.shape[:2]
        top    = int(h * region[0])
        bottom = int(h * region[1])
        left   = int(w * region[2])
        right  = int(w * region[3])

        cropped = img_array[top:bottom, left:right, :]
        return self.reader.readtext(cropped)
        
    def parse(self) -> None:
        print(f"총 {self.total_pages}페이지 감지됨\n")

        current_pages: list[int] = []

        for i in range(self.total_pages):
            print(f"{i + 1}페이지 하단 OCR 읽는 중...")
            results = self._ocr_page(i, fraction=8, section="bottom")
            current_pages.append(i)

            if self._extract_no_next_page(results):
                print(f"  → 다음장없음 감지! {i + 1}페이지에서 그룹 완성 ({len(current_pages)}페이지)")
                self.people.append((None, current_pages))
                current_pages = []

        # 다음장없음 없이 끝난 잔여 페이지 처리
        if current_pages:
            print(f"  → 잔여 그룹 (다음장없음 미감지): {len(current_pages)}페이지")
            self.people.append((None, None, current_pages, None))

        print(f"\n총 {len(self.people)}개 문서 감지됨\n")

    def save_pdfs(self) -> None:
        """그룹별로 이름초본.pdf 형식으로 저장. 이름 추출 실패 시 미상초본, 동명이인 시 _2, _3 suffix 추가."""
        os.makedirs(self.output_dir, exist_ok=True)
        
        # 동명이인 처리를 위한 이름별 등장 횟수 카운터
        name_counter = {}

        for idx, (name, pages) in enumerate(self.people, start=1):
            # 그룹의 첫 번째 페이지에서 이름 추출
            first_page = pages[0]
            name = self._extract_name_from_region(first_page)
            
            # 이름 추출 실패 시 '미상'으로 대체
            base_name = f"{name}초본" if name else "미상초본"
            print(f"  → 이름 추출: [{base_name}]")

            # 동명이인 카운트 증가 (첫 등장이면 1, 두 번째면 2 ...)
            name_counter[base_name] = name_counter.get(base_name, 0) + 1
            count = name_counter[base_name]
            
            # 첫 등장이면 그대로, 두 번째부터 _2, _3 suffix 추가
            filename = base_name if count == 1 else f"{base_name}_{count}"

            # 그룹 내 페이지들을 하나의 PDF로 합치기
            out_doc = fitz.open()
            for page_index in pages:
                out_doc.insert_pdf(self.doc, from_page=page_index, to_page=page_index)

            # 최종 파일 저장
            out_path = os.path.join(self.output_dir, f"{filename}.pdf")
            out_doc.save(out_path)
            out_doc.close()
            print(f"  → {filename}.pdf 저장 완료 ({len(pages)}페이지) → {out_path}")

        print(f"\n총 {len(self.people)}개 PDF 저장 완료 → {self.output_dir}")

    def _extract_name_from_region(self, page_index: int) -> str | None:
        """첫 페이지의 이름 영역에서 이름 추출."""
        results = self._ocr_page_region(page_index, region=(0.16, 0.21, 0.15, 0.33))
        full_text = "".join(text for (_, text, _) in results).replace(" ", "")
        return full_text if full_text else None

    def _extract_no_next_page(self, results: list) -> bool:
        """
        OCR 결과에서 '다음장없음' 있으면 True를 반환. 공백 제거한 뒤 검사
        """
        full_text = "".join(text for (_, text, _) in results).replace(" ", "")
        return "다음장없음" in full_text

    def __enter__(self):
            return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.doc.close()  # 추가 필요
    def run(self):
        start = datetime.now()  
        print(f"시작: {start.strftime('%H:%M:%S')}")
        self.parse()
        self.save_pdfs()
        end = datetime.now()
        elapsed = end - start
        minutes, seconds = divmod(elapsed.seconds, 60)
        print(f"완료: {end.strftime('%H:%M:%S')} (소요시간: {minutes}분 {seconds}초)")
        input("\n종료하려면 Enter를 누르세요...")  # 에러 나도 항상 실행


        #  results = self._ocr_page(3,5,"bottom")  # 전체 페이지 (쪽번호/이름/사건번호 추출용)
        # results = self._ocr_page_region(1, region=(0.16, 0.21, 0.15, 0.33))
        # print(self._extract_no_next_page(results))
        
        # for (_ ,text , _ )in results:
        #      print(text)
         



with A() as reader:
    reader.run()