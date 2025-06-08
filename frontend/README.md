# AVALON Frontend

## Setup & Build

### 의존성 설치

```bash
npm install
```

### 환경변수 설정

```txt
1. 루트 디렉토리에 .env.local 파일 생성

2. .env.local에 key 입력
APP_KEYS=...
```

### 개발 환경 실행

```bash
npm run dev
```

### 빌드 및 배포

```bash
npm run build
npm start
```

### Docker

```bash
# 이미지 빌드
docker build --build-arg NEXT_PUBLIC_API_URL=<API_URL> --tag avalon-frontend:latest .

# 컨테이너 실행
docker run --name avalon-frontend-container --rm -p 3000:3000 --env-file .env.local avalon-frontend:latest
```
