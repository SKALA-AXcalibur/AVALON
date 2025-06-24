# node/testcase/clear_tc_node.py

from state.testcase.flow_state import FlowState

def clear_tc_node(state: FlowState) -> FlowState:
    """
    coverage 기준 미달 시 테스트케이스 목록 초기화
    """
    state.tc_list = []
    return state