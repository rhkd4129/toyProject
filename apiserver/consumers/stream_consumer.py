import asyncio
import os
import tempfile
import traceback
import redis.asyncio as redis
import httpx
from services.Processer import Processer
from services.XLSXProcessor import XLSXProcessor
from core.config import redis_settings, s3_config
from services.s3_uploader import upload_xlsx
import easyocr
import time
'''
Redis는 기본적으로 데이터를 bytes로 반환해요
이걸 True로 하면 자동으로 String으로 변환해줌
없으면 b"taskId", b"abc123" 이런 식으로 나와서 불편함
'''

READER = easyocr.Reader(['ko', 'en'])  # 서버 시작 시 1회만

# 기존 코드 (하드코딩)
# r = redis.Redis(host='localhost', port=6379, decode_responses=True)
r = redis.Redis(
    host=redis_settings.host,
    port=redis_settings.port,
    decode_responses=True
)

async def consume():
    # Stream을 Pub/Sub처럼 쓰기
    last_id = "$"  # 지금 이후 새로 들어오는 것만
    # last_id = "0"  # 처음엔 처음부터 읽기, "$" 쓰면 이 시점 이후부터만
    while True:
        # XREAD BLOCK 0 → 메시지 올 때까지 무한 대기 (0이면 영원히 블로킹)
        try:
            # 기존 코드 (하드코딩)
            # results = await r.xread({"pdf:events": last_id}, block=0, count=10)

            # pydantic-settings 적용
            results = await r.xread(
                {redis_settings.stream_pdf_events: last_id},
                block=0, count=10
            )
        except Exception as e:
            print(f"Redis 연결 오류: {e}")
            last_id = msg_id  # 다음엔 이 이후부터 읽기
            await asyncio.sleep(5)  # 재연결 대기
            continue

        if not results:
            continue
            # results 구조:
            # [ (stream_name, [ (message_id, {field: value, ...}), ... ]) ]
            # 예: [(b'pdf:events', [('1234-0', {'url': 'https://...', 'key': 'abc'})])]
        print("메시지 도착")

        stream_name, messages = results[0]
        for msg_id, fields in messages:
            #loop = asyncio.get_event_loop()
            # await loop.run_in_executor(None, process_message, fields)
            await process_message(fields)
            # await r.xadd("pdf:results", {"taskId": taskId ,"hi":"hi" })
            print("메세지전송")
            last_id = msg_id  # 다음엔 이 이후부터 읽기


# stream_consumer.py
async  def process_message(fields: dict):

    print(f"process_message 진입: {fields}")
    task_id = fields["taskId"].strip('"')
    input_path = fields["inputPath"].strip('"')
    pdf_type = fields['pdfType']

    local_pdf_path = input_path
    tmp_file_path = None

    # presigned URL이면 HTTP로 다운로드해서 임시 파일로 저장
    if input_path.startswith("http"):
        with tempfile.NamedTemporaryFile(suffix=".pdf", delete=False) as tmp:
            tmp_file_path = tmp.name
        async with httpx.AsyncClient() as client:
            response = await client.get(input_path)
            response.raise_for_status()
            with open(tmp_file_path, "wb") as f:
                f.write(response.content)
        local_pdf_path = tmp_file_path
        print(f"S3 다운로드 완료: {local_pdf_path}")

    try:
        with Processer(local_pdf_path, task_id, reader=READER) as processer:
             metadata_list = await processer.parse()

        with XLSXProcessor(metadata_list,task_id) as xlsx_processor:
            xlsx_processor.find_number()
            output_path = xlsx_processor.create_xlsx()
            

        result_path = output_path
        if s3_config.enabled:
            result_path = upload_xlsx(output_path, s3_config)
            print(f"S3 업로드 완료: {result_path}")

        redis_payload = {
            "taskId": task_id,
            "outputPath": result_path,
            "status": "COMPLETED",
            "inputPath": input_path,
        }

        await r.xadd(redis_settings.stream_pdf_results, redis_payload)


    except Exception as e:
        print(f"[ERROR] process_message 실패: {e}")
        traceback.print_exc()
        await r.xadd(redis_settings.stream_pdf_results, {
            "taskId": task_id,
            "inputPath": input_path,
            "status": "FAILED",
            "errorMessage": str(e)
        })
    finally:
        if tmp_file_path:
            try:
                os.remove(tmp_file_path)
                print(f"{tmp_file_path} : 기존 서버에 있는 파일 삭제 ")

            except OSError as e:
                print(f"[WARN] 임시파일 삭제 실패: {e}")