import logging
from typing import Dict, Any
from state.apilist.mapping_state_processor import MappingState, update_mapping_generation_success, update_mapping_generation_failed
from service.apilist.mapping_generator import perform_mapping_generation


def mapping_generation_node(state: MappingState) -> Dict[str, Any]:
    """
    매핑표 생성 노드
    map_node의 결과(semantic_mapping, scenarios, api_lists)를 받아 매핑표를 생성
    """
    logging.info("매핑표 생성 노드 시작")
    try:
        semantic_mapping = state.semantic_mapping
        scenarios = state.scenarios
        api_lists = state.api_lists

        if not semantic_mapping or not scenarios or not api_lists:
            return update_mapping_generation_failed(state, "매핑표 생성에 필요한 데이터가 부족합니다.")

        # 매핑표 생성 LLM 호출
        generated_mapping_table = perform_mapping_generation(scenarios, api_lists)

        # LLM 응답이 mappings 구조일 경우 apiMapping 구조로 변환
        def convert_llm_mappings_to_api_mapping(mappings, api_list):
            api_dict = {api.get('id') or api.get('api_id'): api for api in api_list}
            api_mapping = []
            for mapping in mappings:
                scenario_id = mapping.get('scenario_id')
                for related in mapping.get('related_apis', []):
                    api_id = related.get('api_id')
                    api_info = api_dict.get(api_id, {})
                    api_mapping.append({
                        "scenarioId": scenario_id,
                        "stepName": api_info.get("stepName", api_info.get("apiName", "")),
                        "apiName": api_info.get("apiName", ""),
                        "description": api_info.get("description", ""),
                        "url": api_info.get("url", ""),
                        "method": api_info.get("method", ""),
                        "parameters": api_info.get("parameters", ""),
                        "responseStructure": api_info.get("responseStructure", "")
                    })
            return api_mapping

        # 변환 적용
        if isinstance(generated_mapping_table, dict):
            if "apiMapping" in generated_mapping_table:
                api_mapping = generated_mapping_table["apiMapping"]
            elif "mappings" in generated_mapping_table:
                api_mapping = convert_llm_mappings_to_api_mapping(generated_mapping_table["mappings"], api_lists)
            else:
                api_mapping = []
        elif isinstance(generated_mapping_table, list):
            api_mapping = generated_mapping_table
        else:
            api_mapping = []

        return update_mapping_generation_success(state, api_mapping)
    except Exception as e:
        logging.error(f"매핑표 생성 노드 에러: {str(e)}")
        return update_mapping_generation_failed(state, f"매핑표 생성 중 오류: {str(e)}")