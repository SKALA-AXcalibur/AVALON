from typing import List, Optional
from pydantic import BaseModel

class ApiItem(BaseModel):
    id: str
    name: str
    url: str
    path: str
    method: str
    description: Optional[str] = None

class ScenarioItem(BaseModel):
    id: str
    name: str
    description: str
    validation: str
    flowChart: Optional[str] = None
    projectKey: int

class ApiListResponse(BaseModel):
    apiList: List[ApiItem]
    scenarioList: List[ScenarioItem]
