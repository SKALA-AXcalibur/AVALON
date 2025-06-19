# ai/dto/request/testcase/scenario.py
from pydantic import BaseModel
from typing import Optional

"""
시나리오 객체 정의
(시나리오 이름, 시나리오 설명, 검증포인트)
"""
class Scenario(BaseModel):
    scenario_name: str
    scenario_desc: str
    validation: Optional[str] = None