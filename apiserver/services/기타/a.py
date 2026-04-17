import fitz
import easyocr
import numpy as np
import re
"""
지정한 페이지를 300dpi로 래스터화한 뒤 OCR 결과를 반환한다.
Args:
    page_index: 읽을 페이지 인덱스
    fraction  : 몇 분의 1로 자를지 (None이면 전체 읽기)
                예) 8 → 1/8 크롭, 2 → 1/2 크롭
    section   : "bottom" → 하단 1/fraction (기본값)
                "top"    → 상단 1/fraction

Examples:
    self._ocr_page(i)                            # 전체
    self._ocr_page(i, fraction=8)                # 하단 1/8
    self._ocr_page(i, fraction=2, section="top") # 상단 1/2
    """
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
def extract_page_number(results: list) -> tuple[int | None, int | None]:
    full_text = " ".join(text for (_, text, _) in results)
    match = re.search(r'(\d+)\s*/\s*(\d+)', full_text)
    if match:
        return int(match.group(1)), int(match.group(2))
    return None, None


def split_into_thirds(lst):
    n = len(lst)
    size = n // 3
    remainder = n % 3

    sizes = []
    for i in range(3):
        sizes.append(size + (1 if i < remainder else 0))

    idx = 0
    parts = []
    for s in sizes:
        parts.append(lst[idx:idx + s])
        idx += s
    return parts


def find_boundary(reader, doc, start_idx):
    """start_idx부터 앞으로 이동하며 문서 마지막 페이지(current==total)를 찾는다."""
    idx = start_idx

    while idx < len(doc):
        result = _ocr_page(reader, doc[idx])
        current, total = extract_page_number(result)
        print(f"  [{idx}] {current}/{total}")

        if current is None or total is None:
            print(f"  [{idx}] OCR 실패, 1페이지 앞으로")
            idx += 1
            continue

        if current == total:
            return idx  # 문서 마지막 페이지 → 경계!

        # 문서 중간 → 마지막 페이지까지 점프
        idx += (total - current)

    return len(doc) - 1  # 끝까지 못 찾으면 마지막 페이지 반환

def save_parts(doc, part_a, part_b, part_c):
    for name, part in [("a", part_a), ("b", part_b), ("c", part_c)]:
        new_doc = fitz.open()
        for idx in part:
            new_doc.insert_pdf(doc, from_page=idx, to_page=idx)
        new_doc.save(f"{name}.pdf")
        new_doc.close()
        print(f"{name}.pdf 저장 완료 ({len(part)}페이지)")
 
if __name__ == "__main__":
#    pdf_path = "20260331133042.pdf"
    pdf_path = "20260325133632.pdf"
    reader = easyocr.Reader(['ko', 'en'])
    doc = fitz.open(pdf_path)

    a, b, c = split_into_thirds(list(range(len(doc))))
    print(f"초기 분할: {len(a)}, {len(b)}, {len(c)}")
    print(f"분할 인덱스: a[-1]={a[-1]}, b[-1]={b[-1]}")

    boundary_1 = find_boundary(reader, doc, a[-1])
    boundary_2 = find_boundary(reader, doc, b[-1])

    part_a = list(range(0, boundary_1 + 1))
    part_b = list(range(boundary_1 + 1, boundary_2 + 1))
    part_c = list(range(boundary_2 + 1, len(doc)))

    print(f"\n조정된 분할: {len(part_a)}, {len(part_b)}, {len(part_c)}")
    save_parts(doc, part_a, part_b, part_c)  # ← 추가
    # for page_index in part_a:
    #     results = _ocr_page(page_index)


    # TODO : 쪽번호 3등분은 완료 여기서 결정금액을 추출해야함
