from fastapi import Depends, FastAPI, HTTPException, status
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session

from database import Base, engine, get_db
from models import UserCreate, UserLogin
from utils import (create_token,
                   create_user, get_user_by_email,
                   hashed_password)

app = FastAPI()


@app.on_event("startup")
def create_all():
    Base.metadata.create_all(bind=engine)


@app.post("/signup")
async def signup(user: UserCreate, db: Session = Depends(get_db)):
    existing_user = get_user_by_email(db, email=user.email)
    if existing_user:
        raise HTTPException(status_code=status.HTTP_409_CONFLICT,
                            detail="user already registered")
    create_user(db, user)
    return JSONResponse({"message": "user registered"},
                        status.HTTP_201_CREATED)


@app.post("/login")
async def login(user: UserLogin, db: Session = Depends(get_db)):
    existing_user = get_user_by_email(db, email=user.email)
    if not existing_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail="user not found")
    if existing_user.password != hashed_password(user.password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED,
                            detail="invalid password")
    token = create_token(existing_user)
    return JSONResponse({"token": token}, status.HTTP_200_OK)
