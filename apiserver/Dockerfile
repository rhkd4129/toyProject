FROM python:3.10-slim
WORKDIR /app

RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir torch torchvision \
    --index-url https://download.pytorch.org/whl/cpu

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# 모델 미리 다운로드
RUN python -c "import easyocr; easyocr.Reader(['ko', 'en'])"

ENV APP_ENV=prod

EXPOSE 8084
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8084"]
