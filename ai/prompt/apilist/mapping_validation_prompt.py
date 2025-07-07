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
- URL: {mapping.get('url')} ({mapping.get('method')})
- 설명: {mapping.get('description', '')[:50]}...
""")
    return "\n".join(formatted)
