FROM python:3.10
WORKDIR /app
COPY app/ /app
RUN pip install -r requirements.txt
EXPOSE 8000
ENV PYTHONPATH="/app"
ENV ALEMBIC_CONFIG="/app/alembic.ini"
CMD ["/bin/bash", "-c", "alembic upgrade head && uvicorn main:app --host 0.0.0.0 --port 8000"]
