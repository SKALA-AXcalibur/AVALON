import logging
import redis
import os
from typing import Dict, Any, List
from anthropic import Anthropic
from dotenv import load_dotenv

from service.aplist.state.mapping_state import MappingState, update_map_success, update_map_failed
from service.aplist.repository.scenario_repository import ScenarioRepository
from service.aplist.repository.api_list_repository import ApiListRepository


# .env 파일 로드
load_dotenv()

# Redis 클라이언트 설정
redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

# Anthropic Claude 클라이언트 설정
anthropic_client = Anthropic(api_key=os.getenv('ANTHROPIC_API_KEY'))

def map_node(state: MappingState) -> Dict[str, Any]:
    """
    의미적 매핑 노드
    1. Redis에서 project_key 조회
    2. DB에서 scenarios, api_lists, parameters 조회
    3. LLM으로 의미적 매핑 분석
    """
    logging.info("의미적 매핑 노드 시작")
    
    try:
        avalon = state["avalon"]
        
        # 1. Redis에서 project_key 조회
        project_key = get_project_key_from_redis(avalon)
        if not project_key:
            return update_map_failed(state, "Redis에서 project_key 조회 실패")
        
        # 2. DB에서 데이터 조회
        scenarios = get_scenarios_by_project_key(project_key)
        api_lists = get_api_lists_by_project_key(project_key)
        
        if not scenarios or not api_lists:
            return update_map_failed(state, "시나리오 또는 API 목록이 없습니다")
        
        # 3. LLM으로 의미적 매핑 분석
        semantic_mapping = perform_semantic_mapping(scenarios, api_lists)
        
        # 4. 상태 업데이트 및 조회된 데이터 저장
        result = update_map_success(state, semantic_mapping)
        result.update({
            "project_key": project_key,
            "scenarios": scenarios,
            "api_lists": api_lists
        })
        
        logging.info("의미적 매핑 노드 완료")
        return result
        
    except Exception as e:
        logging.error(f"의미적 매핑 노드 에러: {str(e)}")
        return update_map_failed(state, f"의미적 매핑 실행 중 오류: {str(e)}")


def get_project_key_from_redis(avalon: str) -> int:
    """Redis에서 project_key 조회"""
    try:
        project_key = redis_client.get(avalon)
        if project_key:
            return int(project_key)
        return None
    except Exception as e:
        logging.error(f"Redis 조회 에러: {str(e)}")
        return None


def get_scenarios_by_project_key(project_key: int) -> List[Dict]:
    """프로젝트 키로 시나리오 조회"""
    try:
        scenario_repo = ScenarioRepository()
        scenario_entities = scenario_repo.findByProjectKey(project_key)
        
        scenarios = []
        for entity in scenario_entities:
            scenarios.append({
                "key": entity.key,
                "id": entity.id,
                "name": entity.name,
                "description": entity.description,
                "validation": entity.validation,
                "flow_chart": entity.flowChart,
                "project_key": entity.projectKey
            })
        
        return scenarios
    except Exception as e:
        logging.error(f"시나리오 조회 에러: {str(e)}")
        return []


def get_api_lists_by_project_key(project_key: int) -> List[Dict]:
    """프로젝트 키로 API 목록 조회"""
    try:
        api_list_repo = ApiListRepository()
        api_list_entities = api_list_repo.findByProjectKey(project_key)
        
        api_lists = []
        for entity in api_list_entities:
            api_lists.append({
                "key": entity.key,
                "id": entity.id,
                "name": entity.name,
                "url": entity.url,
                "path": entity.path,
                "method": entity.method,
                "description": entity.description,
                "project_key": entity.projectKey,
                "request_key": entity.requestKey,
                "parameters": entity.parameters  # API 목록에 파라미터가 포함되어 있음
            })
        
        return api_lists
    except Exception as e:
        logging.error(f"API 목록 조회 에러: {str(e)}")
        return []


def perform_semantic_mapping(scenarios: List[Dict], api_lists: List[Dict]) -> Dict[str, Any]:
    """LLM을 사용한 의미적 매핑 분석"""
    try:
        # 시나리오와 API 정보를 텍스트로 구성
        scenarios_text = format_scenarios_for_llm(scenarios)
        apis_text = format_apis_for_llm(api_lists)
        
        # LLM 프롬프트 구성
        prompt = f"""
다음 시나리오들과 API 목록 간의 의미적 연관성을 분석해주세요.

## 시나리오 목록:
{scenarios_text}

## API 목록:
{apis_text}

각 시나리오에 대해 관련된 API들을 찾아서 매핑해주세요.
결과는 다음 JSON 형식으로 반환해주세요:

{{
    "mappings": [
        {{
            "scenario_id": "SCN-001",
            "related_apis": [
                {{
                    "api_id": "IF-PR-0004",
                    "relevance_score": 0.95,
                    "reason": "프로젝트 생성 시나리오에 직접적으로 필요한 API"
                }}
            ]
        }}
    ],
    "overall_confidence": 0.85
}}
"""
        
        # Anthropic Claude API 호출
        response = anthropic_client.messages.create(
            model=os.getenv('MODEL_NAME', 'claude-sonnet-4'),
            max_tokens=4000,  # max_tokens는 고정값 사용
            temperature=float(os.getenv('MODEL_TEMPERATURE', 0.7)),
            system="당신은 API와 시나리오 간의 의미적 연관성을 분석하는 전문가입니다. 정확한 JSON 형식으로만 응답해주세요.",
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        
        # 응답 파싱
        result_text = response.content[0].text
        import json
        semantic_mapping = json.loads(result_text)
        
        return semantic_mapping
        
    except Exception as e:
        logging.error(f"의미적 매핑 분석 에러: {str(e)}")
        return {
            "mappings": [],
            "overall_confidence": 0.0,
            "error": str(e)
        }


def format_scenarios_for_llm(scenarios: List[Dict]) -> str:
    """시나리오 정보를 LLM용 텍스트로 포맷"""
    formatted = []
    for scenario in scenarios:
        formatted.append(f"""
- ID: {scenario['id']}
- 이름: {scenario['name']}
- 설명: {scenario['description']}
- 검증: {scenario.get('validation', 'N/A')}
""")
    return "\n".join(formatted)


def format_apis_for_llm(api_lists: List[Dict]) -> str:
    """API 정보를 LLM용 텍스트로 포맷"""
    formatted = []
    for api in api_lists:
        # API에 포함된 파라미터 정보 처리
        params_text = ""
        if api.get('parameters'):
            params_text = "\n  파라미터: " + ", ".join([f"{p['name']}({p['type']})" for p in api['parameters'][:5]])
        
        formatted.append(f"""
- ID: {api['id']}
- 이름: {api['name']}
- 메서드: {api['method']} {api['path']}
- 설명: {api['description']}{params_text}
""")
    return "\n".join(formatted)