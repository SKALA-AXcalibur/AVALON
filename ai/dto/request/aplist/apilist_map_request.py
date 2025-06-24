from pydantic import BaseModel
from typing import List
class ApiListMapRequest(BaseModel):
    token : str
    apiList : List[str]
    scenarioList : List[str]