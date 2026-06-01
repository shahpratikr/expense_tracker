---
description: Validate a completed phase against spec, conventions, and tests
argument-hint: "<phase-num>"
model: sonnet
allowed-tools: Agent, Bash(git diff:*), Bash(git log:*)
---

## Inputs
Phase number: $ARGUMENTS
Changed files in this phase:
!git diff --name-only HEAD~1

Last commit message:
!git log --oneline -1

## Your job
You are an orchestrator only. You do NOT read files yourself.
You do NOT fix anything. You report only.

Spawn these 3 agents in parallel using the Task tool:

### Agent 1 — spec-validator
Pass:
- phase_number: $ARGUMENTS
- changed_files: (the git diff list above)
- instruction: "Check Phase $ARGUMENTS acceptance criteria from docs/PRD.md"

### Agent 2 — convention-checker
Pass:
- changed_files: (the git diff list above)
- instruction: "Check all changed files against CLAUDE.md conventions"

### Agent 3 — test-reporter
Pass:
- phase_number: $ARGUMENTS
- instruction: "Run the test suite and return structured pass/fail"

Wait for ALL 3 to complete before continuing.

## Output format
Print this exact structure — no extra commentary:

================================================
PHASE $ARGUMENTS VALIDATION REPORT
================================================

SPEC COMPLIANCE
---------------
[paste spec-validator output verbatim]

CONVENTION CHECK
----------------
[paste convention-checker output verbatim]

TEST RESULTS
------------
[paste test-reporter output verbatim]

================================================
OVERALL: [PASS / FAIL / NEEDS REVIEW]
  PASS        = all criteria YES, no violations, tests green
  NEEDS REVIEW = any PARTIAL or minor violations, tests green
  FAIL        = any NO criteria, tests red, or blocking violations
================================================

NEXT STEP: [one sentence — either "safe to start Phase N+1" or
            "fix [specific items] before proceeding"]
================================================

Do not start Phase $ARGUMENTS+1. Stop here.
