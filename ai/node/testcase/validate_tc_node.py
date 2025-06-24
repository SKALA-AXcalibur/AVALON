# ai/service/testcase/node/generate_tc_node.py
import logging

from prompt.testcase.validation_prompt import build_validation_prompt
from service.testcase.llm_executor import validate_testcase_via_llm

from state.testcase.flow_state import FlowState

def validate_tc_node(state: FlowState) -> FlowState:
    """
    TC 검증 노드 정의

    - 시나리오의 validation 조건과 생성된 테스트케이스 목록을 기반으로 각 테스트케이스가 조건을 충족하는지 LLM을 통해 검증합니다.
    - 전체 테스트케이스를 한 번에 검증하는 방식으로, LLM의 응답은 "true"/"false" 리스트 형식입니다.
    - 검증 조건을 만족하지 못한 테스트케이스의 tc_id를 revalidation_targets에 저장합니다.
    - 이후 노드에서 재생성 대상으로 활용할 수 있습니다.
    """
    scenario_id = state.scenario_id
    try:
        # 프롬프트 생성
        prompt = build_validation_prompt(
            state=state
        )
 
        # LLM 호출 → 결과: [true, (false, "사유"), true, ... ]
        result_list = validate_testcase_via_llm(prompt_text=prompt, scenario_id=scenario_id)

        if not result_list or not isinstance(result_list, list) or len(result_list) != len(state.tc_list):
            raise ValueError("LLM 응답 길이와 테스트케이스 수가 일치하지 않음")

        # 검증 실패한 항목만 추출
        revalidation_targets = []
        for tc, result in zip(state.tc_list, result_list):
            if isinstance(result, tuple) and result[0] == False:
                reason = result[1]
                revalidation_targets.append((tc.tc_id, reason))

        # 상태 업데이트
        state.revalidation_targets = revalidation_targets
    
    except (ValueError, TypeError, AttributeError) as e:
        logging.warning(f"[validate_tc_node] 잘못된 응답 형식 - scenario_id={scenario_id} - {type(e).__name__}: {e}")

    except RuntimeError as e:
        logging.error(f"[validate_tc_node] LLM 응답 시간 초과 - scenario_id={scenario_id}: {e}")

    except Exception:
        logging.exception(f"[validate_tc_node] TC 검증 실패 (Unknown Error) - scenario_id={scenario_id}")
    
    return state

def regenerate_decision_fn(state: FlowState) -> str:
    """
    revalidation_targets에 재생성 대상이 있는지 판단하여 분기
    - 없으면: "pass"
    - 있으면: "fail"
    - 재검증 3번 이상: "giveup"
    """
    if not state.revalidation_targets:
        return "pass"
    if state.retry_count >= 3:
        return "giveup"  # 3회 실패 시
    return "fail"