import fitz
import easyocr
import numpy as np
import re


def ocr_page(doc, reader, page_index: int, bottom_ratio: float = 0.2) -> list:
    """bottom_ratio: 하단 몇 % 를 크롭할지 (기본 20%)"""
    page = doc[page_index]
    pix = page.get_pixmap(dpi=300)
    img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(
        pix.height, pix.width, pix.n
    )
    cut = int(img_array.shape[0] * (1 - bottom_ratio))  # 하단 시작 y좌표
    cropped = img_array[cut:, :, :]
    return reader.readtext(cropped)


def extract_page_number(results: list) -> tuple[int | None, int | None]:
    """OCR 결과에서 '현재쪽/전체쪽' 패턴을 찾아 (current, total) 반환."""
    full_text = " ".join(text for (_, text, _) in results)
    match = re.search(r'(\d+)\s*/\s*(\d+)', full_text)
    if match:
        return int(match.group(1)), int(match.group(2))
    return None, None


def extract_all_page_numbers(pdf_path: str) -> list[tuple[int | None, int | None]]:
    """
    PDF 전체 페이지를 순회하며 각 페이지의 (current, total) 쪽번호를 리스트로 반환.
    쪽번호가 없는 페이지는 (None, None)으로 저장됨.
    """
    reader = easyocr.Reader(['ko', 'en'])
    doc = fitz.open(pdf_path)

    page_numbers = []
    i = 0
    total_pages = len(doc)

    while i < total_pages:
        print(f"{i + 1}페이지 OCR 읽는 중...")
        results = ocr_page(doc, reader, i)
        current, total = extract_page_number(results)

        if current == 1 and total:
            page_numbers.append((i, total))
            print(f"  → 쪽번호 1 발견! 총 {total}페이지 → {total - 1}페이지 스킵")
            i += total
        else:
            print(f"  → 쪽번호 없음 또는 1 아님, 스킵")
            i += 1

    doc.close()
    return page_numbers


if __name__ == "__main__":
    pdf_path = "20260325133632.pdf"  # 경로 수정
    result = extract_all_page_numbers(pdf_path)
    print("\n=== 추출된 쪽번호 리스트 ===")
    for i, (current, total) in enumerate(result):
        print(f"  PDF페이지 {i + 1}: {current} / {total}")