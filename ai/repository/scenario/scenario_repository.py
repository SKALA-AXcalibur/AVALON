# repository/scenario/scenario_repository.py
from sqlalchemy.orm import Session
from models.scenario import Scenario


class ScenarioRepository:
    """
    시나리오 흐름도 데이터베이스 저장소 클래스
    시나리오 테이블에 흐름도만 UPDATE(수정)하는 기능만 제공합니다.
    """

    def __init__(self, db_session: Session):
        """
        데이터베이스 세션을 초기화합니다.

        Args:
            db_session (Session): SQLAlchemy 데이터베이스 세션
        """
        self.db = db_session

    def update_flow_chart(self, scenario_id: str, flow_chart: str):
        """
        기존 시나리오 row의 flow_chart만 수정합니다.

        Args:
            scenario_id (str): 수정할 row의 id 값
            flow_chart (str): 저장할 시나리오 흐름도 데이터

        Returns:
            Scenario: 수정된 시나리오 객체
        """
        scenario = self.db.query(Scenario).filter(Scenario.id == scenario_id).first()
        if not scenario:
            raise ValueError(f"id가 {scenario_id}인 시나리오가 존재하지 않습니다.")
        scenario.flow_chart = flow_chart
        self.db.commit()
        self.db.refresh(scenario)
        return scenario
