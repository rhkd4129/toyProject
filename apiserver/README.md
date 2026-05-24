# API Server

FastAPI 기반 PDF 처리 서버

---

## 환경 설정

### 필수 패키지 설치

```bash
pip install fastapi uvicorn pydantic-settings python-dotenv redis easyocr
```

### 환경 파일 구조

```
apiserver/
  .env.dev    ← 로컬 개발용
  .env.prod   ← 운영 서버용
```

#### `.env.dev` 예시

```
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_STREAM_PDF_EVENTS=pdf:events
REDIS_STREAM_PDF_RESULTS=pdf:results
```

#### `.env.prod` 예시

```
REDIS_HOST=운영서버-IP
REDIS_PORT=6379
REDIS_STREAM_PDF_EVENTS=pdf:events
REDIS_STREAM_PDF_RESULTS=pdf:results
```

---

## 실행 방법

> **반드시 `apiserver/` 폴더 안에서 실행**

### CMD (명령 프롬프트)

```cmd
# 개발
set APP_ENV=dev && uvicorn main:app --reload --port 8084

# 운영
set APP_ENV=prod && uvicorn main:app --host 0.0.0.0 --port 8084
```

### PowerShell

```powershell
# 개발
$env:APP_ENV="dev"; uvicorn main:app --reload --port 8084

# 운영
$env:APP_ENV="prod"; uvicorn main:app --host 0.0.0.0 --port 8084
```

### Git Bash / Linux / Mac

```bash
# 개발
APP_ENV=dev uvicorn main:app --reload --port 8084

# 운영
APP_ENV=prod uvicorn main:app --host 0.0.0.0 --port 8084
```

---

## 서버 정보

| 항목 | 값 |
|------|-----|
| 기본 포트 | 8084 |
| API 문서 | http://localhost:8084/docs |
| Redis 스트림 (이벤트 수신) | `pdf:events` |
| Redis 스트림 (결과 전송) | `pdf:results` |

---

## 환경별 차이

| 항목 | dev | prod |
|------|-----|------|
| Redis Host | localhost | 운영서버 IP |
| `--reload` | ✅ 사용 (코드 변경 자동 반영) | ❌ 미사용 |
| `--host` | 생략 가능 | `0.0.0.0` 필수 |
