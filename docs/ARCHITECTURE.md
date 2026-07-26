# ARCHITECTURE DOCUMENT
## Personal Finance & Investment Tracker

---

## Revision Note

This is a **revision** of an existing, committed architecture. Phases 1-6 were previously implemented in full (see git history: `e32fd10` foundation, `88eac53`..`f6ad2c8` phases 2-5, later commits adding loan enhancements and LLM support which was subsequently removed per `03c0fd7`).

The PRD (`docs/PRD.md`) was just revised to:
1. Remove Expense Tracking, Expense Categories, and Budget Management from scope entirely.
2. Rework Loan Tracking to auto-recalculate balance from `loan_start_date`, `emi_day_of_month`, and a fixed `emi_amount`, with manual override retained.

**Locked (stack unchanged, unaffected by this revision):**
- Phase 1 — Foundation: Kotlin, Jetpack Compose, Room, Hilt, Coroutines, MVVM, Clean Architecture layering.
- Phase 5 — Investment Tracking: domain model, repository, use cases, screen, ViewModel all unchanged.

**Obsolete — to be deleted (spec no longer includes these):**
- Phase 2 — Expense Tracking
- Phase 3 — Budget Management
- Expense categories (part of Phase 2)

**Revised in this pass:**
- Phase 4 — Loan Tracking & Prepayment Calculator: `Loan` domain model, entity, DAO, repository, and use cases change to support automatic balance recalculation.
- Phase 6 — Dashboard: drops monthly spending and budget headroom metrics; keeps loan balance and investment gain/loss.

---

## Technology Stack

### Language & Framework
- **Kotlin** with **Jetpack Compose** for Android UI
- **Room** (SQLite wrapper) for local data persistence
- **Kotlin Coroutines** for asynchronous operations
- **Hilt** for dependency injection
- **MVVM** architectural pattern

*(Locked by Phase 1 — restated, not re-decided.)*

### Loan Auto-Update
- **Requirement**: PRD Loan Tracking feature — balance recalculates automatically when the Loans screen opens.
- **Implementation**: Pure Kotlin using `java.time` (`LocalDate`, `ChronoUnit`) — already a dependency of the existing `Loan` model, no new library needed.
- **Logic**: On Loans screen load, for each loan, count EMI dates that have elapsed between `last_balance_update_date` and today (based on `emi_day_of_month`), and apply one amortization cycle per elapsed EMI date: `balance = balance - (emiAmount - balance * monthlyRate)`, floored at 0. Update `last_balance_update_date` to the most recent applied EMI date.

### Prepayment Calculator
- **Scope**: Loans screen only — amortization-based prepayment scenario calculator
- **Implementation**: Pure Kotlin math (no external library); runs fully offline
- **Inputs**: Lump sum prepayment, annual prepayment, EMI increase per month
- **Outputs**: New tenure, years saved, interest saved, total interest under new scenario

*(Locked by Phase 4 — unaffected by this revision.)*

### Database
- **SQLite** via Room ORM
- Local-only, no cloud synchronization
- Single-user per installation, unlimited storage

### Key Libraries
- `androidx.compose.ui` — UI framework
- `androidx.room` — Database & DAOs
- `androidx.lifecycle` — ViewModels, lifecycle awareness
- `com.google.dagger.hilt` — Dependency injection
- `kotlinx.coroutines` — Async/await
- Pure Kotlin math (`java.time` + arithmetic) — amortization and auto-update calculations, no external library

---

## Folder Structure

```
app/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── LoanDao.kt
│   │   │   └── InvestmentDao.kt
│   │   ├── database/
│   │   │   └── FinanceDatabase.kt
│   │   └── repository/
│   │       ├── LoanRepository.kt
│   │       └── InvestmentRepository.kt
│   └── model/
│       ├── LoanEntity.kt
│       └── InvestmentEntity.kt
├── domain/
│   ├── model/
│   │   ├── Loan.kt
│   │   ├── Investment.kt
│   │   └── AssetClass.kt         ← enum: STOCKS, MUTUAL_FUNDS, FIXED_DEPOSITS
│   ├── repository/
│   │   ├── ILoanRepository.kt
│   │   └── IInvestmentRepository.kt
│   └── usecase/
│       ├── loan/
│       └── investment/
├── presentation/
│   ├── screen/
│   │   ├── HomeScreen.kt
│   │   ├── LoanScreen.kt         ← includes prepayment calculator panel
│   │   ├── InvestmentScreen.kt
│   │   └── DashboardScreen.kt
│   ├── viewmodel/
│   │   ├── LoanViewModel.kt      ← loan CRUD + auto-update trigger + selected loan for calculator
│   │   ├── InvestmentViewModel.kt
│   │   └── DashboardViewModel.kt
│   ├── component/
│   │   ├── LoanCard.kt
│   │   ├── InvestmentCard.kt
│   │   ├── PrepaymentCalculatorPanel.kt ← prepayment scenario calculator
│   │   └── ErrorDialog.kt
│   └── navigation/
│       └── NavGraph.kt
└── MainActivity.kt
```

