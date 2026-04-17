import fitz
import easyocr
import numpy as np
import os
import re
import json
import sys
from multiprocessing import Pool


# ──────────────────────────────────────────────
# BaseProcessor
# ──────────────────────────────────────────────
class BaseProcessor:
    @staticmethod
    def load_json(path: str) -> dict | list:
        try:
            with open(path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except FileNotFoundError:
            print(f"파일을 찾을 수 없습니다: {path}")
            sys.exit(1)
        except json.JSONDecodeError:
            print(f"JSON 파싱 실패: {path}")
            sys.exit(1)

    @staticmethod
    def save_json(path: str, data: dict | list) -> None:
        with open(path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        print(f"저장 완료 → {path}")

    def run(self):
        raise NotImplementedError("run()을 구현해야 합니다.")


# ──────────────────────────────────────────────
# 공통 OCR / 추출 함수 (워커에서도 사용)
# ──────────────────────────────────────────────
DIVISION_WORD = "채무자"
CASE_WORD = "사건"


def _ocr_page(reader, doc, page_index: int, fraction: int = None, section: str = "bottom") -> list:
    """
    지정한 페이지를 300dpi로 래스터화한 뒤 OCR 결과를 반환한다.
    fraction=None  → 전체
    fraction=8     → 하단 1/8
    fraction=2, section="top" → 상단 1/2
    """
    page = doc[page_index]
    pix = page.get_pixmap(dpi=300)
    img_array = np.frombuffer(pix.samples, dtype=np.uint8).reshape(
        pix.height, pix.width, pix.n
    )
    if fraction is None:
        cropped = img_array
    else:
        h = img_array.shape[0]
        if section == "bottom":
            cropped = img_array[h * (fraction - 1) // fraction:, :, :]
        else:
            cropped = img_array[:h // fraction, :, :]

    return reader.readtext(cropped)


def extract_page_number(results: list) -> tuple[int | None, int | None]:
    full_text = " ".join(text for (_, text, _) in results)
    match = re.search(r'(\d+)\s*/\s*(\d+)', full_text)
    if match:
        return int(match.group(1)), int(match.group(2))
    return None, None


def extract_name(results: list) -> str | None:
    prev_clean = ""
    for (_, text, _) in results:
        clean = text.replace(" ", "")
        if prev_clean == DIVISION_WORD:
            name = text.strip().split()[0] if text.strip() else text.strip()
            return name[:3]
        prev_clean = clean
    return None


def extract_case_number(results: list) -> str | None:
    prev_prev_clean = ""
    prev_clean = ""
    for (_, text, _) in results:
        clean = text.replace(" ", "")
        combined = prev_prev_clean + prev_clean
        if prev_clean == CASE_WORD or combined == CASE_WORD:
            match = re.search(r'\d{4}[가-힣]+\d+', clean)
            if match:
                return match.group(0)
        prev_prev_clean = prev_clean
        prev_clean = clean
    return None


def extract_amount(results: list) -> str | None:
    full_text = " ".join(text for (_, text, _) in results)
    match = re.search(r'[금긍]\s*([\d,]+)\s*원?', full_text)
    if match:
        return match.group(1)
    return None


def extract_decision_pay(results: list) -> bool:
    full_text = " ".join(text for (_, text, _) in results).replace(" ", "")
    return bool(re.search(r'[청정]구[채재]권의표시', full_text))


# ──────────────────────────────────────────────
# 3등분 유틸
# ──────────────────────────────────────────────
def split_into_thirds(lst: list) -> tuple[list, list, list]:
    n = len(lst)
    size = n // 3
    remainder = n % 3
    sizes = [size + (1 if i < remainder else 0) for i in range(3)]
    idx = 0
    parts = []
    for s in sizes:
        parts.append(lst[idx:idx + s])
        idx += s
    return parts[0], parts[1], parts[2]


# ──────────────────────────────────────────────
# 경계 탐색 (메인 프로세스에서 단독 실행)
# ──────────────────────────────────────────────
def find_boundary(reader, doc, start_idx: int) -> int:
    """start_idx부터 앞으로 이동하며 문서 마지막 페이지(current==total)를 찾는다."""
    idx = start_idx
    while idx < len(doc):
        results = _ocr_page(reader, doc, idx)
        current, total = extract_page_number(results)
        print(f"  [{idx}] {current}/{total}")

        if current is None or total is None:
            idx += 1
            continue
        if current == total:
            return idx
        idx += (total - current)

    return len(doc) - 1


# ──────────────────────────────────────────────
# 멀티프로세싱 워커
# ──────────────────────────────────────────────
def init_worker():
    """프로세스당 1회 easyocr Reader 초기화 (CPU)"""
    global worker_reader
    worker_reader = easyocr.Reader(['ko', 'en'], gpu=False)


def parse_worker(args: tuple) -> list:
    """
    page_indices 리스트를 받아 채무자 정보 리스트를 반환한다.
    반환 형식: [(name, case_number, page_indices, amount), ...]
    """
    pdf_path, page_indices = args
    doc = fitz.open(pdf_path)

    people = []
    current_name = None
    current_case = None
    current_pages = []
    current_amount = None

    i = 0
    while i < len(page_indices):
        real_idx = page_indices[i]
        print(f"  [{real_idx + 1}페이지] OCR 읽는 중...")
        results = _ocr_page(worker_reader, doc, real_idx)
        current, total = extract_page_number(results)

        if current == 1:
            if current_pages:
                people.append((current_name, current_case, current_pages, current_amount))

            current_name = extract_name(results)
            current_case = extract_case_number(results)
            current_amount = None

            if current_case and "타채" in current_case:
                print(f"  → 타채 감지! 이름: [{current_name}] / 사건번호: [{current_case}]")
                current_pages = [real_idx]
                i += 1
                start_i = i
                while i < start_i + total - 1 and i < len(page_indices):
                    r = _ocr_page(worker_reader, doc, page_indices[i], fraction=2, section="top")
                    if extract_decision_pay(r):
                        current_amount = extract_amount(r)
                        print(f"  → 청구채권 발견! 금액: [{current_amount}]")
                        break
                    current_pages.append(page_indices[i])
                    i += 1
                i = start_i + total - 1

            else:
                end = min(i + total, len(page_indices))
                current_pages = page_indices[i:end]
                print(f"  → 새 사람! 이름: [{current_name}] / 사건번호: [{current_case}] / {total}페이지")
                i += total

        elif current is None:
            name = extract_name(results)
            case = extract_case_number(results)
            print(f"  → 한 장짜리 채무자. 이름: [{name}] / 사건번호: [{case}]")
            people.append((name, case, [real_idx], None))
            i += 1

        else:
            current_pages.append(real_idx)
            i += 1

    if current_pages:
        people.append((current_name, current_case, current_pages, current_amount))

    doc.close()
    return people


# ──────────────────────────────────────────────
# PDFParser (메인 클래스)
# ──────────────────────────────────────────────
class PDFParser(BaseProcessor):

    def __init__(self, parameter_file: str):
        self.metadata = self.load_json(parameter_file)
        self.pdf_path: str = self.metadata["PDF_PATH"]
        self.output_dir: str = os.path.join(os.path.dirname(self.pdf_path), "output")

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        pass

    def run(self):
        doc = fitz.open(self.pdf_path)
        total_pages = len(doc)
        print(f"총 {total_pages}페이지 감지됨\n")

        # ── 1) 메인 프로세스에서 3등분 경계 탐색 ──
        reader = easyocr.Reader(['ko', 'en'], gpu=False)
        a, b, _ = split_into_thirds(list(range(total_pages)))

        print("=== 경계 1 탐색 ===")
        boundary_1 = find_boundary(reader, doc, a[-1])
        print("=== 경계 2 탐색 ===")
        boundary_2 = find_boundary(reader, doc, b[-1])
        doc.close()

        part_a = list(range(0, boundary_1 + 1))
        part_b = list(range(boundary_1 + 1, boundary_2 + 1))
        part_c = list(range(boundary_2 + 1, total_pages))
        print(f"\n분할 완료: {len(part_a)} / {len(part_b)} / {len(part_c)} 페이지\n")

        # ── 2) 3개 프로세스로 병렬 파싱 ──
        tasks = [
            (self.pdf_path, part_a),
            (self.pdf_path, part_b),
            (self.pdf_path, part_c),
        ]
        with Pool(processes=3, initializer=init_worker) as pool:
            results = pool.map(parse_worker, tasks)

        # ── 3) 순서대로 합쳐서 JSON 저장 ──
        all_people = results[0] + results[1] + results[2]
        metadata_list = [
            {
                "name": name,
                "case_number": case_number,
                "pages": len(pages),
                "page_indices": pages,
                "amount": amount,
            }
            for name, case_number, pages, amount in all_people
        ]

        os.makedirs(self.output_dir, exist_ok=True)
        meta_path = os.path.join(self.output_dir, "metadata.json")
        self.save_json(meta_path, metadata_list)
        print(f"총 {len(metadata_list)}명 저장 완료")


# ──────────────────────────────────────────────
# 엔트리포인트
# ──────────────────────────────────────────────
if __name__ == "__main__":
    with PDFParser("parameter.json") as parser:
        parser.run()