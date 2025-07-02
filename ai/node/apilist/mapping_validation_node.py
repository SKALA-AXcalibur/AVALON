import logging
from typing import Dict, Any
from state.apilist.mapping_state_processor import MappingState, update_mapping_validation_success, update_mapping_validation_failed
from service.apilist.mapping_validator import perform_mapping_validation


def mapping_validation_node(state: MappingState) -> Dict[str, Any]:
    """
    매핑표 검증 노드
    mapping_generation_node의 결과(매핑표)를 받아 검증
    """
    logging.info("매핑표 검증 노드 시작")
    try:
        mapping_table = state.generated_mapping_table
        if not mapping_table:
            return update_mapping_validation_failed(state, "검증할 매핑표가 없습니다.")

        # 매핑표 검증 LLM 호출
        validation_result = perform_mapping_validation(mapping_table)

        return update_mapping_validation_success(state, validation_result)
    except Exception as e:
        logging.error(f"매핑표 검증 노드 에러: {str(e)}")
        return update_mapping_validation_failed(state, f"매핑표 검증 중 오류: {str(e)}")
