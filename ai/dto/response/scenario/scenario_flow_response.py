# dto/response/scenario/scenario_flow_response.py
from pydantic import BaseModel


class ScenarioFlowResponse(BaseModel):
    data: str  # Mermaid 차트 문법 문자열
