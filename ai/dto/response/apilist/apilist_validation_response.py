from typing import List
from pydantic import BaseModel
from dto.request.apilist.common import ApiMappingItem

class ApiListValidationResponse(BaseModel):
    processedAt: str
    validationRate: float
    apiMapping: List[ApiMappingItem]
