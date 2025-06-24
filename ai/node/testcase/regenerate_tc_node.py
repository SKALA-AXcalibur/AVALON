# ai/service/testcase/node/generate_tc_node.py

from prompt.testcase.regeneration_prompt import build_regeneration_prompt
from service.testcase.llm_executor import generate_testcase_via_llm
from state.testcase.flow_state import FlowState

import logging

def regenerate_tc_node(state: FlowState) -> FlowState:
    """
    재검토 대상 테스트케이스에 대해 LLM을 통해 재생성하여 기존 항목 덮어씌움
    """
    scenario_id = state.scenario_id

    try:
        # 1. 재검토 대상 추출
        revalidation_tc_ids = {tc_id for tc_id, _ in state.revalidation_targets}

        # 2. 재생성 프롬프트 생성
        prompt = build_regeneration_prompt(state=state)

        # 3. LLM 호출하여 TC 재생성
        regenerated_tc_list = generate_testcase_via_llm(prompt_text=prompt, scenario_id=scenario_id)

        # 4. 기존 tc_list에서 재생성 대상만 제거 + 새로 생성된 것 추가
        updated_tc_list = [
            tc for tc in state.tc_list if tc.tc_id not in revalidation_tc_ids
        ] + regenerated_tc_list

        # 5. 상태 업데이트
        state.tc_list = updated_tc_list
        state.revalidation_targets = []  # 검증 완료 후 초기화

        # 6. 재생성 횟수 count
        state.retry_count += 1

    except (ValueError, TypeError) as e:
        logging.warning(f"[regenerate_tc_node] 입력 값 오류 - scenario_id={scenario_id}: {e}")
    except RuntimeError as e:
        logging.error(f"[regenerate_tc_node] LLM 호출 실패 - scenario_id={scenario_id}: {e}")
    except Exception as e:
        logging.exception(f"[regenerate_tc_node] 알 수 없는 예외 발생 - scenario_id={scenario_id}: {e}")

    return state
