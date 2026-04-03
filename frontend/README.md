# Frontend

Vue 3 + Vite 기반 프론트엔드 프로젝트입니다.

---

## 환경 변수

환경별로 `.env` 파일을 분리하여 관리합니다.

| 파일 | 적용 환경 |
|------|----------|
| `.env.development` | 로컬 개발 서버 |
| `.env.production`  | 프로덕션 빌드 |

> 변수명은 반드시 `VITE_` 접두사가 필요합니다. (예: `VITE_API_BASE_URL`)

---

## 실행 명령어

### 개발 서버

| 명령어 | 환경 파일 | 설명 |
|--------|----------|------|
| `npm run dev` | `.env.development` | 로컬 개발 서버 실행 |
| `npm run dev:prod` | `.env.production` | 프로덕션 환경변수로 개발 서버 실행 |

### 빌드

| 명령어 | 환경 파일 | 설명 |
|--------|----------|------|
| `npm run build` | `.env.production` | 프로덕션 빌드 |
| `npm run build:dev` | `.env.development` | 개발 환경변수로 빌드 |

### 빌드 결과 미리보기

```bash
npm run preview
```

---

## 기술 스택

- Vue 3
- Vite
- Vue Router
- Vuex
- Vuetify
- Axios
