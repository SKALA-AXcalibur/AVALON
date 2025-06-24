# ai/service/testcase/node/generate_tc_node.py

from prompt.testcase.regeneration_prompt import build_regeneration_prompt
from service.testcase.llm_executor import generate_testcase_via_llm
from state.testcase.flow_state import FlowState

import logging

def regenerate_tc_node(state: FlowState) -> FlowState:
    """
    재검토 대상 테스트케이스에 대해 LLM을 통해 재생성하여 기존 항목을 덮어씌웁니다.
    """
    try:
        # 1. 재검토 대상 추출
        revalidation_tc_ids = {tc_id for tc_id, _ in state.revalidation_targets}

        # 2. 재생성 프롬프트 생성
        prompt = build_regeneration_prompt(state=state)

        # 3. LLM 호출하여 TC 재생성
        regenerated_tc_list = generate_testcase_via_llm(prompt)

        # 4. 기존 tc_list에서 재생성 대상만 제거 + 덮어쓰기
        updated_tc_list = [
            tc for tc in state.tc_list if tc.tc_id not in revalidation_tc_ids
        ] + regenerated_tc_list

        # 5. 상태 업데이트
        state.tc_list = updated_tc_list
        state.revalidation_targets = []  # 검증 완료 후 초기화

    except Exception as e:
        logging.exception("[regenerate_tc_node] TC 재생성 실패: %s", str(e))

    return state
