from typing import List, Dict


def create_semantic_mapping_prompt(scenarios: List[Dict], api_lists: List[Dict]) -> str:
    """의미적 매핑을 위한 LLM 프롬프트 생성"""
    scenarios_text = format_scenarios_for_llm(scenarios)
    apis_text = format_apis_for_llm(api_lists)
    
    return f"""
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


def get_semantic_mapping_system_prompt() -> str:
    """의미적 매핑을 위한 시스템 프롬프트"""
    return "당신은 API와 시나리오 간의 의미적 연관성을 분석하는 전문가입니다. 정확한 JSON 형식으로만 응답해주세요."


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
        formatted.append(f"""
- ID: {api['id']}
- 이름: {api['name']}
- 메서드: {api['method']} {api['path']}
- 설명: {api['description']}
""")
    return "\n".join(formatted)
