# ai/service/spec/interface_def_parser.py
import pandas as pd
from fastapi import UploadFile
from typing import List
from io import BytesIO
from loguru import logger

from dto.request.spec.api import Api

class InterfaceDefParserService:
    """
    인터페이스 정의서 파일을 파싱하여 요구사항 ID를 Api 객체에 주입하는 클래스
    """
    async def map_req_ids_to_apis(self, upload_file: UploadFile, api_list: List[Api]) -> List[Api]:
        """
        인터페이스 정의서 파일에서 인터페이스 ID와 요구사항 ID를 매핑하여 API 객체에 주입
        """
        contents = await upload_file.read()
        excel_bytes = BytesIO(contents)

        # 시트명 판단을 위한 ExcelFile 객체 생성
        xls = pd.ExcelFile(excel_bytes)
        sheet_name = self.find_interface_def_sheet(xls.sheet_names)

        # BytesIO 포인터 다시 앞으로 (read_excel은 새로 읽어야 함)
        excel_bytes.seek(0)

        # 실제 시트 읽기
        df = pd.read_excel(excel_bytes, sheet_name=sheet_name, header=0)

        id_map = {}

        # 인터페이스 정의서 파싱하여 인터페이스 ID: 요구사항 ID 매핑표 생성
        for _, row in df.iterrows():
            try:
                interface_id = str(row.get("인터페이스ID*", "")).strip()
                req_id = str(row.get("요구사항ID*", "")).strip()

                if interface_id and req_id and interface_id != "nan":
                    id_map[interface_id] = req_id

            except Exception as e:
                logger.warning(f"[정의서 파싱 실패] - {e}")
                continue

        # 최종 API 파싱 결과 반환
        for api in api_list:
            api.req_id = id_map.get(api.id, "")
            if not api.req_id:
                logger.warning(f"[req_id 누락] 인터페이스 ID: {api.id}에 해당하는 요구사항 ID를 찾을 수 없음")

        return api_list
    
    def find_interface_def_sheet(self, sheet_names: List[str]) -> str:
        """
        인터페이스 정의서 시트명을 추론:
        1. 시트명 중 '인터페이스정의서'가 있으면 우선 사용
        2. 없으면 마지막 시트를 fallback
        """
        if "인터페이스정의서" in sheet_names:
            return "인터페이스정의서"
        return sheet_names[-1]