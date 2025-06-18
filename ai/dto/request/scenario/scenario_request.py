from pydantic import BaseModel
from typing import List

class Req(BaseModel):
    name: str
    desc: str
    major: str
    middle: str
    minor: str

class Api(BaseModel):
    id: int
    name: str
    desc: str
    method: str
    url: str
    path: str

class Table(BaseModel):
    name: str
    col_name: str
    type: str
    isNull: bool
    isPk: bool
    fk: str

class ScenarioRequest(BaseModel):
    projectId: str
    reqList: List[Req]
    apiList: List[Api]
    tableList: List[Table]
