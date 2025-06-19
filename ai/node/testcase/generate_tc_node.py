# ai/service/testcase/node/generate_tc_node.py
from typing import List
from json import JSONDecodeError

from dto.response.testcase.testcase_data import TestcaseData

from state.testcase.flow_state import FlowState
from prompt.testcase.prompt_builder import build_prompt
from service.testcase.llm_executor import generate_testcase_via_llm

import logging

async def generate_tc_node(state: FlowState) -> FlowState:
    """
    TC 생성 노드 정의 (진입점 전용)
    - 프롬프트 구성 및 TC 생성 수행
    - 생성된 TC를 state에 누적
    """
    prompt = build_prompt(
        api_mapping_list=state.request.api_mapping_list,
        scenario=state.request.scenario,
    )

    # 생성된 prompt로 LLM 호출 진행
    try:
        testcase: List[TestcaseData] = generate_testcase_via_llm(prompt)

        for tc in testcase:
            # mapping_id가 있는 상태로 올 거라고 기대
            if not tc.mapping_id:
                raise ValueError("TC 응답에 mapping_id 누락")

        state.tc_list.extend(testcase) # 생성된 리스트 추가
        
    
    except ValueError as e:
        logging.warning(f"[generate_tc_node] ValueError: {e}")

    except TypeError as e:
        logging.warning(f"[generate_tc_node] TypeError: {e}")

    except AttributeError as e:
        logging.warning(f"[generate_tc_node] AttributeError: {e}")

    except JSONDecodeError as e:
        logging.error(f"[generate_tc_node] JSON decode 실패: {e}")

    except TimeoutError as e:
        logging.error(f"[generate_tc_node] LLM 응답 Timeout: {e}")

    except Exception:
        logging.exception("[generate_tc_node] TC 생성 실패 (Unknown Error)")

    return state