`ExpenseDao.kt`, `ExpenseCategoryDao.kt`, `BudgetDao.kt`, their entities, repositories, domain models, use cases (`expense/`, `budget/`, `category/`), `ExpenseListScreen.kt`, `ExpenseDetailScreen.kt`, `BudgetScreen.kt`, `CategoryManagementScreen.kt`, `ExpenseViewModel.kt`, `BudgetViewModel.kt`, `CategoryViewModel.kt`, `ExpenseItem.kt`, and `CategorySelector.kt` are removed as part of this revision.

---

## Core Data Models

### Loan
```
id: Long (primary key, auto-generated)
name: String (required)
current_balance: Double (>= 0)
interest_rate: Double (annual %, > 0)
emi_amount: Double (> 0)
loan_start_date: LocalDate
emi_day_of_month: Int (1-31)
last_balance_update_date: LocalDate
created_at: LocalDate
```

`minimum_monthly_payment` is removed; `emi_amount` is now the required, fixed monthly payment used both for auto-update and as the calculator's baseline EMI. `loan_start_date` and `emi_day_of_month` are new, required — they anchor the auto-update cycle count. `last_balance_update_date` is new, system-maintained, initialized to `loan_start_date` on creation.

### Investment
```
id: Long (primary key, auto-generated)
name: String (required)
asset_class: AssetClass (enum, required)
invested_amount: Double (> 0)
current_value: Double (>= 0)
date: LocalDate
```

*(Locked — unchanged.)*

### AssetClass (Enum)
```
STOCKS, MUTUAL_FUNDS, FIXED_DEPOSITS
```

---

## Architecture Layers

### Presentation Layer
- **Components**: Jetpack Compose UI elements, screens
- **ViewModels**: State management, business logic orchestration
- **Navigation**: Screen routing and transitions
- **Responsibilities**: Render UI, handle user input, collect VM state changes

### Domain Layer
- **Models**: Pure Kotlin data classes — no Android or Room imports
- **Repository Interfaces**: Contracts for data access (I-prefix required)
- **Use Cases**: Orchestrate repository calls, implement business rules, input validation
- **Responsibilities**: Business rules, validation, financial calculations, loan auto-update cycle math

### Data Layer
- **Entities**: Room database models
- **DAOs**: Direct database access
- **Repositories**: Implement domain interfaces, transform entities ↔ domain models
- **Database**: Room SQLite setup and migrations
- **Responsibilities**: Persist and retrieve data

---

## Loan Auto-Update Use Case

A new `RecalculateLoanBalancesUseCase` (domain layer) runs whenever `LoanViewModel` loads the Loans screen:

1. For each loan, compute the number of EMI dates (`emi_day_of_month`) that have occurred strictly between `last_balance_update_date` (exclusive) and today (inclusive).
2. For each elapsed EMI date, apply one amortization step: `interest = balance * (interest_rate / 12 / 100)`, `principal_component = emi_amount - interest`, `balance = max(0, balance - principal_component)`.
3. Persist the updated `current_balance` and advance `last_balance_update_date` to the last elapsed EMI date processed.
4. If `emi_amount <= interest` for a cycle (negative amortization), stop advancing that loan's balance and surface a validation warning rather than increasing the balance — this is a data-entry error case (EMI too low for the rate), not a silent runaway.

This runs synchronously in a coroutine on IO dispatcher before emitting `UiState`; no background service or WorkManager job is introduced, keeping the app fully offline-triggered and consistent with the no-scheduled-reminders constraint.

## Prepayment Calculator

`PrepaymentCalculatorPanel` is a stateless Compose component in the presentation layer. It receives a `Loan?` and uses pure Kotlin amortization math to compute:

- **Remaining tenure** — months to pay off at current EMI and rate
- **New tenure** — simulate month-by-month with lump sum, annual prepayment, and EMI increase applied
- **Interest saved** — difference in total interest paid between baseline and new scenario
- **Total interest (new)** — total interest under the prepayment scenario

No external library is required. All calculations run synchronously on the UI thread (negligible cost for typical loan durations).

*(Locked — unchanged by this revision.)*

---

## Dependency Injection (Hilt)

- Database is application-scoped singleton
- All `@HiltViewModel` classes inject use cases, never DAOs directly
- Coroutine dispatchers are injected for testability

---

## Key Design Decisions

### Clean Architecture
Three-layer separation keeps domain logic independent of Android/Room APIs and testable with pure JVM unit tests.

### MVVM Pattern
Each screen has a dedicated ViewModel that exposes immutable `StateFlow<UiState>`, handles user actions via public functions, and delegates business logic to use cases.

### Currency
₹ INR is fixed at build time. No user setting, no conversion logic. All amounts are stored as raw `Double` and displayed with the ₹ symbol everywhere.

### Input Validation
Occurs in domain use cases. Rules: balance >= 0, interest rate > 0, emi_amount > 0, emi_day_of_month in 1-31, required fields populated, valid dates.

