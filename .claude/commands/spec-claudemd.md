---
description: Distill PRD + ARCHITECTURE into a lean CLAUDE.md under 150 lines
model: haiku
---

ROLE: Technical writer who produces actionable developer reference docs
TASK: Distill docs/PRD.md and docs/ARCHITECTURE.md into a lean CLAUDE.md
CONTEXT: CLAUDE.md is loaded into every future Claude session for this project. It must be dense and actionable — not a summary of the docs. The docs are already referenced via @imports and will be read separately.
OUTPUT FORMAT: CLAUDE.md using the template below, under 150 lines
STOP CONDITIONS: Do not duplicate content from PRD.md or ARCHITECTURE.md. Do not include feature descriptions, rationale, or aspirations. Reference the docs via @imports instead.

Read docs/PRD.md and docs/ARCHITECTURE.md.

## Rules for CLAUDE.md content
Include ONLY:
- Commands (build, test, lint, dev server) — exact shell commands, copy-paste ready
- Conventions Claude would otherwise get wrong (naming patterns, file placement, layer rules)
- Hard constraints ("never import X in Y layer", "always use I-prefix for interfaces")
- @docs/PRD.md and @docs/ARCHITECTURE.md as imports (reference, don't duplicate)

Exclude:
- Feature descriptions (already in PRD.md)
- Architecture rationale (already in ARCHITECTURE.md)
- Anything Claude can derive from reading the code itself
- Aspirations, goals, or explanatory prose

## Template to fill
```
# [App name]
@docs/PRD.md
@docs/ARCHITECTURE.md

## Commands
- Dev:   [command]
- Test:  [command]
- Lint:  [command]
- Build: [command]

## Conventions
- [one hard rule per line, 10–20 lines max]

## Constraints
- [absolute prohibitions only, 5–10 lines max]
```

## Self-check (mandatory before saving)
1. Count lines in the draft.
2. If over 150: cut the least actionable lines until under 150. List what you cut and why.
3. Verify every line is either a command, a convention Claude would get wrong, or a hard constraint. Remove anything else.

Save to CLAUDE.md.
Print: "CLAUDE.md saved — [N] lines"
Stop.
