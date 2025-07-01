"""
시나리오 생성 워크플로우 설정
"""

# 재시도 관련 설정
RETRY_THRESHOLD = 80  # 재시도 임계값 (점수)
MAX_RETRIES = 3  # 최대 재시도 횟수

# 검증 관련 설정
MIN_VALIDATION_SCORE = 70  # 최소 합격 점수
VALIDATION_TIMEOUT = 120  # 검증 타임아웃 (초)

# LLM 모델 기본 설정
MODEL_TIMEOUT = 120.0  # 모델 호출 타임아웃 (초)
MODEL_TEMPERATURE = 0.1  # 모델 온도 설정
MODEL_MAX_TOKENS = 4096  # 최대 토큰 수

# 텍스트 처리 관련 설정
LOG_TEXT_LIMIT = 200  # 로깅 시 텍스트 길이 제한
ERROR_LOG_TEXT_LIMIT = 500  # 에러 로깅 시 더 긴 텍스트 제한
