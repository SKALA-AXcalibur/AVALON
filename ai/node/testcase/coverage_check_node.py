# ai/node/testcase/coverage_check_node.py

from state.testcase.flow_state import FlowState

def coverage_check_node(state: FlowState) -> FlowState:
    """
    시나리오에서 사용한 API 커버리지 계산
    """
    used_api_ids = {tc.mapping_id for tc in state.tc_list}
    all_api_ids = {api.mapping_id for api in state.request.api_mapping_list}

    coverage = len(used_api_ids) / len(all_api_ids) if all_api_ids else 0.0
    state.coverage = round(coverage, 4)

    # 재생성 판단 로직
    if coverage < 0.6:
        state.tc_list.clear()  # 전체 재생성 위해 TC list 초기화
        return "fail", state
    return "pass", state