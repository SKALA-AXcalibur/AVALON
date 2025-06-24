import logging
from langgraph.graph import StateGraph, END

from service.aplist.state.mapping_state import MappingState
from service.aplist.graphs.nodes.map_node import map_node
from service.aplist.graphs.nodes.mapping_generation_node import mapping_generation_node
from service.aplist.graphs.nodes.mapping_validation_node import mapping_validation_node
from service.aplist.graphs.nodes.decision_node import should_regenerate
from service.aplist.graphs.nodes.feedback_node import feedback_node

def create_apilist_graph() -> StateGraph:
    """
    의미적 매핑 및 매핑표 생성/검증을 위한 LangGraph 구성
        1. 의미적 매핑 (map)
        2. 매핑표 생성 (mapping_generation)
        3. 매핑표 검증 (mapping_validation)
        4. 피드백 반영 후 재생성 (feedback)
    """
    logging.info("apilist 그래프 생성 시작")
    
    workflow = StateGraph(MappingState)
    
    # 노드 등록
    workflow.add_node("map", map_node)
    workflow.add_node("mapping_generation", mapping_generation_node)
    workflow.add_node("mapping_validation", mapping_validation_node)
    workflow.add_node("feedback", feedback_node)
    
    # 시작점
    workflow.set_entry_point("map")
    
    # 순차 실행
    workflow.add_edge("map", "mapping_generation")
    workflow.add_edge("mapping_generation", "mapping_validation")
    
    # 조건부 분기 (검증 결과에 따라)
    workflow.add_conditional_edges(
        "mapping_validation",
        should_regenerate,
        {
            "regenerate": "feedback",  # 재생성 필요시
            "complete": END,           # 완료시
            "failed": END              # 실패시
        }
    )
    
    # 피드백 후 다시 매핑부터 재시작
    workflow.add_edge("feedback", "map")
    
    graph = workflow.compile()
    logging.info("apilist 그래프 생성 완료")
    return graph
