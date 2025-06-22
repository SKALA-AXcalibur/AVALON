from typing import List
from pydantic import BaseModel

class ApiItem(BaseModel):
    id: str
    name: str
    description: str

class ScenarioItem(BaseModel):
    id: str
    description: str
    api_list: List[ApiItem] 

class ScenarioFlowRequest(BaseModel):
    scenario_list: List[ScenarioItem]