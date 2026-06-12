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

### On-Device AI
- **Model**: Gemma 3 1B (INT4 quantized, ~600MB)
- **Runtime**: MediaPipe LLM Inference API (`com.google.mediapipe:tasks-genai`)
- **Scope**: Loans screen only — conversational repayment Q&A
- **Inference**: Fully local after one-time model download; no internet required at runtime

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
- `com.google.mediapipe:tasks-genai` — On-device LLM inference

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
│   │   ├── LoanScreen.kt         ← includes LLM chat panel
│   │   ├── InvestmentScreen.kt
│   │   ├── DashboardScreen.kt
│   │   └── CategoryManagementScreen.kt
│   ├── viewmodel/
│   │   ├── ExpenseViewModel.kt
│   │   ├── BudgetViewModel.kt
│   │   ├── LoanViewModel.kt      ← owns LlmInferenceHelper lifecycle
│   │   ├── InvestmentViewModel.kt
│   │   ├── DashboardViewModel.kt
│   │   └── CategoryViewModel.kt
│   ├── component/
│   │   ├── ExpenseItem.kt
│   │   ├── BudgetCard.kt
│   │   ├── LoanCard.kt
│   │   ├── InvestmentCard.kt
│   │   ├── LoanChatPanel.kt      ← chat UI for LLM interaction
│   │   ├── CategorySelector.kt
│   │   └── ErrorDialog.kt
│   └── navigation/
│       └── NavGraph.kt
├── ai/
│   └── LlmInferenceHelper.kt     ← wraps MediaPipe LlmInference, builds system prompt
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

### AI Layer (`ai/`)
- Sits outside Clean Architecture layers — it is an infrastructure concern, not a domain concern
- `LlmInferenceHelper` wraps the MediaPipe `LlmInference` object
- Owned and lifecycle-managed by `LoanViewModel`
- Receives a list of `Loan` domain models, formats them into a system prompt, and appends user messages
- Returns streamed response text via a `Flow<String>`

---

## On-Device LLM Integration

### Setup Steps

**1. Add dependency to `app/build.gradle.kts`:**
```kotlin
implementation("com.google.mediapipe:tasks-genai:0.10.22")
```

**2. Download the model file on first launch.**
The model (~600MB) is downloaded once from Google's servers and stored in the app's internal storage. On all subsequent launches, it loads from local storage with no network access.

```kotlin
// Trigger download on first launch (e.g., in MainActivity or a splash screen)
val modelPath = context.filesDir.absolutePath + "/gemma3-1b-it-int4.bin"
if (!File(modelPath).exists()) {
    // Download from: https://storage.googleapis.com/mediapipe-models/llm_inference/gemma3-1b-it-int4/android/latest/gemma3-1b-it-int4.bin
    // Show download progress UI — this is the only internet call the app makes
}
```

**3. Initialize `LlmInference` with the local model path:**
```kotlin
val options = LlmInference.LlmInferenceOptions.builder()
    .setModelPath(modelPath)
    .setMaxTokens(1024)
    .setTopK(40)
    .setTemperature(0.7f)
    .setRandomSeed(101)
    .build()
val llmInference = LlmInference.createFromOptions(context, options)
```

**4. Build the system prompt from live loan data:**
```kotlin
fun buildSystemPrompt(loans: List<Loan>): String {
    val loanSummary = loans.joinToString("\n") { loan ->
        val rate = loan.interestRate?.let { "@ ${it}% p.a." } ?: "(interest rate unknown)"
        val emi = loan.minimumMonthlyPayment?.let { ", min payment ₹${it}/month" } ?: ""
        "- ${loan.name}: ₹${loan.currentBalance} balance $rate$emi"
    }
    return """
        You are a personal loan repayment advisor for an Indian user. Currency is ₹ (INR).
        The user has the following loans:
        $loanSummary

        Answer only questions about loan repayment strategies using this data.
        If interest rate is missing for a loan, ask the user to provide it for accurate calculations.
        Be concise. Do not invent figures not present in the loan data above.
    """.trimIndent()
}
```

**5. Send a user message and stream the response:**
```kotlin
llmInference.generateResponseAsync(
    systemPrompt + "\n\nUser: " + userMessage,
    object : LlmInference.LlmInferenceResultListener {
        override fun onResult(partialResult: String, done: Boolean) {
            // emit partial tokens to StateFlow for streaming UI
        }
        override fun onError(error: RuntimeException) {
            // expose error in ViewModel state
        }
    }
)
```

### LLM Loading State in LoanViewModel
```
LlmState.Idle         — not yet started
LlmState.Loading      — model initializing (show spinner, hide chat input)
LlmState.Ready        — model loaded (show chat input)
LlmState.Generating   — awaiting response (disable send button)
LlmState.Error(msg)   — initialization or inference failed
```

The LLM is initialized when `LoanViewModel` is created (i.e., when the Loans screen is first opened). It is released in `onCleared()`.

---

## Dependency Injection (Hilt)

- Database is application-scoped singleton
- All `@HiltViewModel` classes inject use cases, never DAOs directly
- `LlmInferenceHelper` is injected into `LoanViewModel` as an application-scoped singleton so the model is not reloaded on recomposition
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
All data in SQLite locally. The only network call the app ever makes is the one-time Gemma model download on first launch. All LLM inference runs fully on-device.

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

### Phase 4: Loan Tracking & LLM Integration
- Loan use cases: add, edit, delete, list, update balance
- Loan list screen with CRUD interface, interest rate and minimum payment fields
- `LlmInferenceHelper` implementation with system prompt builder
- One-time model download flow with progress indicator
- `LoanViewModel` LLM loading state machine
- `LoanChatPanel` composable: streaming chat UI within Loans screen
- Input validation: name required, balance >= 0, interest rate > 0 if provided
- **Deliverable**: Complete loan management + conversational AI repayment advisor

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
- Dark mode support for all screens including LLM chat panel
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
- Mock use cases and `LlmInferenceHelper`
- Test ViewModel state emission
- Test LLM loading state transitions

### UI Layer
- Manual testing only
- Verify screens render correctly in light and dark mode
- Test LLM chat interaction end-to-end on device

---

## Success Criteria

1. All MVP features (expense, budget, loan, investment, dashboard) fully functional
2. Offline operation after initial model download; zero network calls during normal use
3. Data persists correctly in SQLite between sessions
4. LLM loads on the Loans screen and answers free-form repayment questions grounded in real loan data
5. Dashboard displays accurate summaries for all four financial domains
6. Input validation enforces all business rules
7. Error handling provides user-friendly feedback
8. Dark mode supported throughout including the chat panel
9. Clean Architecture maintained with clear layer separation
10. Unit tests cover all domain use cases and ViewModel state transitions
11. App runs without crashes on Android API 24+ devices

---
