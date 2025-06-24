from sqlalchemy import Column, Integer, String, Text, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class ApiList(Base):
    __tablename__ = "api_list"
    
    key = Column(Integer, primary_key=True, autoincrement=True)  # API 목록 키 (PK, AUTO_INCREMENT)
    id = Column(String(30), unique=True, nullable=False)         # API 목록 ID (프로젝트별 유니크)
    name = Column(String(20), nullable=False)                    # API 목록 명 (NOT NULL, 최대 20자)
    url = Column(String(50), nullable=False)                     # API 목록 URL (NOT NULL, 최대 50자)
    path = Column(String(100), nullable=False)                   # API 목록 경로 (NOT NULL, 최대 100자)
    method = Column(String(30), nullable=False)                  # API 목록 메서드 (NOT NULL, 최대 30자)
    description = Column(Text, nullable=True)                    # API 목록 설명 (TEXT)
    projectKey = Column(Integer, ForeignKey("project.key"), nullable=False)  # 프로젝트 키 (N:1)
    requestKey = Column(Integer, ForeignKey("request.key"), nullable=False)  # 요구사항 키 (N:1)
    
    # 관계 설정
    project = relationship("Project", back_populates="apiLists")
    request = relationship("Request", back_populates="apiLists")
    mappings = relationship("Mapping", back_populates="apiList", cascade="all, delete-orphan")
    
    def __init__(self, id: str, name: str, url: str, path: str, method: str, projectKey: int, requestKey: int, description: str = None):
        self.id = id
        self.name = name
        self.url = url
        self.path = path
        self.method = method
        self.projectKey = projectKey
        self.requestKey = requestKey
        self.description = description
