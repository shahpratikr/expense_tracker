from pydantic import BaseModel
from sqlalchemy import Column, DateTime, Integer, String

from constants import USERS_TABLE_NAME
from database import Base


class User(Base):
    __tablename__ = USERS_TABLE_NAME

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(50), unique=True, nullable=False)
    username = Column(String(50), nullable=False)
    password = Column(String(128), nullable=False)
    created_at = Column(DateTime)


class UserCreate(BaseModel):
    email: str
    username: str
    password: str


class UserLogin(BaseModel):
    email: str
    password: str
