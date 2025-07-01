import logging
from typing import Dict, Any

from service.apilist.state.mapping_state import MappingState, update_map_success, update_map_failed
from service.apilist.agents.map_agent import perform_semantic_mapping


def map_node(state: MappingState) -> Dict[str, Any]:
    """
    의미적 매핑 노드
    백엔드의 기존 매핑 서비스를 활용하여 데이터 조회 후 LLM으로 의미적 매핑 분석
    """
    logging.info("의미적 매핑 노드 시작")
    
    try:
        # 재생성 시에는 원본 데이터 사용, 처음에는 입력 데이터 사용
        scenarios = state.get('scenarios', []) or state.get('original_scenarios', [])
        api_lists = state.get('api_lists', []) or state.get('original_api_lists', [])

        if not scenarios or not api_lists:
            return update_map_failed(state, "시나리오 또는 API 목록이 없습니다")

        semantic_mapping = perform_semantic_mapping(scenarios, api_lists)
        
        # 원본 데이터를 state에 보존
        result = update_map_success(state, semantic_mapping)
        result.update({
            "scenarios": scenarios,
            "api_lists": api_lists,
            "original_scenarios": scenarios,  # 원본 데이터 보존
            "original_api_lists": api_lists   # 원본 데이터 보존
        })
        return result

    except Exception as e:
        logging.error(f"의미적 매핑 노드 에러: {str(e)}")
        return update_map_failed(state, f"의미적 매핑 실행 중 오류: {str(e)}")

