import logging
from langgraph.graph import StateGraph, END

from state.apilist.mapping_state import MappingState
from node.apilist.map_node import map_node
from node.apilist.mapping_generation_node import mapping_generation_node
from node.apilist.mapping_validation_node import mapping_validation_node
from node.apilist.decision_node import should_regenerate

def create_apilist_graph() -> StateGraph:
    """
    의미적 매핑 및 매핑표 생성/검증을 위한 LangGraph 구성
        1. 의미적 매핑 (map)
        2. 매핑표 생성 (mapping_generation)
        3. 매핑표 검증 (mapping_validation)
    """
    logging.info("apilist 그래프 생성 시작")
    
    workflow = StateGraph(MappingState)
    
    # 노드 등록
    workflow.add_node("map", map_node)
    workflow.add_node("mapping_generation", mapping_generation_node)
    workflow.add_node("mapping_validation", mapping_validation_node)
    
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
            "complete": END,           # 완료시
            "retry": "map"              # 실패시 map 노드로 다시 이동
        }
    )
    
    graph = workflow.compile()
    logging.info("apilist 그래프 생성 완료")
    return graph
