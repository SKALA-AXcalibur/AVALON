from typing import List, Dict, Literal
from pydantic import BaseModel, Field


class ScenarioScores(BaseModel):
    """시나리오별 점수"""
    completeness: int = Field(..., ge=0, le=25, description="완전성 점수 (0-25)")
    independence: int = Field(..., ge=0, le=20, description="독립성 점수 (0-20)")
    practicality: int = Field(..., ge=0, le=25, description="실무성 점수 (0-25)")
    api_mapping: int = Field(..., ge=0, le=20, description="API 매핑 점수 (0-20)")
    validation_quality: int = Field(..., ge=0, le=10, description="검증 품질 점수 (0-10)")


class ScenarioValidationDetail(BaseModel):
    """개별 시나리오 검증 결과"""
    scenario_id: str = Field(..., description="시나리오 ID")
    scores: ScenarioScores = Field(..., description="세부 점수")
    total_score: int = Field(..., ge=0, le=100, description="총점")
    status: Literal["pass", "review_required", "fail"] = Field(..., description="판정 결과")
    critical_issues: List[str] = Field(default_factory=list, description="치명적 이슈")
    major_issues: List[str] = Field(default_factory=list, description="주요 이슈")
    suggestions: List[str] = Field(default_factory=list, description="개선 제안")


class ValidationResult(BaseModel):
    """전체 검증 결과"""
    overall_score: float = Field(..., ge=0, le=100, description="전체 평균 점수")
    overall_status: Literal["pass", "review_required", "fail"] = Field(..., description="전체 판정")
    scenarios: List[ScenarioValidationDetail] = Field(..., description="시나리오별 검증 결과")
    global_issues: List[str] = Field(default_factory=list, description="전역 이슈")
    improvement_priority: List[str] = Field(default_factory=list, description="개선 우선순위")


class ScenarioValidationResponse(BaseModel):
    """검증 응답 DTO"""
    validation_result: ValidationResult = Field(..., description="검증 결과")
    
    @property
    def is_pass(self) -> bool:
        """통과 여부 확인"""
        return self.validation_result.overall_status == "pass"
    
    @property
    def needs_improvement(self) -> bool:
        """개선 필요 여부 확인"""
        return self.validation_result.overall_status in ["review_required", "fail"]
    
    def get_feedback_summary(self) -> str:
        """피드백 요약 생성"""
        result = self.validation_result
        feedback_parts = []
        
        # 전체 점수 및 상태
        feedback_parts.append(f"전체 점수: {result.overall_score:.1f}/100 ({result.overall_status})")
        
        # 주요 이슈들
        if result.global_issues:
            feedback_parts.append("전역 이슈:")
            feedback_parts.extend([f"- {issue}" for issue in result.global_issues])
        
        # 개선 우선순위
        if result.improvement_priority:
            feedback_parts.append("개선 우선순위:")
            feedback_parts.extend([f"- {priority}" for priority in result.improvement_priority])
        
        # 시나리오별 주요 이슈
        critical_scenarios = [s for s in result.scenarios if s.critical_issues or s.major_issues]
        if critical_scenarios:
            feedback_parts.append("시나리오별 주요 이슈:")
            for scenario in critical_scenarios:
                feedback_parts.append(f"[{scenario.scenario_id}]")
                for issue in scenario.critical_issues:
                    feedback_parts.append(f"  치명적: {issue}")
                for issue in scenario.major_issues:
                    feedback_parts.append(f"  주요: {issue}")
        
        return "\n".join(feedback_parts)