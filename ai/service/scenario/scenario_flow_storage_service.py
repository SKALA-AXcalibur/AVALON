# service/scenario/scenario_storage_service.py
import logging
from typing import Optional
from repository.scenario.scenario_repository import ScenarioRepository
from service.database import get_db_session  # 본인의 DB 세션 가져오는 방법


class ScenarioFlowStorageService:
    """시나리오 흐름도 저장 서비스"""

    def __init__(self, scenario_repo: Optional[ScenarioRepository] = None):
        self.scenario_repo = scenario_repo  # None일 경우 아래에서 처리

    def save_scenario_flow(self, scenario_id: str, flow_chart: str) -> str:
        """
        시나리오 흐름도 UPDATE
        - scenario_id: 수정할 row의 id
        - flow_chart: 생성된 흐름도 데이터
        - return: 수정된 시나리오 id
        """
        from service.database import get_db_session

        try:
            with get_db_session() as db_session:
                scenario_repo = self.scenario_repo or ScenarioRepository(db_session)
                scenario = scenario_repo.update_flow_chart(scenario_id, flow_chart)
                return scenario.id
        except Exception as e:
            logging.exception("시나리오 흐름도 저장 중 예외 발생")
            raise RuntimeError(f"시나리오 흐름도 저장 실패: {str(e)}")
