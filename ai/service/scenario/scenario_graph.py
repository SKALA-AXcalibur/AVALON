# service/scenario/scenario_graph.py
from langgraph.graph import StateGraph, END

from state.scenario_state import ScenarioState
from node.scenario.scenario_generate_node import scenario_generate_node
from node.scenario.scenario_validate_node import scenario_validate_node
from node.scenario.decision_node import decision_node


def get_decision_result(state: ScenarioState) -> str:
    """decision 노드의 결과를 상태에서 읽어오는 함수"""
    return state.get("next_step", "end")


def create_scenario_graph() -> StateGraph:
    """
    시나리오 생성 및 검증을 위한 LangGraph 구성
        1. 시나리오 생성 (generation)
        2. 시나리오 검증 (validation)
        3. 검증 결과에 따라 생성으로 피드백 루프 또는 종료
    """
    workflow = StateGraph(ScenarioState)

    workflow.add_node("generation", scenario_generate_node)
    workflow.add_node("validation", scenario_validate_node)
    workflow.add_node("decision", decision_node)

    workflow.set_entry_point("generation")

    workflow.add_edge("generation", "validation")
    workflow.add_edge("validation", "decision")

    workflow.add_conditional_edges(
        "decision",  # 결과에 따라 피드백 루프 또는 종료
        get_decision_result,
        {
            "regenerate": "generation",  # 피드백 루프
            "end": END,  # 종료
        },
    )

    graph = workflow.compile()
    return graph
