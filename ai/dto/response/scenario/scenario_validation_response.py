# dto/response/scenario/scenario_validation_response.py
from pydantic import BaseModel


class ScoreBasedValidationResponse(BaseModel):
    """
    점수 기반 검증 결과를 담는 DTO
    score: LLM이 평가한 종합 점수
    explanation: 점수에 대한 근거 및 개선 방안
    """

    score: int
    explanation: str
