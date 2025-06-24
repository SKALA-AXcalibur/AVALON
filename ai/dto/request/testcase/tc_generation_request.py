# ai/dto/request/testcase/tc_generation_request.py
from pydantic import BaseModel, Field
from typing import List

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario

class TestcaseGenerationRequest(BaseModel):
    """
    TC생성 Request DTO
    (시나리오, API 매핑표)
    """
    scenario: Scenario
    api_mapping_list: List[ApiMapping] = Field(..., alias="apiMappingList")

    model_config = {
        "populate_by_name": True,
    }