# service/scenario/graphs/scenario_graph.py
from langgraph.graph import StateGraph, END

from service.scenario.state.scenario_state import ScenarioState
from service.scenario.graphs.nodes.scenario_generate_node import scenario_generate_node
from service.scenario.graphs.nodes.scenario_validate_node import scenario_validate_node
from service.scenario.graphs.nodes.decision_node import should_regenerate
from service.scenario.graphs.nodes.feedback_preparation_node import feedback_preparation_node


def create_scenario_graph() -> StateGraph:
    """
    시나리오 생성 및 검증을 위한 LangGraph 구성
        1. 시나리오 생성 (generation)
        2. 시나리오 검증 (validation)
        3. 피드백 반영 후 재생성 
    """
    # StateGraph 초기화
    workflow = StateGraph(ScenarioState)
    
    # 노드 등록
    workflow.add_node("generation", scenario_generate_node)
    workflow.add_node("validation", scenario_validate_node)
    workflow.add_node("feedback_preparation", feedback_preparation_node)
    
    # 시작점 설정
    workflow.set_entry_point("generation")
    
    # 기본 엣지 (순차 실행)
    workflow.add_edge("generation", "validation")
    
    # 조건부 엣지 (검증 결과에 따른 분기)
    workflow.add_conditional_edges(
        "validation",
        should_regenerate,  # 조건 함수
        {
            "regenerate": "feedback_preparation",  # 재생성 필요시
            "complete": END,                       # 완료시
            "failed": END                          # 실패시
        }
    )
    
    # 피드백 준비 후 다시 생성으로
    workflow.add_edge("feedback_preparation", "generation")
    
    # 그래프 컴파일
    graph = workflow.compile()

    return graph