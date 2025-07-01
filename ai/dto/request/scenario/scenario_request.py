# dto/request/scenario/scenario_request.py
from pydantic import BaseModel
from typing import List


class ApiItem(BaseModel):
    id: str
    name: str
    desc: str
    method: str
    path: str


class RequirementItem(BaseModel):
    name: str
    desc: str
    major: str
    middle: str
    minor: str


class ScenarioRequest(BaseModel):
    project_id: str
    requirement: List[RequirementItem]
    api_list: List[ApiItem]
