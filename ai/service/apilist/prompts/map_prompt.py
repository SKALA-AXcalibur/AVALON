from typing import List, Dict


def create_semantic_mapping_prompt(scenarios: List[Dict], api_lists: List[Dict]) -> str:
    """의미적 매핑을 위한 LLM 프롬프트 생성"""
    scenarios_text = format_scenarios_for_llm(scenarios)
    apis_text = format_apis_for_llm(api_lists)
    
    return f"""
당신은 API 설계와 비즈니스 프로세스 분석 전문가입니다.

다음 시나리오들과 API 목록 간의 의미적 연관성을 분석해주세요.
이 배치에는 {len(scenarios)}개의 시나리오가 포함되어 있습니다.

## 시나리오 목록:
{scenarios_text}

## API 목록:
{apis_text}

각 시나리오에 필요한 API들을 매핑해주세요.
반드시 완전한 JSON 형식으로 응답해주세요.

결과는 다음 JSON 형식으로 반환해주세요:

{{
    "mappings": [
        {{
            "scenario_id": "SCN-001",
            "scenario_validation": "시나리오 검증 포인트",
            "related_apis": [
                {{
                    "api_id": "API-001",
                    "relevance_score": 0.95,
                    "reason": "필요한 이유",
                    "confidence_level": "high",
                    "business_impact": "critical",
                    "validation_coverage": "검증 충족도"
                }}
            ]
        }}
    ],
    "overall_confidence": 0.85
}}

중요: JSON 응답을 완전히 마무리해주세요. 중간에 끊기지 않도록 주의해주세요.
"""

def get_semantic_mapping_system_prompt() -> str:
    """의미적 매핑을 위한 시스템 프롬프트"""
    return """당신은 API 설계와 비즈니스 프로세스 분석 분야의 최고 전문가입니다. 
20년 이상의 엔터프라이즈 시스템 설계 경험을 바탕으로, 복잡한 비즈니스 요구사항을 
정확하고 효율적인 API 설계로 변환하는 능력이 탁월합니다.

특히 다음 분야에서 깊은 전문성을 보유하고 있습니다:
- 마이크로서비스 아키텍처 설계
- API-비즈니스 로직 매핑
- 시스템 통합 및 데이터 흐름 최적화
- 엔터프라이즈급 소프트웨어 아키텍처
- 비즈니스 검증 요구사항 분석 및 구현

항상 정확한 JSON 형식으로만 응답하며, 비즈니스 관점에서의 실용성과 
기술적 정확성을 모두 고려한 분석을 제공합니다."""


def format_scenarios_for_llm(scenarios: List[Dict]) -> str:
    """시나리오 정보를 LLM용 텍스트로 포맷"""
    formatted = list(map(lambda scenario: f"""
- ID: {scenario.get('scenarioId', scenario.get('scenario_id', scenario.get('id', 'N/A')))}
- 이름: {scenario.get('title', scenario.get('name', 'N/A'))}
- 설명: {scenario.get('description', 'N/A')}
- 검증 포인트: {scenario.get('validation', 'N/A')}
""", scenarios))
    return "\n".join(formatted)


def format_apis_for_llm(api_lists: List[Dict]) -> str:
    """API 정보를 LLM용 텍스트로 포맷"""
    formatted = list(map(lambda api: f"""
- 이름: {api.get('apiName', 'N/A')}
- uri: {api.get('uri', 'N/A')}
- 메서드: {api.get('method', 'N/A')}
- 설명: {api.get('description', 'N/A')}
- 파라미터: {api.get('parameters', 'N/A')}
- 응답 구조: {api.get('responseStructure', 'N/A')}
""", api_lists))
    return "\n".join(formatted)