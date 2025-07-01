from typing import List, Dict


def create_mapping_generation_prompt(semantic_mapping: Dict, scenarios: List[Dict], api_lists: List[Dict]) -> str:
    """
    매핑표 생성을 위한 LLM 프롬프트 생성 - 개선된 버전
    """
    # 간소화된 시나리오 정보
    scenarios_text = format_scenarios_simple(scenarios)
    apis_text = format_apis_simple(api_lists)
    
    return f"""
시스템 통합 전문가로서 다음 시나리오들에 대한 API 매핑표를 생성해주세요.

[시나리오 목록]
{scenarios_text}

[API 목록]
{apis_text}

각 시나리오의 validation 필드에 명시된 검증 포인트를 만족하는 API 호출 시퀀스를 생성해주세요.

**반드시 아래 JSON 구조로만 응답하세요. 다른 텍스트는 포함하지 마세요:**

{{
  "processedAt": "2024-07-01T12:34:56.789Z",
  "validationRate": 92.5,
  "apiMapping": [
    {{
      "scenarioId": "SCN-001",
      "stepName": "프로젝트 생성",
      "apiName": "프로젝트 생성",
      "description": "새 프로젝트 생성 및 쿠키 발급",
      "url": "/api/project/create",
      "method": "POST",
      "parameters": {{ "projectName": "string" }},
      "responseStructure": {{ "projectId": "string" }}
    }}
  ]
}}

**주의: JSON 끝에 모든 중괄호와 대괄호를 반드시 닫아주세요.**
"""


def format_scenarios_simple(scenarios: List[Dict]) -> str:
    """시나리오를 간단하게 포맷 - 토큰 절약"""
    formatted = []
    for scenario in scenarios:
        formatted.append(f"""
{scenario.get('scenarioId')}: {scenario.get('title')}
- 설명: {scenario.get('description', '')[:100]}...
- 검증: {scenario.get('validation', '')[:100]}...
""")
    return "\n".join(formatted)


def format_apis_simple(api_lists: List[Dict]) -> str:
    """API를 간단하게 포맷 - 토큰 절약"""
    formatted = []
    for i, api in enumerate(api_lists):
        formatted.append(f"""
API-{i+1}: {api.get('apiName')} - {api.get('description', '')[:80]}...
""")
    return "\n".join(formatted)