FROM node:22-alpine AS builder
#← 이 스테이지 이름이 "builder"
#--from=builder는 "builder라고 이름붙인
#첫번째 스테이지에서 파일을 가져와라" 라는 뜻
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
