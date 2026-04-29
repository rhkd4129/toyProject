import asyncio
import redis.asyncio as redis
from services.workProcess.Processer import Processer
from services.workProcess.XLSXProceesor import XLSXProceesor

'''
Redis는 기본적으로 데이터를 bytes로 반환해요
이걸 True로 하면 자동으로 String으로 변환해줌
없으면 b"taskId", b"abc123" 이런 식으로 나와서 불편함
'''

r = redis.Redis(host='localhost', port=6379, decode_responses=True)
async def consume():
    # Stream을 Pub/Sub처럼 쓰기
    last_id = "$"  # 지금 이후 새로 들어오는 것만
    # last_id = "0"  # 처음엔 처음부터 읽기, "$" 쓰면 이 시점 이후부터만
    while True:
        # XREAD BLOCK 0 → 메시지 올 때까지 무한 대기 (0이면 영원히 블로킹)
        results = await r.xread({"pdf:events": last_id}, block=0, count=10)
        if results:
            # results 구조:
            # [ (stream_name, [ (message_id, {field: value, ...}), ... ]) ]
            # 예: [(b'pdf:events', [('1234-0', {'url': 'https://...', 'key': 'abc'})])]
            stream_name, messages = results[0]
            for msg_id, fields in messages:
                url = fields["path"]
                key = fields["key"]
                with Processer(url,key) as prcesser:
                    metadata_list = prcesser.parse()
                
                with XLSXProceesor(metadata_list) as xlsxProceesor:
                    xlsxProceesor.find_number()
                    xlsxProceesor.create_xlsx()

                
                # Python에서 스코프를 새로 만드는 건 함수/클래스 뿐입니다.


                # TODO: 실제 작업 처리

                last_id = msg_id  # 다음엔 이 이후부터 읽기
                