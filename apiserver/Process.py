# class XlsxCreate:
#     def execute(self, fields):
#         print("xlsx 작업")

# class DiviWork:
#     def execute(self, fields):
#         print("divi 작업")

# STRATEGIES = {
#     "XLSX": XlsxCreate(),
#     "divi": DiviWork(),
# }

# def process(obj):
#     strategy = STRATEGIES.get(obj.pdfType)
#     if strategy is None:
#         raise ValueError("할 수 없는 작업")
#     strategy.execute(obj)  # ✅ .execute() 호출