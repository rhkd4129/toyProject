from fastapi import FastAPI, HTTPException
from fastapi import FastAPI
import uvicorn
from router.pdfRouter import pdf_router
from init import lifespan 

app = FastAPI(lifespan=lifespan)
app.include_router(pdf_router)

if __name__ == "__main__":
    
    uvicorn.run(app, host="0.0.0.0", port=8084)
    # uvicorn main:app --reload