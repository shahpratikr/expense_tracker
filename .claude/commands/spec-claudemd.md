---
description: Distill PRD + ARCHITECTURE into a lean CLAUDE.md under 150 lines
model: haiku
---

Read docs/PRD.md and docs/ARCHITECTURE.md.

## Rules for CLAUDE.md content
Include ONLY:
- Commands (build, test, lint, dev server)
- Conventions Claude would otherwise get wrong (naming, file placement, patterns)
- Hard constraints ("never use X", "always do Y")
- @docs/PRD.md and @docs/ARCHITECTURE.md as imports (reference, don't duplicate)

Exclude:
- Feature descriptions (in PRD.md)
- Architecture rationale (in ARCHITECTURE.md)
- Anything Claude can read from the code itself
- Aspirations or goals

## Template to fill
# [App name]
@docs/PRD.md
@docs/ARCHITECTURE.md

## Commands
- Dev: [command]
- Test: [command]
- Lint: [command]
- Build: [command]

## Conventions
- [one hard rule per line, 10–20 lines max]

## Constraints
- [absolute prohibitions, 5–10 lines max]

## Self-check (mandatory before saving)
Count the lines in CLAUDE.md.
If over 150 lines: cut the least actionable lines until under 150.
List what you cut and why.

Save to CLAUDE.md.
Print: "CLAUDE.md saved — [N] lines"
Stop.
