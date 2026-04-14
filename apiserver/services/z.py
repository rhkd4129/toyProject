import fitz
import easyocr
import numpy as np
import os
import re
import json
import sys
from BaseProcessor import BaseProcessor
"""
    채무자별 메타데이터(이름, 사건번호, 페이지 수, 시작 페이지)만 추출해 metadata.json으로 저장하는 클래스.
    PDF 분리는 수행하지 않는다.
    """
class A(BaseProcessor):
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
    

    def _extract_decision_pay(self,results):
        combined = (results[0][1] + results[1][1]).replace(" ", "")
        pattern = r"^[청정]구[채재]권의표시$"
        return bool(re.match(pattern, combined))
        

    def parse(self) -> None:
        """전체 PDF를 순회하며 사람별 페이지 범위를 파싱한다."""
        print(f"총 {self.total_pages}페이지 감지됨\n")
        current_name: str | None = None
        current_case: str | None = None
        current_pages: list[int] = []
        i = 0
        while i < self.total_pages:
            print(f"{i + 1}페이지 OCR 읽는 중...")
            results = self._ocr_page(i)
            current, total = self.extract_page_number(results)

            if current == 1:
                if current_pages:
                    self.people.append((current_name, current_case, current_pages))

                current_name = self._extract_name(results)
                current_case = self._extract_case_number(results)
                current_pages = list(range(i, i + total))

                
                if current_case in "타채": #결정금액이 잇는문서
                    results = self._ocr_page(i,fraction=2,section="top")
                    if(self._extract_decision_pay(results)):
                        # TODO: 여기서 금액을 추출해야함 
                        pass
                    i +=1
                else:
                    #결정금액이없다.
                    print(f"  → {i + 1}~{i + total}페이지 스킵 (본문)")
                    i += total


