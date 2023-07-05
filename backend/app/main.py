from email_validator import validate_email, EmailNotValidError
from fastapi import Depends, FastAPI, HTTPException, status
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session

from database import Base, engine, get_db
from exceptions import InvalidPassword, UsernameAlreadyTaken
from models import UserCreate, UserLogin
from utils import (create_token,
                   create_user, get_user,
                   hashed_password, validate_password, validate_username)

app = FastAPI()


@app.on_event("startup")
def create_all():
    Base.metadata.create_all(bind=engine)


@app.post("/signup")
async def signup(user: UserCreate, db: Session = Depends(get_db)):
    try:
        validate_email(user.email)
        validate_password(user.password)
        validate_username(db, user.username)
    except (EmailNotValidError, InvalidPassword, UsernameAlreadyTaken) as e:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,
                            detail=str(e))
    existing_user = get_user(db, email=user.email)
    if existing_user:
        raise HTTPException(status_code=status.HTTP_409_CONFLICT,
                            detail="user already registered")
    create_user(db, user)
    return JSONResponse({"message": "user registered"},
                        status.HTTP_201_CREATED)


@app.post("/login")
async def login(user: UserLogin, db: Session = Depends(get_db)):
    existing_user = get_user(db, email=user.email)
    if not existing_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail="user not found")
    if existing_user.password != hashed_password(user.password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED,
                            detail="invalid password")
    token = create_token(existing_user)
    return JSONResponse({"token": token}, status.HTTP_200_OK)
