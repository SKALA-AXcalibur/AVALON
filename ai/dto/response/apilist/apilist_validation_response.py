from typing import List, Optional
from pydantic import BaseModel

class ApiMappingValidationItem(BaseModel):
    scenarioId: str
    apiId: str
    isValid: bool
    score: float
    message: Optional[str] = None

class ApiListValidationResponse(BaseModel):
    validationRate: float
    details: List[ApiMappingValidationItem]
    needsRegeneration: bool
    retryCount: int
    maxRetries: int
    errorMessage: Optional[str] = None
