from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Mapping(Base):
    __tablename__ = "mapping"

    key = Column(Integer, primary_key=True, autoincrement=True)  # 매핑 고유 키
    id = Column(String(30), unique=True, nullable=False)  # 매핑 아이디
    step = Column(Integer, nullable=True)  # 단계
    scenario_key = Column(Integer, ForeignKey("scenario.key"), nullable=False)  # 시나리오 FK
    api_list_key = Column(Integer, ForeignKey("api_list.key"), nullable=False)  # API 목록 FK

    # 관계 설정
    scenario = relationship("Scenario", back_populates="mappings")
    api_list = relationship("ApiList", back_populates="mappings")

    def __init__(self, id: str, step: int, scenario_key: int, api_list_key: int):
        self.id = id
        self.step = step
        self.scenario_key = scenario_key
        self.api_list_key = api_list_key
