from pydantic import BaseModel
from sqlalchemy import Column, DateTime, ForeignKey, Integer, String

from constants import EXPENSES_TABLE_NAME, USERS_TABLE_NAME
from database import Base


class Expense(Base):
    __tablename__ = EXPENSES_TABLE_NAME

    id = Column(Integer, primary_key=True, index=True)
    created_at = Column(DateTime)
    user_id = Column(Integer, ForeignKey("%s.id" % USERS_TABLE_NAME))
    name = Column(String(50), unique=True, nullable=False)
    price = Column(Integer, nullable=False)
    type = Column(String(6), nullable=False)


class ExpenseCreate(BaseModel):
    name: str
    price: int
    type: str


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
