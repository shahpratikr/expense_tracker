# Personal Finance & Investment Tracker
@docs/PRD.md
@docs/ARCHITECTURE.md

## Commands
```bash
./gradlew build                    # Build the app
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ANDROID_HOME=/home/pratik/Android/Sdk ./gradlew test --no-daemon
./gradlew test --tests "path.Class" # Run specific test
./gradlew installDebug             # Deploy to emulator/device
./gradlew ktlintFormat             # Format Kotlin code
./gradlew lint                     # Check Android lint
```

## Conventions
- Repository interfaces: I-prefix required (ILoanRepository, not LoanRepository)
- ViewModels expose immutable StateFlow<UiState>, never expose raw values
- All @HiltViewModel inject use cases, never DAOs directly
- Database is application-scoped singleton via Hilt
- Loan auto-update (RecalculateLoanBalancesUseCase) runs on Loans screen load, before other loan state is emitted
- Input validation in domain use cases: balance >= 0, interest rate > 0, emi_amount > 0, emi_day_of_month 1-31, required fields, valid dates
- Entity ↔ domain model transforms in data layer repositories
- Error handling: repositories throw; ViewModels catch and expose via state
- Presentation/Domain/Data layer separation — no Room imports in domain
- Loan schema changes require a Room migration (never destructive fallback) with backfill for existing rows

## Constraints
- Never import Room in domain layer
- No cloud sync, network calls, or multi-user support
- Single currency per app (₹ INR fixed at build time)
- No AI/LLM features anywhere in the app; loan guidance is the deterministic prepayment calculator only
- No expense tracking, categories, or budgets — out of scope, do not reintroduce
- No background jobs, WorkManager, or scheduled reminders — loan balance updates trigger only on screen load
- All unit tests use mocked dependencies, no real database
- Loans are liabilities only; no receivables
