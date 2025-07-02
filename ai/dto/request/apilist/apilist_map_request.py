from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional
from datetime import datetime


class ScenarioInput(BaseModel):
    """입력 시나리오 데이터 (다양한 필드명 지원)"""
    scenarioId: Optional[str] = Field(None, alias="scenarioId")
    title: Optional[str] = Field(None, alias="title")
    description: Optional[str] = None
    validation: Optional[str] = None
    
    class Config:
        populate_by_name = True


class ApiInput(BaseModel):
    """입력 API 데이터 (다양한 필드명 지원)"""
    apiName: Optional[str] = Field(None, alias="apiName")
    url: Optional[str] = Field(None, alias="url")
    method: Optional[str] = None
    description: Optional[str] = None
    parameters: Optional[str] = None
    responseStructure: Optional[str] = None
    
    class Config:
        populate_by_name = True


class ApiListMapRequest(BaseModel):
    """API 리스트 매핑 요청 DTO"""
    apiList: List[ApiInput] = Field(default_factory=list, alias="apiList")
    scenarioList: List[ScenarioInput] = Field(default_factory=list, alias="scenarioList")
    
    class Config:
        populate_by_name = True


# 편의 함수들
def convert_scenario_list(scenario_list: List[Dict]) -> List[Dict]:
    """시나리오 리스트를 내부 형식으로 변환"""
    created_at = datetime.now().isoformat()
    return list(map(lambda s: {
        "scenarioId": s.get("scenarioId"),
        "title": s.get("title"),
        "description": s.get("description"),
        "validation": s.get("validation"),
        "createdAt": created_at
    }, scenario_list))


def convert_api_list(api_list: List[Dict]) -> List[Dict]:
    """API 리스트를 내부 형식으로 변환"""
    return list(map(lambda a: {
        "apiName": a.get("apiName"),
        "url": a.get("url"),
        "method": a.get("method"),
        "description": a.get("description"),
        "parameters": a.get("parameters"),
        "responseStructure": a.get("responseStructure")
    }, api_list))