### Error Handling
Repository methods may throw. ViewModels catch exceptions and expose error messages in state. UI displays `ErrorDialog` composable with user-friendly messages.

### Dark Mode
Jetpack Compose respects system theme via `isSystemInDarkTheme()`. Color scheme defined in theme composables, consistent across all screens.

### Offline-First
All data in SQLite locally. No network calls are made at any point. Loan balance auto-update and repayment guidance both run as pure, on-device computation triggered by screen load — no scheduled jobs, no notifications.

---

## Database Schema

### loans
```sql
CREATE TABLE loans (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  current_balance REAL NOT NULL CHECK (current_balance >= 0),
  interest_rate REAL NOT NULL CHECK (interest_rate > 0),
  emi_amount REAL NOT NULL CHECK (emi_amount > 0),
  loan_start_date TEXT NOT NULL,
  emi_day_of_month INTEGER NOT NULL CHECK (emi_day_of_month BETWEEN 1 AND 31),
  last_balance_update_date TEXT NOT NULL,
  created_at TEXT NOT NULL
);
```

A Room migration is required: add `emi_amount`, `loan_start_date`, `emi_day_of_month`, `last_balance_update_date` columns; drop `minimum_monthly_payment`; backfill `loan_start_date` and `last_balance_update_date` from existing `created_at` for pre-existing rows, and `emi_amount` from the old `minimum_monthly_payment` value.

### investments
```sql
CREATE TABLE investments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  asset_class TEXT NOT NULL,
  invested_amount REAL NOT NULL CHECK (invested_amount > 0),
  current_value REAL NOT NULL CHECK (current_value >= 0),
  date TEXT NOT NULL
);
```

*(Locked — unchanged.)*

`expense_categories`, `expenses`, and `budgets` tables are dropped as part of this revision's migration.

---

## Development Phases

### Phase 1: Foundation & Core Database — COMPLETE (locked)
- Room database setup, DAOs, Hilt DI container, repository pattern base, navigation structure
- **Deliverable**: Database layer functional, all entities persist and retrieve correctly

### Phase 2: Expense Tracking — REMOVED
- Previously implemented; removed from scope per PRD revision. Code deleted in this pass.

### Phase 3: Budget Management — REMOVED
- Previously implemented; removed from scope per PRD revision. Code deleted in this pass.

### Phase 4: Loan Tracking & Prepayment Calculator — REVISED (this pass)
- Update `Loan` domain model, entity, DAO, repository for `emi_amount`, `loan_start_date`, `emi_day_of_month`, `last_balance_update_date`
- Room migration dropping `minimum_monthly_payment`, adding new columns, backfilling existing rows
- New `RecalculateLoanBalancesUseCase` implementing the auto-update cycle math
- `LoanViewModel` invokes recalculation on screen load, before existing CRUD/calculator logic
- Retain manual `UpdateLoanBalanceUseCase` for override
- Input validation: name required, balance >= 0, interest rate > 0, emi_amount > 0, emi_day_of_month valid
- **Deliverable**: Loans auto-update on screen open; prepayment calculator continues to function against the live balance

### Phase 5: Investment Tracking — COMPLETE (locked)
- Unchanged: add, edit, delete, list, update current value, filter by asset class
- **Deliverable**: Complete investment tracking across all asset classes

### Phase 6: Dashboard & UI Polish — REVISED (this pass)
- Dashboard screen: total loan balance, total investment gain/loss (monthly spending and budget headroom removed)
- Tappable dashboard metrics navigating to detail screens
- Error handling UI across app (validation messages, ErrorDialog)
- Dark mode support for all screens including the prepayment calculator
- **Deliverable**: Complete app with two-metric dashboard, error handling, dark mode

---

## Testing Strategy

### Domain Layer (Unit Tests)
- Test all use cases with mocked repositories
- Test input validation rules
- Test financial calculations (loan auto-update cycle math, investment gain/loss)

### Repository Layer (Unit Tests)
- Mock Room DAOs
- Test repository implementations
- Verify entity ↔ domain model transformations

### ViewModel Layer (Unit Tests)
- Mock use cases
- Test ViewModel state emission, including auto-update triggering on load

### UI Layer
- Manual testing only
- Verify screens render correctly in light and dark mode
- Test prepayment calculator and auto-update with various loan inputs end-to-end on device

---

## Success Criteria

1. All MVP features (loan, investment, dashboard) fully functional
2. Fully offline operation; zero network calls at any time
3. Data persists correctly in SQLite between sessions
4. Loan balances recalculate automatically from start date, EMI date, and EMI amount whenever the Loans screen opens, with manual override still available
5. Prepayment calculator on the Loans screen correctly computes new tenure, years saved, and interest saved from real loan data
6. Dashboard displays accurate summaries for loan and investment domains
7. Input validation enforces all business rules
8. Error handling provides user-friendly feedback
9. Dark mode supported throughout including the prepayment calculator
10. Clean Architecture maintained with clear layer separation
11. Unit tests cover all domain use cases and ViewModel state transitions
12. App runs without crashes on Android API 24+ devices

---
