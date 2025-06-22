from pydantic import BaseModel
from typing import List, Optional


class ParameterItem(BaseModel):
    name_ko: str
    name: str
    item_type: str 
    data_type: Optional[str] 
    required: bool
    step: Optional[int] = None
    length: Optional[int] = None
    format: Optional[str] = None
    default_value: Optional[str] = None
    upper: Optional[str] = None
    desc: Optional[str] = None
    api_id: Optional[str] = None
    api_name: Optional[str] = None


class RequestItem(ParameterItem):
    pass


class ResponseItem(ParameterItem):
    pass


class PathQueryItem(ParameterItem):
    pass


class ApiItem(BaseModel):
    id: str
    name: str
    desc: str
    method: str
    path: str
    path_query: List[PathQueryItem] 
    request: List[RequestItem]
    response: List[ResponseItem]


class RequirementItem(BaseModel):
    id: str
    name: str
    desc: str
    priority: str
    major: str
    middle: str
    minor: str


class ColumnItem(BaseModel):
    col_name: str
    desc: str
    type: str
    length: Optional[int] = None
    is_pk: Optional[bool] = None
    fk: Optional[str] = None
    is_null: Optional[bool] = None
    const: Optional[str] = None


class TableItem(BaseModel):
    name: str
    column: List[ColumnItem]


class ScenarioRequest(BaseModel):
    project_id: str 
    requirement: List[RequirementItem]
    api_list: List[ApiItem] 
    table_list: List[TableItem] 
