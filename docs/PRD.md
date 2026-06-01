# PRODUCT REQUIREMENTS DOCUMENT
## Personal Finance & Investment Tracker

---

## Problem

Users need a simple, offline-first way to track personal finances on their Android devices without relying on cloud services or third-party integrations. Existing solutions either require internet connectivity, compromise privacy through cloud storage, or are overly complex for basic financial tracking needs.

---

## MVP Features

### 1. Expense Tracking
Track daily expenses with flexible categorization.

**Acceptance Criteria:**
- User can add an expense with amount, date, and optional category
- User can edit or delete existing expenses
- User can view a list of all expenses filtered by date range
- Expenses can be categorized or left uncategorized
- Predefined expense categories exist (e.g., Food, Transport, Entertainment, Utilities, Healthcare, Other)
- User can create, edit, and delete custom categories

### 2. Budget Management
Set and monitor monthly budgets by category.

**Acceptance Criteria:**
- User can set a monthly budget for any category
- Budget limits are tracked per month
- User can view actual spending vs. budget for the current month as text comparison (e.g., "Budget: $100 | Spent: $120")
- User can edit or delete budgets

### 3. Loan Tracking
Track loan balances and details.

**Acceptance Criteria:**
- User can add a loan with name and current balance
- User can edit or delete loans
- User can view a list of all active loans
- User can manually update loan balance as payments are made

### 4. Dashboard
Quick overview of financial summary on a single screen.

**Acceptance Criteria:**
- Dashboard displays total monthly spending amount
- Dashboard is accessible via button click from main screen
- Monthly spending data is clickable to navigate to detailed expense list
- Dashboard data updates when expenses or loans are added/edited

---

## Design Details

### Domain Model
```
Expense: id, amount, category_id (optional), date
ExpenseCategory: id, name
Budget: id, category_id, monthly_limit, month_year
Loan: id, name, current_balance, created_at
```

### Database Schema
- **expense_categories**: Predefined and user-created categories
- **expenses**: Individual transactions with optional category
- **budgets**: Monthly budget limits per category
- **loans**: Loan tracking by name and balance

### Architecture
Clean Architecture with three layers:
- **Presentation**: Android UI (Jetpack Compose), ViewModels, screens
- **Domain**: Use cases, repository interfaces, entities
- **Data**: Room DAOs, repository implementations, SQLite database

### Technology Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room (SQLite wrapper)
- **Async**: Coroutines
- **DI**: Hilt
- **Pattern**: MVVM

---

## Constraints

- **Offline Only**: No cloud synchronization or network connectivity required
- **Database**: SQLite only, no external databases
- **Platform**: Android application (latest stable version - Android 15/API 35)
- **Users**: Single-user per installation
- **Currency**: Single currency per app instance (no multi-currency conversion)
- **Historical Data Only**: No future planning or forecasting features
- **Clean Architecture**: Must follow layered architecture (Presentation → Domain → Data)

---

## Success Criteria

1. All MVP features (expense, budget, loan, dashboard) are fully functional and tested
2. App operates completely offline with no network calls
3. Data persists correctly in SQLite database between app sessions
4. Dashboard displays current month spending on demand
5. Code follows Clean Architecture principles with clear separation of concerns
6. Unit tests cover all domain use cases
7. App runs smoothly on Android devices with no crashes during normal usage

---
