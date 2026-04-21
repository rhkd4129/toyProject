from pydantic import BaseModel

class PdfRequest(BaseModel):
    presignedDownloadUrl: str
    presignedUploadUrl: str
