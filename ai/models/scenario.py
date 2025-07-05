# models/scenario.py
from sqlalchemy import Column, Integer, String, Text, DateTime
from service.database import Base

class Scenario(Base):
    """
    시나리오 DB 테이블 ORM 모델
    """

    __tablename__ = "scenario"

    key = Column(Integer, primary_key=True, index=True)  # PK (Auto Increment)
    id = Column(String(20), unique=True)  # 시나리오 ID
    name = Column(String(50))  # 시나리오 이름
    description = Column(Text)  # 시나리오 설명
    validation = Column(Text)  # 검증 포인트
    flow_chart = Column(Text)  # Mermaid 코드(시나리오 플로우 차트)
    create_at = Column(DateTime)  # 생성일시
    project_key = Column(Integer)  # FK
