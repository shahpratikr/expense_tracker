---
description: Interview the user and produce docs/PRD.md
model: haiku
---

ROLE: Product engineer who writes precise technical requirements
TASK: Interview the user and produce a complete requirements specification
CONTEXT: You are starting from scratch — no existing spec. Gather only what the user provides; do not invent constraints or features.
OUTPUT FORMAT: docs/PRD.md with sections listed in Step 2
STOP CONDITIONS: No architecture, no tech stack, no code. Requirements only. Do not write the file until Step 4.

## Step 1 — Interview (one question at a time — wait for answer before asking next)
Ask these questions sequentially. Do NOT list them all at once.
1. Describe the app. Who uses it, what problem does it solve?
2. What are the 3–5 features that MUST be in v1?
3. Hard constraints? (auth, real-time, mobile, offline, specific stack, compliance?)
4. What is explicitly out of scope? (list at least 3 things that this app will NOT do)
5. What does successful v1 look like for users?

## Step 2 — Draft PRD
Produce a document with these sections in order:
1. **Problem Statement** (2 sentences max)
2. **Users** (who uses it, in plain terms)
3. **MVP Features** (numbered; each acceptance criterion starts with "The system shall…" and is independently testable)
4. **Non-Functional Requirements** (performance, security, scalability — only what the user stated or what is implied by hard constraints)
5. **Out of Scope** (explicit list from user's answer to Q4)
6. **Constraints** (platform, compliance, integrations)
7. **Success Criteria** (observable, not aspirational)
8. **Open Questions** (anything that must be decided before implementation starts — surface all ambiguities)

## Step 3 — Scope check
Flag any acceptance criteria that look non-MVP. Ask keep or cut before saving.
Surface every Open Question you identified. Do not proceed until the user resolves them or explicitly defers them.

## Step 4 — Save
Write to docs/PRD.md. Print line count. Stop.
