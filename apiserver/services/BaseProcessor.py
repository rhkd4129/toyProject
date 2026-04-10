# base.py
import json
import sys

class BaseProcessor:
    """공통 기능을 모아두는 부모 클래스"""

    @staticmethod
    def load_json(path: str) -> dict | list:
        """JSON 파일 로드 (에러 처리 포함)"""
        try:
            with open(path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except FileNotFoundError:
            print(f" 파일을 찾을 수 없습니다: {path}")
            sys.exit(1)
        except json.JSONDecodeError:
            print(f" JSON 파싱 실패: {path}")
            sys.exit(1)

    @staticmethod
    def save_json(path: str, data: dict | list) -> None:
        """JSON 파일 저장"""
        with open(path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        print(f"저장 완료 → {path}")

    def run(self):
        raise NotImplementedError("run()을 구현해야 합니다.")