# ai/dto/request/testcase/scenario.py
from pydantic import BaseModel, Field
from typing import Optional

"""
시나리오 객체 정의
(시나리오 이름, 시나리오 설명, 검증포인트)
"""
class Scenario(BaseModel):
    scenario_name: str = Field(..., alias="scenarioName")
    scenario_desc: str = Field(..., alias="scenarioDesc")
    validation: Optional[str] = None
    
    class Config:
        validate_by_name = True