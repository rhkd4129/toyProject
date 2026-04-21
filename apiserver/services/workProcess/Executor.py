import sys
import os
from datetime import datetime
if not getattr(sys, 'frozen', False):
    sys.path.append(os.path.dirname(os.path.abspath(__file__)) + "/../../../")
from apiserver.services.workProcess import PDFMetadataExtractor, XLSXReader, XLSXCreate
from apiserver.services.utils import LogWrite

class Executor:

      def __init__(self,xlslFile,metadataFile):
          self.xlsxFile = xlslFile
          self.metadataFile = metadataFile
          os.makedirs(os.path.dirname(metadataFile), exist_ok=True)
        #   self.outputFile = outputFile

      def _step1(self):
            with PDFMetadataExtractor() as pdfmetadataExtractor:
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

#  pyinstaller --onefile Executor.pySs
if __name__ == "__main__":

    print("▶ 스크립트 진입 성공")  # 여기서 출력되면 import는 OK

    try:
        base_dir = os.path.dirname(sys.executable) if getattr(sys, 'frozen', False) else os.getcwd()
        today = datetime.now().strftime("%Y%m%d")
        print("▶ Executor 생성 시도")
        executor = Executor(
            os.path.join(base_dir, "2026사건부.xlsx"),
            os.path.join(base_dir, f"{today}/metadata.json")
        )
        print("▶ Executor 생성 성공, run() 호출")
        executor.run()
    except Exception as e:
        import traceback
        traceback.print_exc()  # 에러를 터미널에 직접 출력
        input("\n에러 확인 후 Enter...")


        # pyinstaller --onefile --add-data "apiserver/services/workProcess/NANUMGOTHIC.TTF;." apiserver/services/workProcess/Executor.py