# ARCHITECTURE DOCUMENT
## Personal Finance & Investment Tracker

---

## Technology Stack

### Language & Framework
- **Kotlin** with **Jetpack Compose** for Android UI
- **Room** (SQLite wrapper) for local data persistence
- **Kotlin Coroutines** for asynchronous operations
- **Hilt** for dependency injection
- **MVVM** architectural pattern

### Prepayment Calculator
- **Scope**: Loans screen only — amortization-based prepayment scenario calculator
- **Implementation**: Pure Kotlin math (no external library); runs fully offline
- **Inputs**: Lump sum prepayment, annual prepayment, EMI increase per month
- **Outputs**: New tenure, years saved, interest saved, total interest under new scenario

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
- Pure Kotlin math — amortization calculations for loan prepayment scenarios

---

## Folder Structure

```
app/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── ExpenseDao.kt
│   │   │   ├── ExpenseCategoryDao.kt
│   │   │   ├── BudgetDao.kt
│   │   │   ├── LoanDao.kt
│   │   │   └── InvestmentDao.kt
│   │   ├── database/
│   │   │   └── FinanceDatabase.kt
│   │   └── repository/
│   │       ├── ExpenseRepository.kt
│   │       ├── ExpenseCategoryRepository.kt
│   │       ├── BudgetRepository.kt
│   │       ├── LoanRepository.kt
│   │       └── InvestmentRepository.kt
│   └── model/
│       ├── ExpenseEntity.kt
│       ├── ExpenseCategoryEntity.kt
│       ├── BudgetEntity.kt
│       ├── LoanEntity.kt
│       └── InvestmentEntity.kt
├── domain/
│   ├── model/
│   │   ├── Expense.kt
│   │   ├── ExpenseCategory.kt
│   │   ├── Budget.kt
│   │   ├── Loan.kt
│   │   ├── Investment.kt
│   │   └── AssetClass.kt         ← enum: STOCKS, MUTUAL_FUNDS, FIXED_DEPOSITS
│   ├── repository/
│   │   ├── IExpenseRepository.kt
│   │   ├── IExpenseCategoryRepository.kt
│   │   ├── IBudgetRepository.kt
│   │   ├── ILoanRepository.kt
│   │   └── IInvestmentRepository.kt
│   └── usecase/
│       ├── expense/
│       ├── budget/
│       ├── loan/
│       └── investment/
├── presentation/
│   ├── screen/
│   │   ├── HomeScreen.kt
│   │   ├── ExpenseListScreen.kt
│   │   ├── ExpenseDetailScreen.kt
│   │   ├── BudgetScreen.kt
│   │   ├── LoanScreen.kt         ← includes prepayment calculator panel
│   │   ├── InvestmentScreen.kt
│   │   ├── DashboardScreen.kt
│   │   └── CategoryManagementScreen.kt
│   ├── viewmodel/
│   │   ├── ExpenseViewModel.kt
│   │   ├── BudgetViewModel.kt
│   │   ├── LoanViewModel.kt      ← loan CRUD + selected loan for calculator
│   │   ├── InvestmentViewModel.kt
│   │   ├── DashboardViewModel.kt
│   │   └── CategoryViewModel.kt
│   ├── component/
│   │   ├── ExpenseItem.kt
│   │   ├── BudgetCard.kt
│   │   ├── LoanCard.kt
│   │   ├── InvestmentCard.kt
│   │   ├── PrepaymentCalculatorPanel.kt ← prepayment scenario calculator
│   │   ├── CategorySelector.kt
│   │   └── ErrorDialog.kt
│   └── navigation/
│       └── NavGraph.kt
└── MainActivity.kt
```

---

## Core Data Models

### Expense
```
id: Long (primary key, auto-generated)
amount: Double (> 0)
category_id: Long? (optional, foreign key → expense_categories)
date: LocalDate
```

### ExpenseCategory
```
id: Long (primary key, auto-generated)
name: String (required)
is_predefined: Boolean
```

### Budget
```
id: Long (primary key, auto-generated)
category_id: Long (foreign key → expense_categories)
monthly_limit: Double (> 0)
month_year: YearMonth
```

### Loan
```
id: Long (primary key, auto-generated)
name: String (required)
current_balance: Double (>= 0)
interest_rate: Double (annual %, > 0)
minimum_monthly_payment: Double (>= 0)
created_at: LocalDate
```

### Investment
```
id: Long (primary key, auto-generated)
name: String (required)
asset_class: AssetClass (enum, required)
invested_amount: Double (> 0)
current_value: Double (>= 0)
date: LocalDate
```

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
- **Responsibilities**: Business rules, validation, financial calculations

### Data Layer
- **Entities**: Room database models
- **DAOs**: Direct database access
- **Repositories**: Implement domain interfaces, transform entities ↔ domain models
- **Database**: Room SQLite setup and migrations
- **Responsibilities**: Persist and retrieve data

---

## Prepayment Calculator

`PrepaymentCalculatorPanel` is a stateless Compose component in the presentation layer. It receives a `Loan?` and uses pure Kotlin amortization math to compute:

- **Remaining tenure** — months to pay off at current EMI and rate
- **New tenure** — simulate month-by-month with lump sum, annual prepayment, and EMI increase applied
- **Interest saved** — difference in total interest paid between baseline and new scenario
- **Total interest (new)** — total interest under the prepayment scenario

