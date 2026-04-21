import sys
import os
from datetime import datetime
sys.path.append(os.path.dirname(os.path.abspath(__file__)) + "/../../../")
from apiserver.services.workProcess import PDFMetadataExtractor, XLSXReader, XLSXCreate
from apiserver.services.utils import LogWrite






class Executor:

      def __init__(self,configFile,xlslFile,metadataFile):
          self.config = configFile
          self.xlsxFile = xlslFile
          self.metadataFile = metadataFile
        #   self.outputFile = outputFile

      def _step1(self):
            with PDFMetadataExtractor(self.config) as pdfmetadataExtractor: 
                  pdfmetadataExtractor.run()
      def _step2(self):
           with XLSXReader(self.xlsxFile,self.metadataFile) as xlsxreader:
                  xlsxreader.run()
      def _step3(self):
           with XLSXCreate(self.metadataFile) as xlsxCreater:
                  xlsxCreater.run()

    #   def _step3(self):
    #        with PDFCreate(self.config,self.metadataFile,self.outputFile) as pdfcreate:
    #               pdfcreate.run()
    #   def _step4(self):
    #        with PDFSplitter(self.metadataFile,self.outputFile) as pdfsplitter:  
    #               pdfsplitter.run()

      def run(self):
        filename = datetime.now().strftime("log_%Y%m%d_%H%M%S.txt")
        log = LogWrite(filename)
        sys.stdout = log  # 이 줄부터 모든 print가 파일에도 저장됨
        start_time = datetime.now()
        print(f"▶ 실행 시작: {start_time.strftime('%Y-%m-%d %H:%M:%S')}")
        print("=" * 30)


        try:
            print("-------------------------- 시작 -------------------------- ")
            print(" --------------------------  1. metadata.json작성 -------------------------- ")
            self._step1()
            print("2. -------------------------- 사건부 조회후 매칭 --------------------------")
            self._step2()
            print("3. --------------------------  시트 생성  -------------------------- ")
            self._step3()
            # print("4. --------------------------  pdf  분리   -------------------------- ")
            # self._step4()

        except Exception as e:
            import traceback
            traceback.print_exc()  # 에러 내용 콘솔에 출력
        finally:
            end_time = datetime.now()
            elapsed = end_time - start_time
            print("=" * 30)
            print(f"실행 종료: {end_time.strftime('%Y-%m-%d %H:%M:%S')}")
            print(f"총 소요시간: {elapsed.seconds}초 ({elapsed})")
            sys.stdout = log.stdout  # 원래 stdout 복구
            log.close()
            input("\n종료하려면 Enter를 누르세요...")  # 에러 나도 항상 실행

#  pyinstaller --onefile Executor.py
if __name__ == "__main__":
    executor = Executor("parameter.json","2026사건부.xlsx","output/metadata.json")  # ✅ 소문자로 인스턴스
    executor.run()