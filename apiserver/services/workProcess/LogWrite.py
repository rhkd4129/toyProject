import sys

class LogWrite:
    def __init__(self, filename):
        self.file = open(filename, "w", encoding="utf-8")
        self.stdout = sys.stdout

    def write(self, data):
        self.stdout.write(data)   # 콘솔에도 출력
        self.file.write(data)     # 파일에도 저장

    def flush(self):
        self.stdout.flush()
        self.file.flush()

    def close(self):
        self.file.close()