# ai/service/testcase/tc_generation_flow.py
from langgraph.graph import StateGraph, END

from state.testcase.flow_state import FlowState
from node.testcase.generate_tc_node import generate_tc_node
from node.testcase.coverage_check_node import coverage_check_node

def build_testcase_flow():
    """
    이건무슨함수다
    설명설명
    """
    builder = StateGraph(state_schema=FlowState)

    # 1. 각 API 매핑에 대해 TC 생성 → 생성된 TC는 상태에 추가
    builder.add_node("generate_tc", generate_tc_node)

    # 2. coverage 판단 후 분기
    builder.add_node("check_coverage", coverage_check_node)

    # 3. 검증 진행
    builder.add_node("validate_tc", validate_tc_node)

    # 4. 종료
    builder.add_node("finalize", finalize_node)

    # 흐름 정의
    builder.set_entry_point("generate_tc")
    builder.add_edge("generate_tc", "check_coverage")
    
    # coverage 판단에 따라 분기
    builder.add_conditional_edges(
        "check_coverage",
        {
            "pass": "validate_tc",     # coverage 충분한 경우
            "fail": "generate_tc"      # 아닐시 전체 재생성
        }
    )

    # # coverage 판단에 따라 분기
    # builder.add_conditional_edges(
    #     "validate_tc",
    #     {
    #         "pass": "finalize",     # validate fail
    #         "fail": "generate_tc"   # 검증 후 finalize
    #     }
    # )

    # builder.add_edge("finalize", END)

    return builder.compile()