---
name: ktlint-fix
description: Run ktlint formatter then verify no remaining violations. Use after making code changes or when linting errors are reported.
disable-model-invocation: true
allowed-tools: Bash(./gradlew *)
---

Fix all ktlint violations in the project.

1. Run the formatter to auto-fix violations:
   ```
   ./gradlew ktlintFormat
   ```

2. Run the check to verify no remaining violations:
   ```
   ./gradlew ktlintCheck
   ```

3. If `ktlintCheck` still reports violations after formatting, read each flagged file and fix the remaining issues manually (these are typically violations the formatter cannot auto-correct, such as wildcard imports or incorrect file naming).

4. Re-run `./gradlew ktlintCheck` until it passes with no errors.
