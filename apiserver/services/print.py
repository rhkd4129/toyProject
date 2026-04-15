
import easyocr
import io
import fitz
import numpy as np
# from paddleocr import PaddleOCR
import os
PDF_PATH = "z.pdf"
print("EasyOCR 초기화 중...")
reader = easyocr.Reader(['ko', 'en'])

doc = fitz.open(PDF_PATH)
print(f"총 {len(doc)}페이지 감지됨\n")

for page_num, page in enumerate(doc):
    print(f"{'='*40}")
    print(f"  {page_num + 1}페이지")
    print(f"{'='*40}")

    pix = page.get_pixmap(dpi=300)
    
    # ✅ PIL 대신 numpy 배열로 변환
    img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(pix.height, pix.width, pix.n)

    results = reader.readtext(img_array)

    for (bbox, text, confidence) in results:
        print(f"{text}")

    print()
 
print("완료!")




