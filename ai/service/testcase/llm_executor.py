# ai/service/testcase/llm_executor.py

from typing import List, Union, Tuple, Optional

from langchain.prompts import ChatPromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from langchain_core.exceptions import OutputParserException
from langchain_core.runnables import RunnableLambda
from langchain_core.messages import AIMessage
from pydantic import ValidationError

from service.llm_service import gpt_model
from dto.response.testcase.testcase_data import TestcaseData

import logging
import ast
import re

# parser: List[TestcaseData]로 결과 파싱
parser = JsonOutputParser(pydantic_object=List[TestcaseData])

# prompt 템플릿 구성
prompt = ChatPromptTemplate.from_messages([
    ("system", "너는 20년차 QA이자 테스트케이스 생성 전문가야."),
    ("human", "{prompt}")
])

def generate_testcase_via_llm(prompt_text: str, scenario_id: Optional[str] = None) -> List[TestcaseData]:
    """
    GPT를 이용해 테스트케이스를 (재)생성하고, 파싱된 객체 리스트로 반환
    """
    try:
        # 체인 구성
        generate_tc_chain = prompt | gpt_model | RunnableLambda(lambda x: clean_json_like_output(x)) | parser
        raw_output = generate_tc_chain.invoke({"prompt": prompt_text})

        return [TestcaseData(**item) for item in raw_output]

    except OutputParserException as e:
        logging.warning(f"OutputParserException - scenario_id={scenario_id} : {e}")
        raise RuntimeError("LLM 응답이 올바른 JSON 포맷이 아닙니다.")

    except ValidationError as e:
        logging.warning(f"[Pydantic 모델 검증 실패 - ValidationError] - scenario_id={scenario_id} : {e}")
        raise RuntimeError("생성된 테스트케이스가 예상된 데이터 형식과 일치하지 않습니다.")

    except Exception as e:
        logging.warning(f"[LLM 테스트케이스 생성 중 알 수 없는 예외] - scenario_id={scenario_id} : {e}")
        raise RuntimeError("LLM 응답 처리 중 알 수 없는 오류가 발생했습니다.")

def validate_testcase_via_llm(prompt_text: str, scenario_id: Optional[str] = None) -> List[Union[bool, Tuple[bool, str]]]:
    """
    GPT를 이용해 테스트케이스의 expected_result가 시나리오 검증 조건을 만족하는지 확인
    - 응답은 반드시 "true" 또는 "false"
    """
    try:
        response = gpt_model.invoke(prompt_text)
        raw = response.content if isinstance(response, AIMessage) else str(response)
        content = raw.strip()

        # 문자열을 Python 리스트로 안전하게 파싱
        result = ast.literal_eval(content)

        if not isinstance(result, list):
            raise ValueError("LLM 응답이 리스트 형식이 아님")

        return result
    
    except (SyntaxError, ValueError) as e:
        logging.warning(f"[LLM 응답 파싱 실패] 형식 오류 - scenario_id={scenario_id} : {e}")
        raise RuntimeError("LLM 응답 파싱 오류 발생")
    
    except AttributeError as e:
        logging.warning(f"[LLM 응답 속성 오류] - scenario_id={scenario_id} : {e}")
        raise RuntimeError("LLM 응답 처리 중 속성 접근 오류 발생")
    
    except TypeError as e:
        logging.warning(f"[LLM 응답 처리 실패 - TypeError] - scenario_id={scenario_id} : {e}")
        raise RuntimeError("LLM 응답 처리 중 타입 오류 발생")
    
    except Exception as e:
        logging.warning(f"[LLM 검증 응답 파싱 실패] 예외 내용 - scenario_id={scenario_id} : {e}")
        raise RuntimeError("LLM 검증 중 오류 발생")

def clean_json_like_output(text: Union[str, AIMessage]) -> str:
    """
    JSON으로 변환 불가능한 응답 반환 시 사용할 파싱 함수
    - md 코드 블록 형식: ``` 로 시작/끝나는 경우
    - js 표현식: repeat(10)과 같이 string으로 인식할 수 없는 문법이 등장한 경우, + 연산인데 공백이 포함된 문자열인 경우
    - 주석이 추가된 경우(//, /* ... */)
    """
    if isinstance(text, AIMessage):
        text = text.content  # AIMessage일 경우 content만 추출
        
    text = text.strip()
    text = re.sub(r'^```(?:json)?\n?', '', text)
    text = re.sub(r'\n?```$', '', text)
    text = re.sub(r'"([^"]+)"\.repeat\((\d+)\)', lambda m: '"' + m.group(1) * int(m.group(2)) + '"', text)
    text = re.sub(r'"([^"]+)"\s*\+\s*"([^"]+)"', lambda m: '"' + m.group(1) + m.group(2) + '"', text)
    text = re.sub(r'//.*', '', text)
    text = re.sub(r'/\*.*?\*/', '', text, flags=re.DOTALL)
    return text