No external library is required. All calculations run synchronously on the UI thread (negligible cost for typical loan durations).

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
Occurs in domain use cases. Rules: amount > 0, balance >= 0, interest rate > 0 if provided, required fields populated, valid dates.

### Error Handling
Repository methods may throw. ViewModels catch exceptions and expose error messages in state. UI displays `ErrorDialog` composable with user-friendly messages.

### Dark Mode
Jetpack Compose respects system theme via `isSystemInDarkTheme()`. Color scheme defined in theme composables, consistent across all screens.

### Offline-First
All data in SQLite locally. No network calls are made at any point. Loan repayment guidance uses a built-in amortization calculator with no external dependencies.

---

## Database Schema

### expense_categories
```sql
CREATE TABLE expense_categories (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  is_predefined INTEGER NOT NULL DEFAULT 0
);
```

### expenses
```sql
CREATE TABLE expenses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  amount REAL NOT NULL CHECK (amount > 0),
  category_id INTEGER,
  date TEXT NOT NULL,
  FOREIGN KEY (category_id) REFERENCES expense_categories(id)
);
```

### budgets
```sql
CREATE TABLE budgets (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  category_id INTEGER NOT NULL,
  monthly_limit REAL NOT NULL CHECK (monthly_limit > 0),
  month_year TEXT NOT NULL,
  UNIQUE(category_id, month_year),
  FOREIGN KEY (category_id) REFERENCES expense_categories(id)
);
```

### loans
```sql
CREATE TABLE loans (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  current_balance REAL NOT NULL CHECK (current_balance >= 0),
  interest_rate REAL CHECK (interest_rate > 0),
  minimum_monthly_payment REAL CHECK (minimum_monthly_payment >= 0),
  created_at TEXT NOT NULL
);
```

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

---

## Development Phases

### Phase 1: Foundation & Core Database
- Room database setup with all five entities (Expense, ExpenseCategory, Budget, Loan, Investment)
- DAOs for all entities
- Hilt DI container initialization
- Repository pattern base implementation
- Navigation structure with empty screens
- **Deliverable**: Database layer functional, all entities persist and retrieve correctly

### Phase 2: Expense Tracking
- Expense use cases: add, edit, delete, list, filter by date range
- ExpenseCategory use cases: predefined categories, custom creation, list
- Expense list screen with date range filtering
- Expense add/edit screen with category selector
- Input validation: amount > 0, required fields, valid dates
- **Deliverable**: Full expense CRUD workflow with categories

### Phase 3: Budget Management
- Budget use cases: set, edit, delete, calculate spent vs limit
- Budget screen for setting monthly budgets
- "Budget: ₹X | Spent: ₹Y" calculation and display, overspent visual indicator
- Input validation: limit > 0, required fields
- **Deliverable**: Budget tracking with monthly spent calculation

### Phase 4: Loan Tracking & Prepayment Calculator
- Loan use cases: add, edit, delete, list, update balance
- Loan list screen with CRUD interface, interest rate and minimum payment fields
- `PrepaymentCalculatorPanel` composable: input boxes for lump sum, annual prepayment, EMI increase
- Real-time amortization calculation for new tenure, years saved, interest saved
- Input validation: name required, balance >= 0, interest rate > 0 if provided
- **Deliverable**: Complete loan management + prepayment scenario calculator

### Phase 5: Investment Tracking
- Investment use cases: add, edit, delete, list, update current value, filter by asset class
- Investment screen with asset class grouping/filter
- Gain/loss calculation (₹ and %) per investment and total
- Input validation: name required, invested amount > 0, current value >= 0
- **Deliverable**: Complete investment tracking across all asset classes

### Phase 6: Dashboard & UI Polish
- Dashboard screen: total monthly spending, total loan balance, total investment gain/loss, budget headroom
- Tappable dashboard metrics navigating to detail screens
- Error handling UI across app (validation messages, ErrorDialog)
- Dark mode support for all screens including the prepayment calculator
- Final navigation and UX refinement
- **Deliverable**: Complete app with unified dashboard, error handling, dark mode

---

## Testing Strategy

### Domain Layer (Unit Tests)
- Test all use cases with mocked repositories
- Test input validation rules
- Test financial calculations (budget spent vs limit, investment gain/loss)

### Repository Layer (Unit Tests)
- Mock Room DAOs
- Test repository implementations
- Verify entity ↔ domain model transformations

### ViewModel Layer (Unit Tests)
- Mock use cases
- Test ViewModel state emission

### UI Layer
- Manual testing only
- Verify screens render correctly in light and dark mode
- Test prepayment calculator with various loan inputs end-to-end on device

---

## Success Criteria

1. All MVP features (expense, budget, loan, investment, dashboard) fully functional
2. Fully offline operation; zero network calls at any time
3. Data persists correctly in SQLite between sessions
4. Prepayment calculator on the Loans screen correctly computes new tenure, years saved, and interest saved from real loan data
5. Dashboard displays accurate summaries for all four financial domains
6. Input validation enforces all business rules
7. Error handling provides user-friendly feedback
8. Dark mode supported throughout including the prepayment calculator
9. Clean Architecture maintained with clear layer separation
10. Unit tests cover all domain use cases and ViewModel state transitions
11. App runs without crashes on Android API 24+ devices

---
