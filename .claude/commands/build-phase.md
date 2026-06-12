---
description: Build phase of the app — scaffold, implement, test, commit
argument-hint: "<phase-num>"
model: sonnet
context: fork
allowed-tools: Bash, Read, Write, Edit, MultiEdit, Glob, Grep
---

## PHASE NUMBER — READ THIS FIRST
PHASE_NUM = $ARGUMENTS (take the first token as the integer phase number).
You MUST implement exactly Phase PHASE_NUM as numbered in docs/ARCHITECTURE.md.
Do NOT auto-detect the phase from git history, existing files, or any other heuristic.
If $ARGUMENTS is "1", implement Phase 1. If "3", implement Phase 3. No exceptions.

ROLE: Senior engineer implementing a single, spec-referenced phase
TASK: Implement Phase PHASE_NUM of the app as defined in docs/ARCHITECTURE.md, with every piece of code traceable to a PRD requirement
CONTEXT: Spec is in docs/PRD.md (requirements numbered R-1, R-2, …). Architecture phase plan is in docs/ARCHITECTURE.md. Conventions are in CLAUDE.md. Previous phases are already committed — do not touch them unless broken.
OUTPUT FORMAT: Working code committed to git, plus the report in Step 6
STOP CONDITIONS: Implement Phase PHASE_NUM ONLY. Nothing from Phase PHASE_NUM+1. No speculative abstractions. No refactoring outside this phase's scope.

## Setup
Read these files before doing anything else:
1. CLAUDE.md — conventions and commands
2. docs/PRD.md — requirements (note the R-## numbers)
3. docs/ARCHITECTURE.md — read the FULL file; extract the Phase PHASE_NUM section AND the folder structure, data models, and database schema sections

Extract from docs/ARCHITECTURE.md for Phase PHASE_NUM:
- The exact bullet list of deliverables for Phase PHASE_NUM
- Every file path mentioned in the folder structure that belongs to Phase PHASE_NUM's scope
- Every data model definition (field names, types, constraints) relevant to this phase
- Every database schema (SQL) relevant to this phase

These extracted definitions are your source of truth for field names, file placement, and layer boundaries. Do NOT invent field names or file paths — use exactly what docs/ARCHITECTURE.md specifies.

Current branch:          !git branch --show-current
Last commit:             !git log --oneline -1
Uncommitted changes:     !git status --short

## Rules (enforce all — do not skip any)
- Every implemented function or class must include a comment citing its PRD requirement: `// R-##`
- Follow every naming convention, file placement rule, and layer constraint in CLAUDE.md exactly
- Field names in domain models, entities, and DAOs must match docs/ARCHITECTURE.md exactly (e.g. `minimumMonthlyPayment`, not `emi`)
- File paths must match the folder structure in docs/ARCHITECTURE.md exactly
- Database column names must match the SQL schema in docs/ARCHITECTURE.md exactly
- Only implement files and features listed in Phase PHASE_NUM's deliverables — nothing from other phases
- If unsure about a design decision → STOP and ask. Never assume.
- Do not refactor code from previous phases unless it is demonstrably broken by this phase's changes.
- Clean up any temp or scratch files before finishing.

## Step 1 — Scaffold
Create the folder structure and empty files for Phase PHASE_NUM.
Show the file tree. STOP. Wait for explicit approval before writing any logic.

## Step 2 — Implement
Write logic file by file, in dependency order (models before repositories, repositories before use cases, use cases before ViewModels, ViewModels before screens).
After EACH file:
- Print a 2-line summary: what it does | what it exports
- Confirm it satisfies the PRD requirement(s) it implements (cite R-##)
Do not move to the next file until the current one is complete.

## Step 3 — Self-review
Before running tests, verify:
- Every public class/function has a `// R-##` citation
- Every file follows CLAUDE.md naming and placement conventions
- No code belonging to Phase PHASE_NUM+1 crept in — remove it if so
- No hardcoded values that should be constants or config

## Step 4 — Test
Run: `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ANDROID_HOME=/home/pratik/Android/Sdk ./gradlew test --no-daemon`
- If tests fail: fix them now. Do NOT proceed with failing tests.

## Step 5 — Commit
Stage only the files created or modified in this phase. Do NOT use `git add -A`.
Commit message must be exactly:
```
git commit -m "Phase PHASE_NUM: [short phase title from ARCHITECTURE.md]

Implements PRD requirements: [R-## list]"
```
Do NOT add bullet points, file lists, or any other content to the commit body.

## Step 6 — Report and stop
Print exactly:
```
Phase PHASE_NUM complete.
Files created: [list]
Requirements covered: [R-## list]
Tests: PASS / FAIL
Commit: [hash]
```

Do NOT start Phase PHASE_NUM+1. Stop here and wait.
