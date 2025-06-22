# service/scenario/graphs/scenario_graph.py
import logging
from typing import Dict, Any
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
    logging.info("시나리오 그래프 생성 시작")
    
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
    
    logging.info("시나리오 그래프 생성 완료")
    return graph


# def get_workflow_status(state: Dict[str, Any]) -> Dict[str, Any]:
#     """
#     현재까지의 시나리오 워크플로우 실행 상태를 요약
#     """
#     return {
#         "current_step": state.get("current_step", "unknown"), # 현재 단계
#         "attempt_count": state.get("attempt_count", 0), # 시도 횟수
#         "generation_status": state.get("generation_status", "unknown"), # 생성 상태
#         "validation_status": state.get("validation_status", "unknown"), # 검증 상태
#         "overall_score": state.get("overall_score", 0), # 점수
#         "has_error": "error_message" in state, # 에러 여부
#         "is_completed": state.get("current_step") in ["validation_completed", "generation_failed", "validation_failed"] # 완료 여부
#     }