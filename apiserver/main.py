from fastapi import FastAPI
from PdfRequest import PdfRequest
app = FastAPI()


@app.get("/")
def read_root():
    return {"Hello": "World"}
@app.get("/items/{item_id}")
def read_item(item_id: int, q: str | None = None):
    return {"item_id": item_id, "q": q}

# import requests
#
# def process_file_from_s3(url: str):
#     with requests.get(url, stream=True) as r:
#         r.raise_for_status()
#
#         for chunk in r.iter_content(chunk_size=1024 * 1024):  # 1MB씩
#             if chunk:
#                 # 👉 여기서 바로 처리 (파일 안 쌓음)
#                 handle_chunk(chunk)
#
#
# def handle_chunk(chunk: bytes):
#     # 예시: 로그 찍기 / 파싱 / 변환 등
#     print(f"chunk size: {len(chunk)}")


@app.post("/process-pdf")
def process_pdf(request:PdfRequest):
    print(request.filePath)  # pdfs/2024/abc123.pdf
    print(request.jobId)     # job-uuid-001
    return request.filePath

# @app.post("/pdf/")
# def pdf_upload()


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8084)


    # fastapi dev
# uvicorn main:app --port 8084
