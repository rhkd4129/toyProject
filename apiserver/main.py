from fastapi import FastAPI, HTTPException
import requests
from PdfRequest import PdfRequest

app = FastAPI()


@app.post("/process-pdf")
def process_pdf(request: PdfRequest):
    with requests.get(request.presignedDownloadUrl, stream=True) as download:
        if download.status_code != 200:
            raise HTTPException(status_code=400, detail=f"S3 다운로드 실패: {download.status_code}")

        upload = requests.put(
            request.presignedUploadUrl,
            data=download.content,
            headers={"Content-Type": "application/pdf"}
        )

    if upload.status_code != 200:
        raise HTTPException(status_code=400, detail=f"S3 업로드 실패: {upload.status_code}")

    return {"status": "success"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8084)

# uvicorn main:app --port 8084
# 우선순위	기능	이유
# ⭐⭐⭐	Background Tasks	현재 코드에 바로 적용 가능
# ⭐⭐⭐	Dependency Injection	인증, 공통 로직 분리 핵심
# ⭐⭐	Response Model	API 명세 명확해짐
# ⭐⭐	Lifespan + httpx	성능 개선
# ⭐	Middleware	로깅, CORS 등 운영 레벨
# 현재 코드에서 가장 임팩트 있는 변경은 requests → httpx 비동기 전환 + Background Tasks 조합이에요. 원하시면 전체 리팩토링 코드도 작성해드릴게요!





