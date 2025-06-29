import logging
from typing import Dict, Any
from ai.service.apilist.state.mapping_state import MappingState, update_mapping_validation_success, update_mapping_validation_failed
from ai.service.apilist.prompts.mapping_validation_prompt import create_mapping_validation_prompt
from ai.service.apilist.agents.map_agent import perform_semantic_mapping  # LLM 호출 재활용


def mapping_validation_node(state: MappingState) -> Dict[str, Any]:
    """
    매핑표 검증 노드
    mapping_generation_node의 결과(매핑표)를 받아 검증
    """
    logging.info("매핑표 검증 노드 시작")
    try:
        mapping_table = state.get("generated_mapping_table")
        if not mapping_table:
            return update_mapping_validation_failed(state, "검증할 매핑표가 없습니다.")

        # LLM 프롬프트 생성
        prompt = create_mapping_validation_prompt(mapping_table)
        # LLM 호출 (여기서는 perform_semantic_mapping 재활용 가능)
        validation_result = perform_semantic_mapping([], [])  # 실제론 별도 LLM 호출 함수 필요

        # 실제론 validation_result = LLM 응답에서 추출
        return update_mapping_validation_success(state, validation_result)
    except Exception as e:
        logging.error(f"매핑표 검증 노드 에러: {str(e)}")
        return update_mapping_validation_failed(state, f"매핑표 검증 중 오류: {str(e)}")
