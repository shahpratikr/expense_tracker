import hashlib

from datetime import datetime, timedelta

import jwt

from sqlalchemy.orm import Session

from constants import ALGORITHM, SECRET_KEY
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


def get_user_by_email(db: Session, email: str):
    return db.query(User).filter(User.email == email).first()


def hashed_password(password: str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()
