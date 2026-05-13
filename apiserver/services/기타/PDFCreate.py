import fitz  # pymupdf
import os
from apiserver.services.workProcess.BaseProcessor import BaseProcessor


class PDFCreate(BaseProcessor):
    font_path = "NANUMGOTHIC.TTF"
    x = 710   #^
    y = 515   #>
    def __init__(self, parameter_file, metadata_file_name,output_file_name):
        self.output_file_name = output_file_name
        params = self.load_json(parameter_file)
        self.pdf_path: str = params["PDF_PATH"]
        self.metadata   = self.load_json(metadata_file_name)
        self.total_page = sum(item["pages"] for item in self.metadata)

    def create_pdf(self):
        doc = fitz.open(self.pdf_path)
        page_idx = 0  # 현재 페이지 인덱스 추적
        for affair in self.metadata:
            manager  = affair.get("담당자")
            bond_num = affair.get("채권번호")
            num_pages = affair["pages"]

            if manager is None or bond_num is None:
                print(f"필수 속성 누락: {affair.get('name')} → {num_pages}페이지 스킵")
                page_idx += num_pages
                continue

            # 각 인원의 첫 번째 페이지에만 텍스트 삽입
            if page_idx < len(doc):
                page = doc[page_idx]
                formatted = f"{bond_num[:3]}-{bond_num[3:]}"
                
                page.insert_text(
                    (self.x, self.y),
                    manager,
                    fontfile=self.font_path,
                    fontname="NanumGothic",
                    fontsize=10,
                    color=(0, 0, 0),
                    overlay=True,  
                    rotate=270
                )
                
                page.insert_text(
                    (self.x-15, self.y),  # reportlab의 y-17 → fitz는 아래가 +
                    formatted,
                    fontfile=self.font_path,
                    fontname="NanumGothic",
                    fontsize=10,
                    color=(0, 0, 0),
                    overlay=True,  
                    rotate=270
                )

            page_idx += num_pages  # 다음 인원의 시작 페이지로 이동

        doc.save(self.output_file_name)
        doc.close()
        print(f"생성완료: {os.path.abspath(self.output_file_name)}")

    def __enter__(self):  return self
    def __exit__(self, *_): pass

    def run(self):
        self.create_pdf()


# with PDFCreate("parameter.json","output/metadata.json") as pdf:
#     pdf.run()