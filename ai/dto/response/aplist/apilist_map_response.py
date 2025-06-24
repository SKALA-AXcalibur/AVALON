from pydantic import BaseModel
from typing import List, Optional

class ApiMappingItem(BaseModel):
    scenarioId : str
    apiId : str
    step : Optional[int] = None

class ApiMappingResponse(BaseModel):
    mappings : List[ApiMappingItem]
