# PRODUCT REQUIREMENTS DOCUMENT
## Personal Finance & Investment Tracker

---

## Problem

A personal user needs a single offline Android app to track daily expenses, manage loan balances, monitor budgets, track investments across asset classes, and get AI-assisted loan repayment guidance — without relying on cloud services or internet connectivity. Existing solutions either require internet access, lack investment tracking, or provide no intelligent analysis of local financial data.

---

## MVP Features

### 1. Expense Tracking
Track daily expenses with flexible categorization.

**Acceptance Criteria:**
- User can add an expense with amount (₹), date, and optional category
- User can edit or delete existing expenses
- User can view a list of all expenses filtered by date range (day, week, month, custom)
- Expenses can be categorized or left uncategorized
- Predefined expense categories exist: Food, Transport, Entertainment, Utilities, Healthcare, Other
- User can create, rename, and delete custom categories

### 2. Budget Management
Set and monitor monthly budgets by category.

**Acceptance Criteria:**
- User can set a monthly budget for any category
- Budget limits are tracked per calendar month
- User can view actual spending vs. budget for the current month as text comparison (e.g., "Budget: ₹5,000 | Spent: ₹6,200")
- Overspent budgets are visually distinguished from within-limit budgets
- User can edit or delete budgets

### 3. Loan Tracking with Prepayment Calculator
Track loan balances and calculate the impact of prepayments.

**Acceptance Criteria:**
- User can add a loan with name, current balance (₹), annual interest rate (%), and minimum monthly payment (₹)
- User can edit or delete loans
- User can view a list of all active loans
- User can manually update loan balance as payments are made
- Tapping a loan card opens a prepayment calculator panel at the bottom of the Loans screen
- The calculator shows: remaining balance, interest rate, current EMI, and calculated remaining tenure
- User can enter lump sum prepayment (₹), annual prepayment (₹), and EMI increase per month (₹) as text inputs
- The calculator displays in real time: new tenure, years saved, interest saved, and total interest under the new scenario
- Tapping the same loan card again deselects it and clears the calculator

### 4. Investment Tracking
Track investments across asset classes with gain/loss visibility.

**Acceptance Criteria:**
- User can add an investment with name, asset class, invested amount (₹), current value (₹), and date
- Supported asset classes: Stocks, Mutual Funds, Fixed Deposits, Real Estate, Crypto, Other
- User can manually update the current value of an investment
- User can edit investment details or delete an investment
- Investment list displays invested amount, current value, and calculated gain/loss (₹ and %)
- Investments are grouped or filterable by asset class

### 5. Dashboard
Quick overview of financial summary on a single screen.

**Acceptance Criteria:**
- Dashboard displays: total monthly spending, total active loan balance, total investment gain/loss, and remaining budget headroom for current month
- Dashboard is accessible via button click from main screen
- Each dashboard metric is tappable and navigates to the corresponding detail screen
- Dashboard data updates immediately when expenses, loans, or investments are added or edited

---

## Design Details

### Domain Model
```
Expense: id, amount, category_id (optional), date
ExpenseCategory: id, name, is_predefined
Budget: id, category_id, monthly_limit, month_year
Loan: id, name, current_balance, interest_rate (optional), minimum_monthly_payment (optional), created_at
Investment: id, name, asset_class, invested_amount, current_value, date
```

### Asset Classes (Enum)
`STOCKS`, `MUTUAL_FUNDS`, `FIXED_DEPOSITS`

### Database Schema
- **expense_categories**: Predefined and user-created categories
- **expenses**: Individual transactions with optional category
- **budgets**: Monthly budget limits per category
- **loans**: Loan tracking with balance, interest rate, and minimum payment
- **investments**: Investment records by asset class with invested and current value

### Architecture
Clean Architecture with three layers:
- **Presentation**: Android UI (Jetpack Compose), ViewModels, screens
- **Domain**: Use cases, repository interfaces, domain models
- **Data**: Room DAOs, repository implementations, SQLite database

### Technology Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room (SQLite wrapper)
- **Async**: Kotlin Coroutines
- **DI**: Hilt
- **Pattern**: MVVM
- **Calculations**: Pure Kotlin math for amortization and prepayment scenarios
- **Currency**: ₹ INR only (fixed at build time, no user configuration)

---

## Constraints

- **Offline Only**: No cloud synchronization, no network calls, no telemetry. The app is fully offline after installation.
- **Database**: SQLite only via Room. Unlimited local storage.
- **Platform**: Android application, minimum API 24, target API 35
- **Users**: Single-user per installation
- **Currency**: ₹ INR only. No multi-currency conversion.
- **Historical Data Only**: No future forecasting, goal planning, or scheduled reminders
- **Clean Architecture**: Presentation → Domain → Data layer separation enforced
- **Calculations Only**: Loan repayment guidance is provided through a deterministic prepayment calculator; no AI or LLM features.
- **Loans are liabilities only**: Loans given to others (receivables) are out of scope

---

## Out of Scope

- Cloud synchronization or external database storage
- Multi-user or shared account access
- Multi-currency conversion or exchange rates
- Push notifications or scheduled reminders
- Receipt scanning or OCR
- Bank or financial institution integrations
- Future forecasting or financial goal planning
- Tax calculation or reporting
- Biometric or PIN lock
- Web or desktop versions
- AI features outside the Loans screen

---

## Success Criteria

1. All MVP features (expense, budget, loan, investment, dashboard) are fully functional
2. App operates completely offline after initial model download; no network calls during normal use
3. Data persists correctly in SQLite between app sessions with unlimited storage
4. Prepayment calculator on the Loans screen computes new tenure, interest saved, and total interest using real loan data
5. Dashboard displays accurate summaries across all four financial domains
6. Code follows Clean Architecture principles with clear layer separation
7. Unit tests cover all domain use cases
8. App runs without crashes on Android devices during normal usage

---
