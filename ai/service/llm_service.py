"""
@file: service/llm_service.py
@brief AVALON AI의 LLM 서비스 모듈
@details 이 모듈은 langchain을 이용해 LLM 서비스 기능을 구현합니다.
@version 1.0
"""

from dotenv import load_dotenv
from os import environ
from langchain_anthropic.chat_models import ChatAnthropic
from langchain_openai import ChatOpenAI

load_dotenv()

model = ChatAnthropic(
    model_name=environ.get("MODEL_NAME", 'claude-sonnet-4-20250514'),
    temperature=float(environ.get("MODEL_TEMPERATURE", 0.7)),
    timeout=float(environ.get("MODEL_TIMEOUT", 30.0)),
    api_key=environ.get("ANTHROPIC_API_KEY"),
    max_tokens=2048,
    stop=None,
)

gpt_model = ChatOpenAI(
    model_name=environ.get("GPT_MODEL_NAME", 'gpt-4.1'),
    temperature=float(environ.get("GPT_MODEL_TEMPERATURE", 0.1)),
    request_timeout=float(environ.get("MODEL_TIMEOUT", 120.0)),
    api_key=environ.get("OPENAI_API_KEY"),
    max_tokens=8192,
)


async def call_model(prompt: str) -> str:
    """LLM 호출 함수"""
    response = await model.ainvoke([HumanMessage(content=prompt)])
    return response.content
