import pandas as pd
import numpy as np
from BaseProcessor import BaseProcessor
import json
# @staticmethod란?
# @staticmethod는 클래스/인스턴스와 무관하게 동작하는 순수 함수입니다.
# 그래서 self를 파라미터로 받지 않고, 당연히 self.뭔가에도 접근할 수 없습니다.
# _load_metadata는 self.sheets, self.metadata를 수정해야 하는 함수이므로, @staticmethod가 아닌 일반 메서드로 선언해야 합니다.
class XLSXReader(BaseProcessor):
    
    division_name = "채무자"
    division_number ="사건번호"

    def __init__(self,xlsx_file_name:str,metadata_file_name:str):
        self.xlsx_file_name = xlsx_file_name
        self.metadata_file_name = metadata_file_name

        self.metadata = self.load_json(metadata_file_name)
        self.sheets = self._load_xlsx(xlsx_file_name)      

        self.year = xlsx_file_name[0:4]


        
        # 드모르간 법칙으로 이해하면 쉽습니다:
        # ```
        # "A도 있고 B도 있어야 한다"
        # = "A가 없거나 B가 없으면 skip"
        # = not in A or not in B
    def _search_in_sheets(self, target_name: str, target_number: str, item: dict) -> bool:
        found = False
        for sheet_name, df in self.sheets.items():
            if self.division_number not in df.columns or self.division_name not in df.columns:
                continue
    
            matched = df[
                (df[self.division_number] == str(target_number)) &
                # (df[self.division_name] == str(target_name))
                 (df[self.division_name].str.contains(str(target_name), na=False))
            ]
                
            for idx in matched.index:
                found = True
                # matched.loc[idx].to_dict : 매칭된 행(row) 하나를 Series로 가져오고 dict으로변환
                # if pd.notna(v)값이 NaN인 항목 제거
                #
                row_dict = {k: v for k, v in matched.loc[idx].to_dict().items() if pd.notna(v)}
                if '채권번호' in row_dict:
                    row_dict['채권번호'] = row_dict['채권번호'].replace("-", "")
                if "강북" in sheet_name:
                    sheet_name = "강북"
                row_dict["sheet"] = sheet_name

                item["info"] = row_dict
        return found


    def find_number(self):
        for item in self.metadata:
            target_name = item.get('name')
            target_number = item.get('case_number')
            if target_name is None or target_number is None:
                print(f" 필수 속성 누락 - item: {item}")
                continue

            found = self._search_in_sheets(target_name, target_number, item)  # ← 현재 연도
            if(found):
                print(f"{target_name} 찾음")
            if not found:
                if str(target_number[0:4]) != str(self.year):
                    new_xlsx_file = self.xlsx_file_name.replace(self.year, target_number[0:4])
                    print(f"{target_name}은 {target_number[0:4]} 이므로 해당년도 파일 탐색 : {new_xlsx_file}")
                    try:
                        self.sheets = self._load_xlsx(new_xlsx_file)
                        found = self._search_in_sheets(target_name, target_number, item)  # ← 다른 연도
                        if(found):
                            print(f"{target_name} 찾음")
                    except FileNotFoundError:
                        print(f"{new_xlsx_file} 파일을 찾을 수 없습니다.")
                    finally:
                        self.sheets =  self._load_xlsx(self.xlsx_file_name)  # 원본 복원

            if not found:
                print(f" 매치 없음 - 이름: {target_name} | 사건번호: {target_number}")

    def _load_xlsx(self, xlsx_file_name: str):
         return pd.read_excel(xlsx_file_name,sheet_name=None,dtype=str)



    def run(self):
        # self._load_metadata(self.xlsx_file,self.metadata_file)
        self.find_number()
        with open(self.metadata_file_name, "w", encoding="utf-8") as f:
                json.dump(self.metadata, f, ensure_ascii=False, indent=2)

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        pass
        #self.close()



# with XLSXReader("2026사건부.xlsx","output/metadata.json") as reader:
#     reader.run()