import logging
import requests
from typing import Dict, Any
import os
from dotenv import load_dotenv

from ai.service.apilist.state.mapping_state import MappingState, update_map_success, update_map_failed
from ai.service.apilist.agents.map_agent import perform_semantic_mapping

# .env 파일 로드
load_dotenv()

# Java 백엔드 API 설정
JAVA_API_BASE_URL = os.getenv('JAVA_API_BASE_URL', 'http://localhost:8080')


def map_node(state: MappingState) -> Dict[str, Any]:
    """
    의미적 매핑 노드
    백엔드의 기존 매핑 서비스를 활용하여 데이터 조회 후 LLM으로 의미적 매핑 분석
    """
    logging.info("의미적 매핑 노드 시작")
    
    try:
        avalon = state["avalon"]
        
        # 백엔드의 기존 매핑 API 호출하여 데이터 조회
        mapping_data = get_mapping_data_from_backend(avalon)
        if not mapping_data:
            return update_map_failed(state, "백엔드에서 매핑 데이터 조회 실패")
        
        scenarios = mapping_data.get('scenarios', [])
        api_lists = mapping_data.get('apiList', [])
        
        if not scenarios or not api_lists:
            return update_map_failed(state, "시나리오 또는 API 목록이 없습니다")
        
        # LLM으로 의미적 매핑 분석
        semantic_mapping = perform_semantic_mapping(scenarios, api_lists)
        
        # 상태 업데이트 및 조회된 데이터 저장
        result = update_map_success(state, semantic_mapping)
        result.update({
            "project_key": mapping_data.get('projectKey'),
            "scenarios": scenarios,
            "api_lists": api_lists
        })
        
        logging.info("의미적 매핑 노드 완료")
        return result
        
    except Exception as e:
        logging.error(f"의미적 매핑 노드 에러: {str(e)}")
        return update_map_failed(state, f"의미적 매핑 실행 중 오류: {str(e)}")


def get_mapping_data_from_backend(avalon: str) -> Dict[str, Any]:
    """백엔드의 기존 매핑 API 호출"""
    try:
        # 백엔드의 getApiMappingList API 호출
        response = requests.get(f"{JAVA_API_BASE_URL}/api/list/v1/mapping", 
                              params={'avalon': avalon})
        if response.status_code == 200:
            return response.json()
        return None
    except Exception as e:
        logging.error(f"백엔드 매핑 데이터 조회 에러: {str(e)}")
        return None