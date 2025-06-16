# ai/service/spec/interface_def_parser.py
import pandas as pd
from fastapi import UploadFile
from typing import List
from io import BytesIO
from loguru import logger

from dto.request.spec.api import Api


class InterfaceDefParserService:
    async def map_req_ids_to_apis(self, upload_file: UploadFile, api_list: List[Api]) -> List[Api]:
        """
        인터페이스 정의서 파일에서 인터페이스 ID와 요구사항 ID를 매핑하여 Api 객체에 주입
        """
        contents = await upload_file.read()
        excel_bytes = BytesIO(contents)
        df = pd.read_excel(excel_bytes, sheet_name=0, header=None)

        id_map = {}
        for i, row in df.iterrows():
            try:
                interface_id = str(row[1]).strip() if pd.notna(row[1]) else ""
                req_id = str(row[2]).strip() if pd.notna(row[2]) else ""
                if interface_id:
                    id_map[interface_id] = req_id
            except Exception as e:
                logger.warning(f"[정의서 파싱 실패] {i}행 - {e}")
                continue

        for api in api_list:
            api.req_id = id_map.get(api.id, "")
            if not api.req_id:
                logger.warning(f"[req_id 누락] 인터페이스 ID: {api.id}에 해당하는 요구사항 ID를 찾을 수 없음")

        return api_list
