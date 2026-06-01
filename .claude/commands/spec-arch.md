---
description: Read PRD.md and produce docs/ARCHITECTURE.md
model: haiku
---
Read docs/PRD.md. Do NOT write code. Do NOT install packages.

## Step 1 — Stack proposal
One language+framework (with one-line reason), one database (with reason),
required libraries only, folder tree (max 2 levels), core data models (no code).

## Step 2 — Overengineering audit (mandatory)
For each item: 'Is this required by a PRD feature, or am I adding it speculatively?'
Mark speculative items. Ask keep or cut. Wait for answer before continuing.

## Step 3 — Phase plan
Phase 1: Foundation only (no business logic, independently runnable)
Each phase adds exactly one feature group, independently testable.

## Step 4 — Save
Write docs/ARCHITECTURE.md. Print phase count. Stop.
