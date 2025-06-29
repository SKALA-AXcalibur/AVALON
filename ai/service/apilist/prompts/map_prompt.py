from typing import List, Dict


def create_semantic_mapping_prompt(scenarios: List[Dict], api_lists: List[Dict]) -> str:
    """의미적 매핑을 위한 LLM 프롬프트 생성"""
    scenarios_text = format_scenarios_for_llm(scenarios)
    apis_text = format_apis_for_llm(api_lists)
    
    return f"""
당신은 API 설계와 비즈니스 프로세스 분석 분야에서 20년 이상의 경험을 가진 최고 수준의 시스템 아키텍트입니다. 
특히 마이크로서비스 아키텍처 설계와 API-비즈니스 로직 매핑 분야에서 국제적으로 인정받는 전문가로서, 
복잡한 비즈니스 요구사항을 정확한 API 설계로 변환하는 능력이 탁월합니다.

다음 시나리오들과 API 목록 간의 의미적 연관성을 분석해주세요.

## 시나리오 목록:
{scenarios_text}

## API 목록:
{apis_text}

각 시나리오의 비즈니스 목적과 API의 기능적 특성을 깊이 있게 분석하여, 
가장 적합한 매핑 관계를 도출해주세요. 

특히 시나리오의 validation 필드에 명시된 검증 포인트를 중점적으로 고려하여,
해당 검증 요구사항을 만족하는 API들이 올바르게 매핑되도록 해주세요.

분석 시 다음 요소들을 종합적으로 고려하세요:
1. 비즈니스 로직의 일관성과 완성도
2. 시나리오의 validation 요구사항과 API 기능의 매칭도
3. 데이터 흐름의 논리적 정합성
4. API의 기능적 범위와 시나리오 요구사항의 매칭도
5. 시스템 아키텍처 관점에서의 최적성

결과는 다음 JSON 형식으로 반환해주세요:

{{
    "mappings": [
        {{
            "scenario_id": "SCN-001",
            "scenario_validation": "시나리오 검증 포인트",
            "related_apis": [
                {{
                    "api_id": "IF-PR-0004",
                    "relevance_score": 0.95,
                    "reason": "프로젝트 생성 시나리오에 직접적으로 필요한 API",
                    "confidence_level": "high",
                    "business_impact": "critical",
                    "validation_coverage": "해당 API가 시나리오 검증을 얼마나 충족하는지"
                }}
            ]
        }}
    ],
    "overall_confidence": 0.85,
    "analysis_quality": "excellent",
    "mapping_coverage": 0.92,
    "validation_satisfaction": 0.88
}}
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
    formatted = []
    for scenario in scenarios:
        formatted.append(f"""
- ID: {scenario['id']}
- 이름: {scenario['name']}
- 설명: {scenario['description']}
- 검증 포인트: {scenario.get('validation', 'N/A')}
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
