import hashlib
import string

from datetime import datetime, timedelta

import jwt

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from constants import ALGORITHM, MINIMUM_PASSWORD_LENGTH, SECRET_KEY
from exceptions import InvalidPassword, UsernameAlreadyTaken
from models import Expense, ExpenseCreate, User, UserCreate


def authenticate_token(token: str):
    http_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="invalid username or password"
    )
    try:
        decoded_token = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    except jwt.DecodeError:
        raise http_exception
    if datetime.utcnow() >= datetime.strptime(decoded_token["expire"],
                                              "%Y-%m-%dT%H:%M:%S.%f"):
        raise http_exception
    return decoded_token


def create_expense(db: Session, expense: ExpenseCreate, user_id: str):
    new_expense = Expense(user_id=user_id, name=expense.name,
                          price=expense.price, type=expense.type,
                          created_at=datetime.utcnow())
    db.add(new_expense)
    db.commit()
    db.refresh(new_expense)


def create_token(user: User):
    to_encode = user.__dict__.copy()
    expire = datetime.utcnow() + timedelta(hours=1)
    to_encode.update({"expire": expire.isoformat()})
    del to_encode["_sa_instance_state"]
    del to_encode["created_at"]
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)


def create_user(db: Session, user: UserCreate):
    db_user = User(email=user.email, username=user.username,
                   created_at=datetime.utcnow(),
                   password=hashed_password(user.password))
    db.add(db_user)
    db.commit()
    db.refresh(db_user)


def get_expense(db: Session, user_id: id, expense_id: id = 0):
    expenses = []
    if expense_id != 0:
        return db.query(Expense).filter(
            Expense.user_id == user_id and Expense.id == expense_id).first()
    for expense in db.query(Expense).filter(Expense.user_id == user_id).all():
        expense_dict = expense.__dict__
        del expense_dict["_sa_instance_state"]
        if expense_dict["created_at"] is not None:
            expense_dict["created_at"] = expense_dict["created_at"].strftime(
                "%d/%m/%Y %H:%M:%S")
        expenses.append(expense_dict)
    return expenses


def get_user(db: Session, username: str = "", email: str = ""):
    if email:
        return db.query(User).filter(User.email == email).first()
    if username:
        return db.query(User).filter(User.username == username).first()


def hashed_password(password: str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()


def remove_expense(db: Session, user_id: int, expense_id: int):
    existing_expense = get_expense(db, user_id, expense_id)
    if not existing_expense:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail="expense not found")
    db.delete(existing_expense)
    db.commit()


def validate_password(password: str):
    lower_char, upper_char, special_char, digit = 0, 0, 0, 0
    errors = []
    if (len(password) < MINIMUM_PASSWORD_LENGTH):
        errors.append("password should be at least %d characters" %
                      MINIMUM_PASSWORD_LENGTH)
    for char in password:
        if char.islower():
            lower_char += 1
        elif char.isupper():
            upper_char += 1
        elif char.isdigit():
            digit += 1
        elif char in string.punctuation:
            special_char += 1
    if lower_char == 0:
        errors.append("password must contain 1 lowercase character")
    if upper_char == 0:
        errors.append("password must contain 1 uppercase character")
    if special_char == 0:
        errors.append("password must contain 1 special character")
    if digit == 0:
        errors.append("password must contain 1 digit")
    if errors:
        raise InvalidPassword(errors)


def validate_username(db: Session, username: str):
    user = get_user(db, username=username)
    if user:
        raise UsernameAlreadyTaken("username is already taken")
