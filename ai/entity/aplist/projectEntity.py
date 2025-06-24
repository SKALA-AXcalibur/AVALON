from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class ProjectEntity(Base):
    __tablename__ = "project"

    key = Column(Integer, primary_key=True, autoincrement=True)  # 프로젝트 고유 키
    id = Column(String(20), unique=True, nullable=False)  # 프로젝트 아이디

    # 관계 설정
    apiLists = relationship("ApiList", back_populates="project", cascade="all, delete-orphan")
    requests = relationship("Request", back_populates="project", cascade="all, delete-orphan")
    filePaths = relationship("FilePath", back_populates="project", cascade="all, delete-orphan")
    dbDesigns = relationship("DbDesign", back_populates="project", cascade="all, delete-orphan")

    def __init__(self, id: str):
        self.id = id
