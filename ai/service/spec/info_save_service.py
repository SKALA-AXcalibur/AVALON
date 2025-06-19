import os
import logging
import httpx
from fastapi import HTTPException

async def save_to_info_api(project_id: str, result: dict): # project_id 추가
    """
    결과 딕셔너리를 정보저장 api로 전송하는 함수
    """
    base_url = os.getenv("PROJECT_API_BASE_URL")
    url = f"{base_url}/{project_id}" # 실제 서비스 주소로 변경 필요 (정보저장 api 경로)
    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(url, json=result)
            response.raise_for_status()
            return response.json()
        except httpx.RequestError as e:
            logging.error(f"[정보저장API 호출 실패] project_id={project_id}, error={e}")
            raise HTTPException(status_code=502, detail={"code": "G001", "message": "정보저장 API 호출 실패"})
        except httpx.HTTPStatusError as e:
            logging.error(f"[정보저장API 응답 오류] project_id={project_id}, status={e.response.status_code}, body={e.response.text}")
            raise HTTPException(status_code=500, detail={"code": "G002", "message": "정보저장 API 응답 오류"})
