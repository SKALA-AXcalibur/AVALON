from typing import List
from pydantic import BaseModel
from datetime import datetime
from dto.request.apilist.common import ApiMappingItem


class ApiListMapResponse(BaseModel):
    processedAt: datetime
    validationRate: float
    apiMapping: List[ApiMappingItem]
