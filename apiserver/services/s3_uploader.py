import os
import boto3
from core.config import S3Settings


def upload_xlsx(local_path: str, settings: S3Settings) -> str:
    filename = os.path.basename(local_path)
    s3_key = settings.result_key_prefix + filename
    client = boto3.client("s3", region_name=settings.region)
    client.upload_file(
        local_path,
        settings.bucket,
        s3_key,
        ExtraArgs={"ContentType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
    )
    return s3_key
