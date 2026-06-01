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

### Database
- **SQLite** via Room ORM
- Local-only, no cloud synchronization
- Single-user per installation

### Key Libraries
- `androidx.compose.ui` — UI framework
- `androidx.room` — Database & DAOs
- `androidx.lifecycle` — ViewModels, lifecycle awareness
- `com.google.dagger.hilt` — Dependency injection
- `kotlinx.coroutines` — Async/await

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
│   │   │   └── LoanDao.kt
│   │   ├── database/
│   │   │   └── FinanceDatabase.kt
│   │   └── repository/
│   │       ├── ExpenseRepository.kt
│   │       ├── ExpenseCategoryRepository.kt
│   │       ├── BudgetRepository.kt
│   │       └── LoanRepository.kt
│   └── model/
│       ├── ExpenseEntity.kt
│       ├── ExpenseCategoryEntity.kt
│       ├── BudgetEntity.kt
│       └── LoanEntity.kt
├── domain/
│   ├── model/
│   │   ├── Expense.kt
│   │   ├── ExpenseCategory.kt
│   │   ├── Budget.kt
│   │   └── Loan.kt
│   ├── repository/
│   │   ├── IExpenseRepository.kt
│   │   ├── IExpenseCategoryRepository.kt
│   │   ├── IBudgetRepository.kt
│   │   └── ILoanRepository.kt
│   └── usecase/
│       ├── expense/
│       ├── budget/
│       └── loan/
├── presentation/
│   ├── screen/
│   │   ├── HomeScreen.kt
│   │   ├── ExpenseListScreen.kt
│   │   ├── ExpenseDetailScreen.kt
│   │   ├── BudgetScreen.kt
│   │   ├── LoanScreen.kt
│   │   ├── DashboardScreen.kt
│   │   └── CategoryManagementScreen.kt
│   ├── viewmodel/
│   │   ├── ExpenseViewModel.kt
│   │   ├── BudgetViewModel.kt
│   │   ├── LoanViewModel.kt
│   │   ├── DashboardViewModel.kt
│   │   └── CategoryViewModel.kt
│   ├── component/
│   │   ├── ExpenseItem.kt
│   │   ├── BudgetCard.kt
│   │   ├── LoanCard.kt
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
category_id: Long? (optional, foreign key)
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
category_id: Long (foreign key)
monthly_limit: Double (> 0)
month_year: YearMonth
```

### Loan
```
id: Long (primary key, auto-generated)
name: String (required)
current_balance: Double (>= 0)
created_at: LocalDate
```

---

## Architecture Layers

### Presentation Layer
- **Components**: Jetpack Compose UI elements, screens
- **ViewModels**: State management, business logic orchestration
- **Navigation**: Screen routing and transitions
- **Responsibilities**: Render UI, handle user input, collect VM state changes

### Domain Layer
- **Models**: Pure Kotlin data classes (Expense, Budget, Loan, etc.)
- **Repository Interfaces**: Contracts for data access
- **Use Cases**: Orchestrate repository calls, implement business rules
- **Responsibilities**: Define business rules, input validation, calculation logic

### Data Layer
- **Entities**: Room database models (ExpenseEntity, etc.)
- **DAOs**: Direct database access
- **Repositories**: Implement domain repository interfaces, bridge domain ↔ data
- **Database**: Room SQLite setup, migrations
- **Responsibilities**: Persist/retrieve data, transform between entities and domain models

### Dependency Injection (Hilt)
- Provide repository implementations to ViewModels
- Inject database instance into repositories
- Scope database instance as singleton
- Provide coroutine dispatchers for testing

---

## Key Design Decisions

### Clean Architecture
Three-layer separation enforces testability and maintainability. Domain layer remains independent of Android/Room APIs.

### MVVM Pattern
Each screen has a dedicated ViewModel that:
- Exposes data as immutable `StateFlow` for UI observation
- Handles user actions via public functions
- Delegates business logic to use cases

### Input Validation
- Occurs in domain layer (use cases)
- Enforced in presentation layer (UI fields)
- Rules: amount > 0, dates valid, required fields populated, budget limits > 0

### Error Handling
- Repository methods may throw or return error states
- ViewModels catch exceptions and expose error messages to UI
- UI displays error dialogs with user-friendly messages

### Dark Mode
- Jetpack Compose respects system theme via `isSystemInDarkTheme()`
- Color scheme defined in theme composables
- Consistent across all screens

### Offline-First
- All data stored in SQLite locally
- No network layer, no cloud sync
- Single currency per app instance (configurable in settings)

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
  created_at TEXT NOT NULL
);
```

---

## Development Phases

### Phase 1: Foundation & Core Database
- Room database setup with all entities
- DAOs for Expense, ExpenseCategory, Budget, Loan
- Hilt DI container initialization
- Repository pattern base implementation
- Navigation structure with empty screens
- **Deliverable**: Database layer functional, entities persist/retrieve correctly

### Phase 2: Expense Tracking (Core Feature)
- Expense use cases: add, edit, delete, list, filter by date
- ExpenseCategory use cases: predefined categories, custom creation, list
- Expense list screen with date range filtering
- Expense add/edit screen with category selector
- Input validation: amount > 0, required fields, valid dates
- **Deliverable**: Full expense CRUD workflow with categories

### Phase 3: Budget Management
- Budget use cases: set, edit, delete, calculate spent vs limit
- Budget screen for setting monthly budgets
- "Budget: $X | Spent: $Y" calculation and display
- Input validation: limit > 0, required fields
- **Deliverable**: Budget tracking with monthly spent calculation

### Phase 4: Loan Tracking
- Loan use cases: add, edit, delete, list, update balance
- Loan list screen with CRUD interface
- Balance update functionality
- Input validation: name required, balance >= 0
- **Deliverable**: Complete loan management workflow

### Phase 5: Dashboard & UI Polish
- Dashboard screen with total monthly spending
- Dashboard navigation from home screen
- Clickable spending amount → expense list detail view
- Error handling UI across app (validation messages, error dialogs)
- Dark mode support for all screens
- Final navigation and UX refinement
- **Deliverable**: Complete app with dashboard, error handling, dark mode

---

## Testing Strategy

### Domain Layer (Unit Tests)
- Test all use cases with mocked repositories
- Test input validation rules
- Test calculations (budget spent vs limit, etc.)

### Repository Layer (Unit Tests)
- Mock Room DAOs
- Test repository implementations
- Verify entity ↔ domain model transformations

### ViewModel Layer (Unit Tests)
- Mock use cases
- Test ViewModel state emission
- Test user action handling

### UI Layer
- Manual testing (UI tests not in scope)
- Verify screens render correctly
- Test user interactions (tap, input, navigation)

---

## Success Criteria

1. All MVP features (expense, budget, loan, dashboard) fully functional
2. Offline-only operation with no network calls
3. Data persists correctly in SQLite between sessions
4. Dashboard displays current month spending accurately
5. Input validation enforces business rules
6. Error handling provides user-friendly feedback
7. Dark mode supported throughout app
8. Clean Architecture maintained with clear layer separation
9. Unit tests cover all domain use cases
10. App runs smoothly on Android devices with no crashes

---
