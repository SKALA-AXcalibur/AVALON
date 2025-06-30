from typing import List, Dict


def create_mapping_validation_prompt(mapping_table: List[Dict]) -> str:
    """
    매핑표 검증을 위한 LLM 프롬프트 생성 - 개선된 버전
    """
    # 매핑표를 간소화하여 토큰 절약
    mapping_summary = format_mapping_table_simple(mapping_table)
    
    return f"""
소프트웨어 품질 보증 전문가로서 이 매핑표를 검증해주세요.

[매핑표 요약]
{mapping_summary}

시나리오의 validation 필드에 명시된 검증 포인트를 만족하는지 검증해주세요.

**반드시 다음 JSON 형식으로만 응답해주세요:**

{{
    "validation_score": 85,
    "overall_assessment": "good",
    "detailed_analysis": {{
        "business_logic_accuracy": {{
            "score": 35,
            "max_score": 40,
            "issues": ["문제점1"],
            "strengths": ["강점1"]
        }},
        "validation_requirement_satisfaction": {{
            "score": 25,
            "max_score": 30,
            "issues": ["검증 요구사항 미충족 사항"],
            "strengths": ["검증 요구사항 충족 사항"]
        }},
        "technical_consistency": {{
            "score": 18,
            "max_score": 20,
            "issues": ["기술적 문제점"],
            "strengths": ["기술적 강점"]
        }},
        "implementation_feasibility": {{
            "score": 8,
            "max_score": 10,
            "issues": ["구현 문제점"],
            "strengths": ["구현 강점"]
        }}
    }},
    "critical_issues": ["치명적 문제점 목록"],
    "improvement_suggestions": ["개선 제안 목록"],
    "recommendation": "pass",
    "confidence_level": 0.92
}}

**주의: JSON 끝에 모든 중괄호와 대괄호를 반드시 닫아주세요.**
"""


def format_mapping_table_simple(mapping_table: List[Dict]) -> str:
    """매핑표를 간단하게 포맷 - 토큰 절약"""
    if not mapping_table:
        return "매핑표가 비어있음"
    
    formatted = []
    for i, mapping in enumerate(mapping_table):
        if i >= 10:  # 처음 10개만 표시
            formatted.append("... (나머지 생략)")
            break
            
        formatted.append(f"""
{mapping.get('scenarioId')}: {mapping.get('stepName')} -> {mapping.get('apiName')}
- URI: {mapping.get('uri')} ({mapping.get('method')})
- 설명: {mapping.get('description', '')[:50]}...
""")
    return "\n".join(formatted)


def create_quick_validation_prompt(mapping_count: int, scenario_count: int) -> str:
    """
    빠른 검증을 위한 간소화된 프롬프트
    """
    return f"""
매핑표 검증: {mapping_count}개 매핑, {scenario_count}개 시나리오

다음 기준으로 평가하세요:
1. 비즈니스 로직 정확성 (40점)
2. 검증 요구사항 충족 (30점)  
3. 기술적 일관성 (20점)
4. 구현 가능성 (10점)

**JSON으로만 응답:**

{{
    "validation_score": 85,
    "overall_assessment": "good",
    "recommendation": "pass",
    "confidence_level": 0.92,
    "critical_issues": [],
    "improvement_suggestions": []
}}
"""


def create_detailed_validation_prompt(mapping_table: List[Dict], scenarios: List[Dict]) -> str:
    """
    상세 검증을 위한 프롬프트 (필요시 사용)
    """
    scenario_validations = {}
    for scenario in scenarios:
        scenario_validations[scenario.get('scenarioId')] = scenario.get('validation', '')
    
    mapping_by_scenario = {}
    for mapping in mapping_table:
        scenario_id = mapping.get('scenarioId')
        if scenario_id not in mapping_by_scenario:
            mapping_by_scenario[scenario_id] = []
        mapping_by_scenario[scenario_id].append(mapping)
    
    analysis_text = []
    for scenario_id, mappings in mapping_by_scenario.items():
        validation_req = scenario_validations.get(scenario_id, '')
        analysis_text.append(f"""
{scenario_id}: {len(mappings)}개 API 매핑
검증 요구사항: {validation_req[:100]}...
매핑된 API들: {', '.join([m.get('apiName', '') for m in mappings[:3]])}...
""")
    
    return f"""
매핑표 상세 검증을 수행하세요.

{chr(10).join(analysis_text)}

각 시나리오의 검증 요구사항이 매핑된 API들로 충족되는지 평가하세요.

**JSON 응답:**
{{
    "validation_score": 85,
    "detailed_analysis": {{
        "business_logic_accuracy": {{"score": 35, "max_score": 40}},
        "validation_requirement_satisfaction": {{"score": 25, "max_score": 30}},
        "technical_consistency": {{"score": 18, "max_score": 20}},
        "implementation_feasibility": {{"score": 8, "max_score": 10}}
    }},
    "recommendation": "pass"
}}
"""