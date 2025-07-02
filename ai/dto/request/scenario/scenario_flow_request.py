# dto/request/scenario/scenario_flow_request.py
from typing import List
from pydantic import BaseModel


class ApiItem(BaseModel):
    id: str  # API 고유 아이디
    name: str
    description: str


class ScenarioItem(BaseModel):
    id: str  # 시나리오 고유 아이디
    description: str  # 시나리오 설명   
    api_list: List[ApiItem] 


class ScenarioFlowRequest(BaseModel):
    scenario_list: List[ScenarioItem]
