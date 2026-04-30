import asyncio
import redis.asyncio as redis
from services.Processer import Processer
from services.XLSXProceesor import XLSXProceesor
import fitz , easyocr
'''
Redis는 기본적으로 데이터를 bytes로 반환해요
이걸 True로 하면 자동으로 String으로 변환해줌
없으면 b"taskId", b"abc123" 이런 식으로 나와서 불편함
'''

READER = easyocr.Reader(['ko', 'en'])  # 서버 시작 시 1회만
r = redis.Redis(host='localhost', port=6379, decode_responses=True)
async def consume():
    # Stream을 Pub/Sub처럼 쓰기
    last_id = "$"  # 지금 이후 새로 들어오는 것만
    # last_id = "0"  # 처음엔 처음부터 읽기, "$" 쓰면 이 시점 이후부터만
    while True:
        # XREAD BLOCK 0 → 메시지 올 때까지 무한 대기 (0이면 영원히 블로킹)
        try:
            results = await r.xread({"pdf:events": last_id}, block=0, count=10)
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
            # loop = asyncio.get_event_loop()
            # await loop.run_in_executor(None, process_message, fields)
            process_message(fields)
            last_id = msg_id  # 다음엔 이 이후부터 읽기


# stream_consumer.py
async  def process_message(fields: dict):
    print(f"process_message 진입: {fields}")
    url = fields["filePath"].strip('"')
    key = fields["fileName"].strip('"')

    try:
        with Processer(url, key, reader=READER) as processer:
            metadata_list = processer.parse()

        with XLSXProceesor(metadata_list) as xlsx_processor:
            xlsx_processor.find_number()
            output = xlsx_processor.create_xlsx()
                    # ✅ 성공 → Spring으로 결과 전송
        # await r.xadd("result:events", {
        #     "taskId": key,
        #     "status": "done",
        #     "resultPath": output
        # })

    except Exception as e:
        # 예외를 반드시 여기서 잡아서 로그 출력
        import traceback
        print(f"[ERROR] process_message 실패: {e}")
        traceback.print_exc()
         # ✅ 실패 → Spring으로 에러 전송
        # await r.xadd("result:events", {
        #     "taskId": key,
        #     "status": "error",
        #     "message": str(e)
        # })