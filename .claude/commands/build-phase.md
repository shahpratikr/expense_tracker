---
description: Build phase of the app — scaffold, implement, test, commit
argument-hint:  ""
model: claude-sonnet-4-20250514
allowed-tools: Bash, Read, Write, Edit, MultiEdit, Glob, Grep
---

## Context
Current branch: !git branch --show-current
Last commit:    !git log --oneline -1
Uncommitted changes: !git status --short

## Inputs
Treat $ARGUMENTS as: first token = PHASE_NUM, remainder = PHASE_DESCRIPTION
Example: "2 Add user authentication" → PHASE_NUM=2, PHASE_DESCRIPTION="Add user authentication"

## Your job
Read CLAUDE.md, docs/PRD.md, and docs/ARCHITECTURE.md.
Extract the Phase PHASE_NUM section from ARCHITECTURE.md.

## Rules (enforce all — do not skip any)
- Implement Phase PHASE_NUM ONLY. Nothing from Phase PHASE_NUM+1.
- Follow every convention in CLAUDE.md exactly.
- If unsure about a design decision → STOP and ask. Never assume.
- Do not refactor code from previous phases unless it is broken.
- Clean up any temp or scratch files before finishing.

## Step 1 — Scaffold
Create the folder structure and empty files for Phase PHASE_NUM.
Show the file tree. STOP. Wait for my explicit approval before writing any logic.

## Step 2 — Implement
Write logic file by file.
After EACH file: print a 2-line summary (what it does, what it exports).
Do not move to the next file until the current one is complete.

## Step 3 — Self-review
Before running tests, review your own changes:
- Does every file follow CLAUDE.md naming and structure conventions?
- Is there any code that belongs in Phase PHASE_NUM+1? Remove it.
- Are there any hardcoded values that should be env vars?

## Step 4 — Test
Run: !cat CLAUDE.md | grep -A1 "Test:" | tail -1 || echo "npm test"
If tests fail: fix them now. Do NOT proceed with failing tests.

## Step 5 — Commit
git add -A
git commit -m "Implemented phase PHASE_NUM"

## Step 6 — Report and stop
Print exactly:
  Phase PHASE_NUM complete.
  Files created:  [list]
  Files modified: [list]
  Tests: PASS / FAIL
  Commit: [hash]

Do NOT start Phase PHASE_NUM+1. Stop here and wait.
