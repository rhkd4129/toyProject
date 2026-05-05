import re
import os
import sys
import fitz
import easyocr
import numpy as np
from datetime import datetime


class A:

    DIVISION_WORD  = "채무자"
    DEFENDANT_WORD = "피고"
    ALL_KEYWORDS   = {"지급명령", "이행권고결정", "판결", "집행문", "송달/확정증명서"}
    SET_KEYWORDS   = {"판결", "집행문", "송달/확정증명서"}
    # 채무자로 이름 찾는 키워드
    DEBTOR_KEYWORDS    = {"지급명령"}
    # 피고로 이름 찾는 키워드
    DEFENDANT_KEYWORDS = {"이행권고결정", "판결"}

    def __init__(self):
        if getattr(sys, 'frozen', False):
            self.base_dir = os.path.dirname(sys.executable)
        else:
            self.base_dir = os.path.dirname(os.path.abspath(__file__))

        self.pdf_path: str = self._find_pdf()
        print(f"처리할 PDF: {self.pdf_path}")

        self.output_dir: str = os.path.join(self.base_dir, datetime.now().strftime("%Y%m%d_%H"))

        self.reader = easyocr.Reader(['ko', 'en'])
        self.doc = fitz.open(self.pdf_path)
        self.total_pages: int = len(self.doc)

        # (이름(str | list[str] | None), [페이지 인덱스, ...], 키워드 or None)
        self.people: list[tuple[str | list[str] | None, list[int], str | None]] = []

    # ──────────────────────────────────────────
    # 초기화 헬퍼
    # ──────────────────────────────────────────

    def _find_pdf(self) -> str:
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

    def _ocr_page(self, page_index: int, fraction: int = None, section: str = "bottom") -> list:
        page = self.doc[page_index]
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
        return self.reader.readtext(cropped)

    def _extract_page_number(self, results: list) -> tuple[int | None, int | None]:
        full_text = " ".join(text for (_, text, _) in results)
        match = re.search(r'(\d+)\s*/\s*(\d+)', full_text)
        if match:
            return int(match.group(1)), int(match.group(2))
        return None, None

    def _extract_keyword(self, results: list) -> str | None:
        """OCR 결과에서 5개 키워드 중 하나를 반환. 공백 제거 후 비교."""
        full_text_clean = "".join(text for (_, text, _) in results).replace(" ", "")
        for kw in self.ALL_KEYWORDS:
            if kw.replace(" ", "") in full_text_clean:
                return kw
        return None

    def _clean_name(self, raw: str) -> str:
        """숫자 또는 '(' 전까지 자르고 최대 10글자 반환."""
        cleaned = re.split(r'[\d(]', raw.replace(" ", ""))[0].strip()
        return cleaned[:10]

    def _extract_name(self, results: list) -> str | None:
        """지급명령용: '채무자' 블록 바로 다음 블록을 이름으로 반환."""
        prev_text = ""
        for (_, text, _) in results:
            clean = text.replace(" ", "")
            if prev_text == self.DIVISION_WORD:
                return self._clean_name(text)
            prev_text = clean
        return None

    def _extract_defendants(self, results: list) -> list[str]:
        """
        판결/이행권고결정용: '피고' 다음에 오는 피고 이름(들)을 추출.
        단일 피고 → ['이름']
        복수 피고 → ['이름1', '이름2', ...]
        """
        blocks = [(text.replace(" ", ""), text) for (_, text, _) in results]

        found        = False
        defendants   = []
        numbered_mode = False

        i = 0
        while i < len(blocks):
            clean, original = blocks[i]

            if not found:
                if clean == self.DEFENDANT_WORD:
                    found = True
                i += 1
                continue

            # 번호 패턴: "1.이름" 또는 "1." (1~2자리 숫자만, 날짜 4자리와 구분)
            m = re.match(r'^(\d{1,2})\.(.*)', clean)

            if m:
                numbered_mode = True
                name_part = m.group(2).strip()
                if name_part:
                    defendants.append(self._clean_name(name_part))
                else:
                    # 이름이 다음 블록에 있는 경우
                    if i + 1 < len(blocks):
                        i += 1
                        defendants.append(self._clean_name(blocks[i][1]))

            elif not numbered_mode:
                # 단일 피고: 피고 바로 다음 블록이 이름
                defendants.append(self._clean_name(original))
                break

            # numbered_mode일 때 번호 패턴 아닌 블록은 주소 등으로 간주하고 무시

            i += 1

        return defendants

    # ──────────────────────────────────────────
    # 핵심 처리 메서드
    # ──────────────────────────────────────────

    def parse(self) -> None:
        print(f"총 {self.total_pages}페이지 감지됨\n")

        # ── 1패스: 쪽번호 스캔 ──
        print("=== 1패스: 쪽번호 스캔 ===")

        entries: list[tuple[int, int | None]] = []
        unknown_pages: list[int] = []

        i = 0
        while i < self.total_pages:
            print(f"  {i + 1}페이지 쪽번호 OCR...")
            results = self._ocr_page(i, fraction=8, section="bottom")
            current, total = self._extract_page_number(results)

            if current == 1:
                print(f"    → 새 채무자 시작, 총 {total}페이지 ({i + 1}~{i + total}페이지 스킵)")
                entries.append((i, total))
                i += total

            elif current is None:
                print(f"    → 쪽번호 없음, 미상 묶음에 추가 ({i + 1}페이지)")
                unknown_pages.append(i)
                i += 1

            else:
                if entries:
                    prev_idx, prev_total = entries[-1]
                    entries[-1] = (prev_idx, prev_total + 1)
                i += 1

        print(f"\n  1패스 완료 — {len(entries)}명 감지됨, 미상 {len(unknown_pages)}페이지")

        # ── 2패스: 이름 + 키워드 스캔 ──
        print("\n=== 2패스: 이름/키워드 스캔 ===")

        raw: list[tuple[str | list[str] | None, list[int], str | None]] = []

        for start_idx, total in entries:
            print(f"  {start_idx + 1}페이지 OCR...")
            results = self._ocr_page(start_idx, fraction=1, section="top")
            keyword = self._extract_keyword(results)
            pages   = list(range(start_idx, start_idx + total))

            # 키워드에 따라 이름 추출 방식 분기
            if keyword in self.DEBTOR_KEYWORDS or keyword is None:
                name = self._extract_name(results)          # 채무자
            elif keyword in self.DEFENDANT_KEYWORDS:
                name = self._extract_defendants(results)    # 피고 (list)
                if not name:
                    name = None
            else:
                # 집행문, 송달/확정증명서 → 이름 없음
                name = None

            raw.append((name, pages, keyword))
            print(f"    → 이름: [{name}]  키워드: [{keyword}]  "
                  f"{start_idx + 1}~{pages[-1] + 1}페이지")

        # ── 세트 병합 (판결 + 집행문 + 송달/확정증명서) ──
        print("\n=== 세트 병합 처리 ===")

        i = 0
        while i < len(raw):
            name, pages, keyword = raw[i]

            if (keyword in self.SET_KEYWORDS
                    and i + 2 < len(raw)
                    and raw[i + 1][2] in self.SET_KEYWORDS
                    and raw[i + 2][2] in self.SET_KEYWORDS):

                merged_pages = pages + raw[i + 1][1] + raw[i + 2][1]

                # 이름은 '판결' 그룹에서 가져오기
                merged_name = None
                for j in range(i, i + 3):
                    if raw[j][2] == "판결":
                        merged_name = raw[j][0]
                        break

                self.people.append((merged_name, merged_pages, "판결"))
                print(f"  세트 병합: {raw[i][2]} / {raw[i+1][2]} / {raw[i+2][2]} "
                      f"→ [{merged_name}]판결.pdf")
                i += 3

            else:
                self.people.append((name, pages, keyword))
                i += 1

        # 쪽번호 없는 페이지 → 미상 하나로
        if unknown_pages:
            self.people.append((None, unknown_pages, None))
            print(f"  미상 묶음: {len(unknown_pages)}페이지 → 미상.pdf")

        print(f"\n총 {len(self.people)}건 감지됨\n")

    def _build_base_name(self, name: str | list[str] | None, keyword: str | None) -> str:
        """파일 기본명 생성."""
        if name:
            if isinstance(name, list):
                # 복수 피고: 각 이름 합치기 (이미 _clean_name 처리됨)
                name_part = "".join(name)
            else:
                name_part = name[:3]

            if keyword:
                return f"{name_part}{keyword}"
            else:
                return f"{name_part}_키워드불명"
        else:
            return "미상"

    def save(self) -> None:
        os.makedirs(self.output_dir, exist_ok=True)
        name_counter: dict[str, int] = {}

        for name, pages, keyword in self.people:
            base = self._build_base_name(name, keyword)

            # 중복 처리
            if base in name_counter:
                name_counter[base] += 1
                file_name = f"{base}_{name_counter[base]}"
            else:
                name_counter[base] = 1
                file_name = base

            save_path = os.path.join(self.output_dir, f"{file_name}.pdf")

            new_pdf = fitz.open()
            for page_idx in pages:
                new_pdf.insert_pdf(self.doc, from_page=page_idx, to_page=page_idx)
            new_pdf.save(save_path)
            new_pdf.close()

            print(f"  {file_name}.pdf 저장 완료 (총 {len(pages)}페이지)")

        print("\n모든 PDF 분리 완료!")

    def run(self) -> None:
        start = datetime.now()
        print(f"시작: {start.strftime('%H:%M:%S')}")
        self.parse()
        self.save()
        end = datetime.now()
        elapsed = end - start
        minutes, seconds = divmod(elapsed.seconds, 60)
        print(f"완료: {end.strftime('%H:%M:%S')} (소요시간: {minutes}분 {seconds}초)")

    def close(self) -> None:
        self.doc.close()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()


if __name__ == "__main__":
    try:
        with A() as splitter:
            splitter.run()
    except Exception as e:
        print(f"\n❌ 오류 발생: {e}")
    finally:
        input("\n종료하려면 Enter를 누르세요...")

        # pyinstaller --onedir --console splitter.py