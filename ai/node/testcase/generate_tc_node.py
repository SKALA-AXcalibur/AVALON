# ai/service/testcase/node/generate_tc_node.py
from typing import List
from json import JSONDecodeError

from dto.response.testcase.testcase_data import TestcaseData
from state.testcase.flow_state import FlowState
from prompt.testcase.generation_prompt import build_generation_prompt
from service.testcase.llm_executor import generate_testcase_via_llm

import logging

async def generate_tc_node(state: FlowState) -> FlowState:
    """
    TC 생성 노드 정의 (진입점 전용)
    - 프롬프트 구성 및 TC 생성 수행
    - 생성된 TC를 state에 누적
    """
    scenario_id = state.scenario_id
    prompt = build_generation_prompt(
        api_mapping_list=state.request.api_mapping_list,
        scenario=state.request.scenario,
    )

    # 생성된 prompt로 LLM 호출 진행
    try:
        testcase: List[TestcaseData] = generate_testcase_via_llm(prompt_text=prompt, scenario_id=scenario_id)

        for tc in testcase:
            if not tc.mapping_id:
                raise ValueError(f"[generate_tc_node] TC 응답에 mapping_id 누락 - scenario_id={scenario_id}")

        state.tc_list.extend(testcase)

    except (ValueError, TypeError, AttributeError) as e:
        logging.warning(f"[generate_tc_node] 잘못된 응답 형식 - scenario_id={scenario_id} - {type(e).__name__}: {e}")

    except JSONDecodeError as e:
        logging.error(f"[generate_tc_node] JSON 파싱 실패 - scenario_id={scenario_id} - {e}")

    except TimeoutError as e:
        logging.error(f"[generate_tc_node] LLM 응답 시간 초과 - scenario_id={scenario_id} - {e}")

    except Exception:
        logging.exception(f"[generate_tc_node] TC 생성 중 알 수 없는 오류 발생 - scenario_id={scenario_id}")

    return state