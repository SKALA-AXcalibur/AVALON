from typing import Dict, Any
from pydantic import BaseModel


class ApiMappingItem(BaseModel):
    """API 매핑 아이템 공통 DTO"""
    scenarioId: str
    stepName: str
    apiName: str
    description: str
    url: str
    method: str
    parameters: Dict[str, Any]
    responseStructure: Dict[str, Any] 