from fastapi import FastAPI, HTTPException
from fastapi import FastAPI
import uvicorn
from router.pdfRouter import pdf_router
from core.lifespan import lifespan 

app = FastAPI(lifespan=lifespan)
app.include_router(pdf_router)

if __name__ == "__main__":
    
    uvicorn.run(app, host="0.0.0.0", port=8084)
    # uvicorn main:app --reload

    ##$env:APP_ENV = "prod"; uvicorn main:app --host 0.0.0.0 --port 8084


    # process_message 진입:
    # {'taskId': 'null', 'pdfType': 'XLSX', 'filePath': 'pdf/smal.pdf', 
    #  'presignedDownloadUrl': 'https://toyproject-lee.s3.ap-northeast-2.amazonaws.com/pdf/smal.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260612T160623Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA3S4HJGLOPM522QPP%2F20260612%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=900&X-Amz-Signature=494d72218e8ba0663137953e78f09851c48b838929db5c752130741095f9936e', 
    #  'presignedUploadUrl': 'https://toyproject-lee.s3.ap-northeast-2.amazonaws.com/xlsx/null_result.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260612T160623Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA3S4HJGLOPM522QPP%2F20260612%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=900&X-Amz-Signature=c147136ef7ac6bf3d69bf4a10b77c5f167c87cb7c58fca466b7956b39324a910',
    #    'resultDownloadUrl': 'https://toyproject-lee.s3.ap-northeast-2.amazonaws.com/xlsx/null_result.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260612T160623Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA3S4HJGLOPM522QPP%2F20260612%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=900&X-Amz-Signature=14be965930ef2b1de1d3e199b8087bb55d40402142007c20e3935c04cb8ef416'}