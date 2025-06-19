# ai/service/testcase/llm_executor.py

from typing import List, Union

from langchain.prompts import ChatPromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from langchain_core.runnables import RunnableLambda
from langchain_core.messages import AIMessage

from service.llm_service import model, gpt_model  # 위에서 만든 claude model
from dto.response.testcase.testcase_data import TestcaseData

import logging, re

# parser: List[TestcaseData]로 결과 파싱
parser = JsonOutputParser(pydantic_object=List[TestcaseData])

# prompt 템플릿 구성
prompt = ChatPromptTemplate.from_messages([
    ("system", "너는 20년차 QA이자 테스트케이스 생성 전문가야."),
    ("human", "{prompt}")
])

# 체인 구성
generate_tc_chain = prompt | gpt_model | RunnableLambda(lambda x: clean_json_like_output(x)) | parser

def generate_testcase_via_llm(prompt_text: str) -> List[TestcaseData]:
    """
    GPT를 이용해 테스트케이스를 생성하고, 파싱된 객체 리스트로 반환
    """
    try:
        return generate_tc_chain.invoke({"prompt": prompt_text})

    except Exception as e:
        # LLM이 JSON이 아닌 결과를 반환했거나 파싱에 실패했을 경우
        logging.warning("[LLM 응답 파싱 실패] 예외 내용: %s", str(e))

        raise RuntimeError("LLM 응답이 JSON으로 파싱되지 않았습니다.")


def clean_json_like_output(text: Union[str, AIMessage]) -> str:
    """
    JSON으로 변환 불가능한 응답 반환 시 사용할 파싱 함수
    - md 코드 블록 형식: ``` 로 시작/끝나는 경우
    - js 표현식: repeat(10)과 같이 string으로 인식할 수 없는 문법이 등장한 경우, + 연산인데 공백이 포함된 문자열인 경우
    - 주석이 추가된 경우
    """
    if isinstance(text, AIMessage):
        text = text.content  # AIMessage일 경우 content만 추출
        
    text = text.strip()
    text = re.sub(r'^```(?:json)?\n?', '', text)
    text = re.sub(r'\n?```$', '', text)
    text = re.sub(r'"([^"]+)"\.repeat\((\d+)\)', lambda m: '"' + m.group(1) * int(m.group(2)) + '"', text)
    text = re.sub(r'"([^"]+)"\s*\+\s*"([^"]+)"', lambda m: '"' + m.group(1) + m.group(2) + '"', text)
    text = re.sub(r'//.*', '', text)
    return text
