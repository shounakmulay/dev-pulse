---
name: ktlint-fix
description: Use when explicitly asked to run the legacy ktlint workflow; first check whether formatting tasks actually exist.
---

This repository does not register ktlintCheck or ktlintFormat. Do not run nonexistent tasks or install a formatter. Read `.agents/skills/verify-change/SKILL.md` and use the configured lint and scoped verification commands. If the user specifically needs formatting, report the missing formatter and propose a separate setup change.
