# dto/response/scenario/scenario_response.py
from pydantic import BaseModel
from typing import List, Optional


class ApiItem(BaseModel):
    """시나리오에서 사용된 API 정보"""

    id: Optional[str] = None  # API 고유 아이디
    name: Optional[str] = None  # API 이름
    desc: Optional[str] = None  # API 설명


class Scenario(BaseModel):
    """시나리오 정보"""

    scenario_id: str  # 시나리오 고유 아이디
    title: str  # 시나리오 제목
    description: str  # 시나리오 설명
    validation: str  # 시나리오 검증 포인트
    api_list: List[ApiItem]  # 시나리오에서 사용된 API 리스트


class ScenarioResponse(BaseModel):
    """시나리오 응답 정보"""

    scenario_list: List[Scenario]
