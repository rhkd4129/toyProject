from pydantic import BaseModel

class PdfRequest(BaseModel):
    filePath: str
    jobId: str
