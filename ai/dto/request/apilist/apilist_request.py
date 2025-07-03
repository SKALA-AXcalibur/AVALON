from pydantic import BaseModel

class ApiListRequest(BaseModel):
    token : str