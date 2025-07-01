from typing import List, Optional
from pydantic import BaseModel

class ApiItem(BaseModel):
    apiName: str
    url: str
    method: str
    description: Optional[str] = None
    parameters: Optional[str] = None
    responseStructure: Optional[str] = None
    
class ScenarioItem(BaseModel):
    scenarioId: str
    title: str
    description: str
    validation: str
    
class ApiListResponse(BaseModel):
    apiList: List[ApiItem]
    scenarioList: List[ScenarioItem]
