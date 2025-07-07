FROM python:3.11-slim-bookworm AS builder

# 빌드에 필요한 시스템 의존성 설치 (예: C 확장 컴파일용)
RUN apt-get update && \
  apt-get install -y --no-install-recommends build-essential && \
  rm -rf /var/lib/apt/lists/*

# uv 0.7.13 바이너리를 공식 distroless 이미지에서 복사
COPY --from=ghcr.io/astral-sh/uv:0.7.13 /uv /usr/local/bin/uv

# 작업 디렉토리 설정
WORKDIR /app

# uv 캐시를 활용하기 위한 환경 변수 설정
# UV_COMPILE_BYTECODE=1:.pyc 파일 생성으로 런타임 시작 속도 개선
ENV UV_COMPILE_BYTECODE=1

# 1단계: 의존성 파일만 복사하여 의존성 레이어 캐싱 극대화
COPY pyproject.toml uv.lock ./

# 2단계: 프로덕션 의존성 설치 (--no-dev로 개발 의존성 제외)
# --mount=type=cache: 빌드 간 uv 캐시 공유로 속도 향상
RUN --mount=type=cache,target=/root/.cache/uv \
  uv sync --locked --no-dev --no-install-project

# 3단계: 애플리케이션 소스 코드 전체 복사
COPY . .

# 4단계: 애플리케이션 자체를 가상 환경에 설치
RUN --mount=type=cache,target=/root/.cache/uv \
  uv sync --locked --no-dev

FROM python:3.11-slim-bookworm AS final

# 보안 강화를 위한 비-루트 사용자 생성 및 설정
RUN groupadd --system --gid 1001 appuser && \
  useradd --system --uid 1001 --gid appuser --no-create-home appuser

# 작업 디렉토리 생성 및 소유권 부여
WORKDIR /app
RUN chown appuser:appuser /app

# builder 스테이지에서 생성된 가상 환경(.venv)만 복사
COPY --from=builder --chown=appuser:appuser /app/.venv /app/.venv

# 애플리케이션 소스 코드 복사
COPY --from=builder --chown=appuser:appuser /app /app

# 비-루트 사용자로 전환
USER appuser

# 가상 환경의 실행 파일을 PATH에 추가
ENV PATH="/app/.venv/bin:$PATH"

# 리스닝할 포트
EXPOSE 8080

# 실행 명령어
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]