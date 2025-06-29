from typing import List, Dict

def create_mapping_generation_prompt(semantic_mapping: Dict, scenarios: List[Dict], api_lists: List[Dict]) -> str:
    """
    매핑표 생성을 위한 LLM 프롬프트 생성
    """
    return f"""
당신은 엔터프라이즈 시스템 통합과 API 설계 분야에서 25년 이상의 경험을 가진 최고 수준의 
시스템 통합 아키텍트입니다. 특히 복잡한 비즈니스 프로세스를 정확한 API 호출 시퀀스로 
변환하는 능력이 탁월하며, 마이크로서비스 환경에서의 시스템 간 연동 설계 전문가입니다.

아래는 시나리오-API 의미적 매핑 결과입니다.

[의미적 매핑 결과]
{semantic_mapping}

[시나리오 목록]
{scenarios}

[API 목록]
{api_lists}

위 정보를 바탕으로, 각 시나리오별로 실제로 호출해야 할 API와 파라미터를 포함한 
상세한 매핑표를 생성해주세요.

특히 각 시나리오의 validation 필드에 명시된 검증 포인트를 반드시 포함하여,
해당 검증 요구사항을 만족하는 완전한 API 호출 시퀀스를 구성해주세요.

매핑표 생성 시 다음 요소들을 철저히 고려하세요:
1. API 호출 순서의 논리적 정합성
2. 시나리오 validation 요구사항의 완전한 구현
3. 데이터 의존성과 전달 관계
4. 에러 처리 및 예외 상황 대응
5. 성능 최적화를 위한 호출 최적화
6. 비즈니스 로직의 완전성 보장

생성된 매핑표는 실제 개발팀이 바로 구현할 수 있을 정도로 상세하고 정확해야 하며,
시나리오의 모든 검증 포인트가 API 호출로 적절히 구현되어야 합니다.

다음 JSON 형식으로 응답해주세요:
{{
    "mapping_table": [
        {{
            "scenario_id": "SCN-001",
            "scenario_name": "시나리오명",
            "scenario_validation": "시나리오 검증 포인트",
            "execution_sequence": [
                {{
                    "step": 1,
                    "api_id": "API-001",
                    "api_name": "API명",
                    "method": "POST",
                    "url": "/api/endpoint",
                    "parameters": {{
                        "param1": "value1",
                        "param2": "value2"
                    }},
                    "expected_response": {{
                        "status": 200,
                        "data_structure": "응답 데이터 구조"
                    }},
                    "error_handling": "에러 처리 방법",
                    "business_logic": "이 단계의 비즈니스 목적",
                    "validation_contribution": "이 API 호출이 시나리오 검증에 기여하는 부분"
                }}
            ],
            "total_steps": 5,
            "estimated_duration": "예상 실행 시간",
            "critical_path": "핵심 실행 경로",
            "validation_coverage": "검증 포인트 커버리지"
        }}
    ],
    "overall_quality_score": 0.95,
    "implementation_ready": true,
    "risk_assessment": "구현 시 주의사항",
    "validation_satisfaction": 0.92
}}
"""
