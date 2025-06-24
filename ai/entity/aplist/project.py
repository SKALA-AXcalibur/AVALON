from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Project(Base):
    __tablename__ = "project"

    key = Column(Integer, primary_key=True, autoincrement=True)  # 프로젝트 고유 키
    id = Column(String(20), unique=True, nullable=False)  # 프로젝트 아이디

    # 관계 설정
    api_lists = relationship("ApiList", back_populates="project", cascade="all, delete-orphan")
    requests = relationship("Request", back_populates="project", cascade="all, delete-orphan")
    file_paths = relationship("FilePath", back_populates="project", cascade="all, delete-orphan")
    db_designs = relationship("DbDesign", back_populates="project", cascade="all, delete-orphan")

    def __init__(self, id: str):
        self.id = id
