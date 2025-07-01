from typing import List, Dict, Any
from pydantic import BaseModel
from datetime import datetime

class ApiMappingItem(BaseModel):
    scenarioId: str
    stepName: str
    apiName: str
    description: str
    url: str
    method: str
    parameters: Dict[str, Any]
    responseStructure: Dict[str, Any]

class ApiListValidationResponse(BaseModel):
    processedAt: str
    validationRate: float
    apiMapping: List[ApiMappingItem]
