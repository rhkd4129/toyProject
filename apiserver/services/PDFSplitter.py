import fitz
import os
from BaseProcessor import BaseProcessor 


class PDFSplitter(BaseProcessor):
    """
    metadata.json을 읽어 채무자별로 PDF를 분리·저장하는 클래스.
    OCR 없이 동작하며, pdf_metadata_extractor.py 실행 후 사용한다.
    """

    def __init__(self, metadata_file_name: str,output_file_name:str):
        self.output_dir: str = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output")
        self.doc = fitz.open(output_file_name)
        self.metadata   = self.load_json(metadata_file_name)
        

    # ──────────────────────────────────────────
    # 핵심 처리 메서드
    # ──────────────────────────────────────────

    def split(self) -> None:
        """metadata.json을 기반으로 채무자별 PDF를 output 폴더에 저장한다."""
        os.makedirs(self.output_dir, exist_ok=True)
        unknown_count = 0

        for entry in self.metadata:
            name: str | None = entry.get("name")
            case_number: str | None = entry.get("case_number")
            page_indices: list[int] = entry.get("page_indices", [])

            if not page_indices:
                print(f"   [{name}] page_indices가 비어 있어 스킵합니다.")
                continue

            # ── 파일명 결정 ──
            if name:
                base_name = name[:3]
            else:
                unknown_count += 1
                base_name = "미상" if unknown_count == 1 else f"미상_{unknown_count}"

            # 동명이인 중복 방지
            save_path = os.path.join(self.output_dir, f"{base_name}.pdf")
            counter = 1
            while os.path.exists(save_path):
                counter += 1
                save_path = os.path.join(self.output_dir, f"{base_name}_{counter}.pdf")

            file_name = os.path.basename(save_path)

            # ── PDF 저장 ──
            new_pdf = fitz.open()
            for page_idx in page_indices:
                new_pdf.insert_pdf(self.doc, from_page=page_idx, to_page=page_idx)
            new_pdf.save(save_path)
            new_pdf.close()

            print(f"  {file_name} 저장 완료 | 이름: {name} | 사건번호: {case_number} | {len(page_indices)}페이지")

        print("\n 모든 PDF 분리 완료!")

    def run(self):
        self.split();

    def close(self) -> None:
        self.doc.close()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()


# ──────────────────────────────────────────
# 실행 진입점
# ──────────────────────────────────────────

# if __name__ == "__main__":
#     with PDFSplitter("parameter.json") as splitter:
#         splitter.split()