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


"""
인터페이스 설계서 파서 관련 설정값
"""
# 인터페이스 설계서 구분자
IMPL_PARAM = "*** 입출력 파라미터 명세 ***"
PATH_QUERY_PARAM = "*** Path / Query 파라미터 항목 ***"
REQUEST_PARAM = "*** 요청(Request) 파라미터 항목 ***"
RESPONSE_PARAM = "*** 응답(Response) 파라미터 항목 ***"

# 인터페이스 설계서 파싱 문자
IMPL_FIELD_MAP = {
    "id": ("인터페이스ID", 2),
    "name": ("인터페이스명", 2),
    "desc": ("설명", 2),
    "method": ("HTTP Method", 1),
    "path": ("Path", 2),
    "url": ("URL", 2),
}

"""
인터페이스 정의서 파서 관련 설정값
"""
# 인터페이스 정의서 구분자
INTERFACE_DEF = "인터페이스정의서"
INTERFACE_ID = "인터페이스ID*"
INTERFACE_REQ_ID = "요구사항ID*"
INTERFACE_DEF_PARSER = "업무Level1*"