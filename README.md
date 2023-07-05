# expense_tracker

Expense Tracker is a demo application for FastAPI and Kubernetes that utilizes
MySQL as its database.

## Prerequisites

Before setting up the application, make sure you have the following:

- Running Kubernetes cluster
- Helm utility

## Steps to setup application

Follow the steps below to set up the Expense Tracker application:

- Install the MySQL Helm chart:

```bash
$ helm repo add bitnami https://charts.bitnami.com/bitnami
$ helm install mysql bitnami/mysql --namespace mysql --create-namespace \
    --set auth.rootPassword=<root_password> --set auth.database=<db_name>
```

- Retrieve the IP address of the MySQL service:

```bash
$ kubectl -n mysql get svc
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
mysql            ClusterIP   10.101.129.130   <none>        3306/TCP   36m
mysql-headless   ClusterIP   None             <none>        3306/TCP   36m
```

- Copy the CLUSTER-IP value for the mysql service.

- Generate the Helm charts for the Expense Tracker application:

```bash
$ cd helm_charts/expense-traker
$ helm package .
```

- Install the application's Helm chart:

```bash
$ helm install expense-tracker ./expense-tracker-1.tgz --create-namespace \
    --namespace expense-tracker --set mysql.service_ip=<mysql_service_ip> \
    --set mysql.db_username=root --set mysql.db_password=<root_password> \
    --set mysql.db_name=<db_name>
```

### NOTE:

Please ensure that you use the same root_password and db_name that were
utilized during the installation of MySQL.
