"""
시나리오 생성 워크플로우 설정
"""

# 재시도 관련 설정
RETRY_THRESHOLD = 80  # 재시도 임계값 (점수)
MAX_RETRIES = 3  # 최대 재시도 횟수

# LLM 모델 기본 설정
MODEL_TIMEOUT = 120.0  # 모델 호출 타임아웃 (초)
MODEL_TEMPERATURE = 0.1  # 모델 온도 설정
MODEL_MAX_TOKENS = 4096  # 최대 토큰 수

