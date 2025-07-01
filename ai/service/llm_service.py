"""
@file: service/llm_service.py
@brief AVALON AI의 LLM 서비스 모듈
@details 이 모듈은 langchain을 이용해 LLM 서비스 기능을 구현합니다.
@version 1.0
"""

from dotenv import load_dotenv
from os import environ
from langchain_anthropic.chat_models import ChatAnthropic
from langchain_core.messages import HumanMessage
from config.scenario_config import MODEL_TIMEOUT, MODEL_TEMPERATURE, MODEL_MAX_TOKENS

load_dotenv()

model = ChatAnthropic(
    model_name=environ.get("MODEL_NAME", "claude-sonnet-4-20250514"),
    temperature=float(environ.get("MODEL_TEMPERATURE", MODEL_TEMPERATURE)),
    timeout=float(environ.get("MODEL_TIMEOUT", MODEL_TIMEOUT)),
    max_tokens=int(environ.get("MODEL_MAX_TOKENS", MODEL_MAX_TOKENS)),
    stop=None,
)


def call_model(prompt: str) -> str:
    """LLM 호출 함수"""
    response = model.invoke([HumanMessage(content=prompt)])
    return response.content
