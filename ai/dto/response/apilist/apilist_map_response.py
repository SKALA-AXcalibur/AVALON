from typing import List, Dict, Any
from pydantic import BaseModel
from datetime import datetime

class ApiMappingItem(BaseModel):
    scenarioId: str
    stepName: str
    apiName: str
    description: str
    uri: str
    method: str
    parameters: Dict[str, Any]
    responseStructure: Dict[str, Any]

class ApiListMapResponse(BaseModel):
    processedAt: datetime
    validationRate: float
    apiMapping: List[ApiMappingItem]
