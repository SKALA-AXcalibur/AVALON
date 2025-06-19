# ai/dto/request/testcase/tc_generation_request.py
from pydantic import BaseModel, Field
from typing import List

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario

"""
TC생성 Request DTO
(시나리오, API 매핑표)
"""
class TestcaseGenerationRequest(BaseModel):
    scenario: Scenario
    api_mapping_list: List[ApiMapping] = Field(..., alias="apiMappingList")

    class Config:
        validate_by_name = True