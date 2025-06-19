# ai/dto/request/testcase/tc_generation_request.py
from pydantic import BaseModel
from typing import List

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario
from dto.request.testcase.table import Table

"""
TC생성 Request DTO
(시나리오, API 매핑표, 테이블 설계 정보)
"""
class TestcaseGenerationRequest(BaseModel):
    scenario: Scenario
    api_mapping_list: List[ApiMapping]
    db_list: List[Table]