from typing import List, Dict

def create_mapping_validation_prompt(mapping_table: List[Dict]) -> str:
    """
    매핑표 검증을 위한 LLM 프롬프트 생성
    """
    return f"""
당신은 소프트웨어 품질 보증과 시스템 검증 분야에서 30년 이상의 경험을 가진 
최고 수준의 품질 보증 전문가입니다. 특히 엔터프라이즈 시스템의 API 설계 검증과 
비즈니스 로직 정확성 검증 분야에서 국제적으로 인정받는 전문가로서, 
복잡한 시스템의 품질을 정확하게 평가하는 능력이 탁월합니다.

아래는 시나리오-API 매핑표입니다.

[매핑표]
{mapping_table}

이 매핑표가 시나리오와 API의 목적에 맞게 잘 매핑되어 있는지 
엄격한 품질 기준에 따라 종합적으로 검증해주세요.

특히 각 시나리오의 validation 필드에 명시된 검증 포인트가 
매핑표의 API 호출 시퀀스에서 완전히 구현되고 있는지를 중점적으로 검증해주세요.

검증 시 다음 항목들을 세밀하게 분석하세요:

1. **비즈니스 로직 정확성 (40점)**
   - 시나리오의 비즈니스 목적이 API 호출로 정확히 구현되는가
   - 시나리오의 validation 요구사항이 모든 API 호출에서 충족되는가
   - API 호출 순서가 비즈니스 프로세스와 일치하는가
   - 누락된 비즈니스 단계가 있는가

2. **검증 요구사항 충족도 (30점)**
   - 시나리오의 validation 포인트가 적절한 API로 구현되는가
   - 검증에 필요한 모든 데이터가 API 호출에서 수집되는가
   - 검증 로직이 올바른 순서로 실행되는가
   - 검증 결과가 적절히 처리되는가

3. **기술적 정합성 (20점)**
   - API 파라미터와 데이터 타입이 올바른가
   - API 응답 처리가 적절한가
   - 에러 처리 로직이 충분한가

4. **구현 가능성 (10점)**
   - 실제 개발팀이 구현할 수 있는 수준인가
   - 명확하고 모호하지 않은가

다음 JSON 형식으로 검증 결과를 반환해주세요:

{{
    "validation_score": 85,
    "overall_assessment": "good",
    "detailed_analysis": {{
        "business_logic_accuracy": {{
            "score": 35,
            "max_score": 40,
            "issues": ["문제점1", "문제점2"],
            "strengths": ["강점1", "강점2"]
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
    "validation_coverage_analysis": {{
        "covered_validation_points": ["충족된 검증 포인트 목록"],
        "missing_validation_points": ["누락된 검증 포인트 목록"],
        "coverage_percentage": 85
    }},
    "critical_issues": ["치명적 문제점 목록"],
    "improvement_suggestions": ["개선 제안 목록"],
    "recommendation": "pass|fail|retry",
    "confidence_level": 0.92
}}
"""
