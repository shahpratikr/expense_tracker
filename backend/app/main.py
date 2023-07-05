from email_validator import validate_email, EmailNotValidError
from fastapi import Depends, FastAPI, HTTPException, Request, status
from fastapi.responses import JSONResponse, Response
from sqlalchemy.orm import Session

from database import Base, engine, get_db
from exceptions import InvalidPassword, UsernameAlreadyTaken
from models import ExpenseCreate, UserCreate, UserLogin
from utils import (authenticate_token, create_expense, create_token,
                   create_user, remove_expense, get_expense, get_user,
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


@app.post("/add_expense")
async def add_expense(expense: ExpenseCreate, request: Request,
                      db: Session = Depends(get_db)):
    decoded_token = authenticate_token(request.headers["token"])
    create_expense(db, expense, decoded_token["id"])
    return JSONResponse({"message": "expense added"}, status.HTTP_201_CREATED)


@app.get("/get_expense")
async def list_expense(request: Request, db: Session = Depends(get_db)):
    decoded_token = authenticate_token(request.headers["token"])
    expense = get_expense(db, decoded_token["id"])
    return JSONResponse({"expenses": expense}, status.HTTP_200_OK)


@app.delete("/delete_expense/{expense_id}")
async def delete_expense(request: Request, expense_id: int,
                         db: Session = Depends(get_db)):
    decoded_token = authenticate_token(request.headers["token"])
    remove_expense(db, decoded_token["id"], expense_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
