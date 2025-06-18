# config/config.py

"""
테이블설계서 파서 관련 설정값
"""

# 헤더명
DB_HEADER_MAP = {
    "테이블 한글명": "name",
    "컬럼 한글명": "col_name",
    "데이터타입": "type",
    "길이": "length",
    "PK": "is_pk",
    "FK": "fk",
    "Null여부": "is_null",
    "제약 조건": "const",
    "설명": "desc",
}

# 시트 이름
DB_SHEET_NAME = "테이블정의서(컬럼)"

# 키워드
DB_REQUIRED_KEYWORDS = ["Null여부", "데이터타입"]

"""
요구사항정의서 파서 관련 설정값
"""

# 헤더명
REQ_HEADER_MAP = {
    "요구사항 ID": "id",
    "요구사항 명": "name",
    "요구사항 설명": "desc",
    "중요도": "priority",
    "대분류": "major",
    "중분류": "middle",
    "소분류": "minor",
}

# 시트 이름
REQ_SHEET_NAME = "요구사항정의서"

# 키워드
REQ_REQUIRED_KEYWORDS = ["대분류", "중분류", "소분류"]