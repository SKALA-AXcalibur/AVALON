from sqlalchemy import Column, Integer, String, Text, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class ScenarioEntity(Base):
    __tablename__ = "scenario"

    key = Column(Integer, primary_key=True, autoincrement=True)  # 시나리오 고유 키
    id = Column(String(30), unique=True, nullable=False)  # 시나리오 아이디
    name = Column(String(50), nullable=False)  # 시나리오 이름
    description = Column(Text, nullable=False)  # 시나리오 설명
    validation = Column(Text, nullable=True)  # 검증 포인트
    flowChart = Column(Text, nullable=True)  # 흐름도
    projectKey = Column(Integer, ForeignKey("project.key"), nullable=False)  # 프로젝트 FK
    
    # 관계 설정
    project = relationship("Project", back_populates="scenarios")
    mappings = relationship("Mapping", back_populates="scenario", cascade="all, delete-orphan")
    
    def __init__(self, id: str, name: str, description: str, validation: str, flowChart: str, projectKey: int):
        self.id = id
        self.name = name
        self.description = description
        self.validation = validation
        self.flowChart = flowChart
        self.projectKey = projectKey
    
