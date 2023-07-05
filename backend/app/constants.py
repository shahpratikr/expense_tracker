import os

mysql_db_host = os.getenv("MYSQL_DB_HOST")
mysql_db_username = os.getenv("MYSQL_DB_USERNAME")
mysql_db_password = os.getenv("MYSQL_DB_PASSWORD")
mysql_db_name = os.getenv("MYSQL_DB_NAME")
MYSQL_DATABASE_URL = "mysql://%s:%s@%s:3306/%s" % (
    mysql_db_username, mysql_db_password, mysql_db_host, mysql_db_name)
USERS_TABLE_NAME = "users"
ALGORITHM = "HS256"
SECRET_KEY = "4fbafc86-4d2d-43b0-b7fb-decfe94d809f"
