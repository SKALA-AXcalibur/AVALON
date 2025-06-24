# ai/state/testcase/flow_state.py
from pydantic import BaseModel
from typing import Optional, List, Tuple

from dto.request.testcase.tc_generation_request import TestcaseGenerationRequest
from dto.response.testcase.testcase_data import TestcaseData

"""
TC 생성 STATE 정의
- 시나리오 ID
- 생성에 필요한 request 객체
- 생성된 TC 목록
- TC에 대한 coverage 값(검증률)
- regenerated 필요한 tc id 및 사유
- 재시도 횟수
"""
class FlowState(BaseModel):
    scenario_id: str
    request: TestcaseGenerationRequest
    tc_list: List[TestcaseData] = []
    coverage: Optional[float] = None
    revalidation_targets: List[Tuple[str, str]] = []  # (tc_list index, reason)
    retry_count: int = 0