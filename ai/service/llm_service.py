"""
@file: service/llm_service.py
@brief AVALON AI의 LLM 서비스 모듈
@details 이 모듈은 langchain을 이용해 LLM 서비스 기능을 구현합니다.
@version 1.0
"""

from os import environ
from langchain_anthropic.chat_models import ChatAnthropic


model = ChatAnthropic(
    model_name=environ.get("MODEL_NAME", 'claude-sonnet-4-20250514'),
    temperature=float(environ.get("MODEL_TEMPERATURE", 0.7)),
    timeout=float(environ.get("MODEL_TIMEOUT", 30.0)),
    stop=None,
)