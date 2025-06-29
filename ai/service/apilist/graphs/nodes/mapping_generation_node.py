import logging
from typing import Dict, Any
from ai.service.apilist.state.mapping_state import MappingState, update_mapping_generation_success, update_mapping_generation_failed
from ai.service.apilist.prompts.mapping_generation_prompt import create_mapping_generation_prompt
from ai.service.apilist.agents.map_agent import perform_semantic_mapping  # LLM 호출 재활용


def mapping_generation_node(state: MappingState) -> Dict[str, Any]:
    """
    매핑표 생성 노드
    map_node의 결과(semantic_mapping, scenarios, api_lists)를 받아 매핑표를 생성
    """
    logging.info("매핑표 생성 노드 시작")
    try:
        semantic_mapping = state.get("semantic_mapping")
        scenarios = state.get("scenarios")
        api_lists = state.get("api_lists")

        if not semantic_mapping or not scenarios or not api_lists:
            return update_mapping_generation_failed(state, "매핑표 생성에 필요한 데이터가 부족합니다.")

        # LLM 프롬프트 생성
        prompt = create_mapping_generation_prompt(semantic_mapping, scenarios, api_lists)
        # LLM 호출 (여기서는 perform_semantic_mapping 재활용 가능)
        generated_mapping_table = perform_semantic_mapping(scenarios, api_lists)  # 실제론 별도 LLM 호출 함수 필요

        # 실제론 generated_mapping_table = LLM 응답에서 추출
        return update_mapping_generation_success(state, generated_mapping_table)
    except Exception as e:
        logging.error(f"매핑표 생성 노드 에러: {str(e)}")
        return update_mapping_generation_failed(state, f"매핑표 생성 중 오류: {str(e)}")
