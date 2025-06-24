# ai/service/testcase/tc_generation_flow.py
from langgraph.graph import StateGraph, END

from state.testcase.flow_state import FlowState
from node.testcase.generate_tc_node import generate_tc_node
from node.testcase.coverage_check_node import coverage_check_node, coverage_decision_fn
from node.testcase.clear_tc_node import clear_tc_node
from node.testcase.validate_tc_node import validate_tc_node, regenerate_decision_fn
from node.testcase.regenerate_tc_node import regenerate_tc_node

def build_testcase_flow():
    """
    TC 생성 랭그래프 노드 정의
    LangGraph 기반으로 다음과 같은 노드를 순차적으로 실행하는 흐름을 정의합니다:

    1. generate_tc: 시나리오 기반 최초 테스트케이스 생성
    2. check_coverage: 생성된 TC의 API 커버리지 측정
    3. clear_tc: 커버리지 기준 미달 시 TC 초기화 및 재생성 루프로 회귀
    4. validate_tc: 생성된 TC의 정합성 검증 (value, expected_result, param 일치 등)
    5. regenerate_tc: 검증 실패한 TC 재생성 및 검증 루프 수행 (최대 3회)

    조건 분기에 따라 TC 재생성 루프를 반복하고,
    커버리지가 일정 수준 이상이며 검증도 통과한 경우 최종 종료(END)됩니다.

    Returns:
        Compiled LangGraph flow 객체 (GraphRunnable[FlowState])
    """
    
    builder = StateGraph(state_schema=FlowState)

    # 1. 각 API 매핑에 대해 TC 생성 → 생성된 TC는 상태에 추가
    builder.add_node("generate_tc", generate_tc_node)

    # 2. coverage 판단 후 분기
    builder.add_node("check_coverage", coverage_check_node)

    # 2-1. coverage 미달 시 재생성 로직 수행 전 TC 리스트 초기화
    builder.add_node("clear_tc", clear_tc_node)

    # 3. 검증 진행
    builder.add_node("validate_tc", validate_tc_node)

    # 4. 검증 후 실패 TC에 대한 재생성 수행
    builder.add_node("regenerate_tc", regenerate_tc_node)

    # 흐름 정의
    builder.set_entry_point("generate_tc")
    builder.add_edge("generate_tc", "check_coverage")

    # coverage 판단에 따라 분기
    builder.add_conditional_edges(
        source="check_coverage",
        path=coverage_decision_fn,
        path_map={
            "pass": "validate_tc",     # coverage 충분한 경우
            "fail": "clear_tc"         # 아닐시 생성 tc 초기화
        }
    )

    builder.add_edge("clear_tc", "generate_tc")

    # 검증 실패 TC 존재 여부에 대해 분기
    builder.add_conditional_edges(
        source="validate_tc",
        path=regenerate_decision_fn,
        path_map={
            "pass": END,
            "fail": "regenerate_tc",    # validation 실패 시 재생성 로직 돌입
            "giveup": END               # validation - regenerate 3번 반복 시 루프 탈출
        }
    )
    
    builder.add_edge("regenerate_tc", "validate_tc")  # 루프 추가

    return builder.compile()
