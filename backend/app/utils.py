import hashlib
import string

from datetime import datetime, timedelta

import jwt

from sqlalchemy.orm import Session

from constants import ALGORITHM, MINIMUM_PASSWORD_LENGTH, SECRET_KEY
from exceptions import InvalidPassword, UsernameAlreadyTaken
from models import User, UserCreate


def create_token(user: User):
    to_encode = user.__dict__.copy()
    expire = datetime.utcnow() + timedelta(hours=1)
    to_encode.update({"expire": expire.isoformat()})
    del to_encode["_sa_instance_state"]
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)


def create_user(db: Session, user: UserCreate):
    db_user = User(email=user.email, username=user.username,
                   password=hashed_password(user.password))
    db.add(db_user)
    db.commit()
    db.refresh(db_user)


def get_user(db: Session, username: str = "", email: str = ""):
    if email:
        return db.query(User).filter(User.email == email).first()
    if username:
        return db.query(User).filter(User.username == username).first()


def hashed_password(password: str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()


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
