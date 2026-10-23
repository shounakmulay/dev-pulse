<p align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" width="470" height="160" srcset="https://github.com/user-attachments/assets/5481b111-6e70-4296-9f07-03e8cf55836a">
  <source media="(prefers-color-scheme: light)" width="470" height="160" srcset="https://github.com/user-attachments/assets/b56a916f-5727-4ffb-9801-e90991726f59">
  <img alt="Project Logo" src="path/to/default-image.png">
</picture>

DevPulse is a Kotlin Multiplatform productivity workspace for developers.

The long-term goal is to bring together the information and context developers interact with throughout the day — technical content, notes, activity, tools, reminders, and other useful signals — into one place that is easy to search, organize, and revisit.

Rather than being centered around a single feature, DevPulse is intended to evolve into a broader developer companion.

> **Status:** Early Active development. The product direction is intentionally evolving as the core foundations are built.

## Product Direction

DevPulse is being designed around a few core ideas:

- **Stay up to date**  
  Follow technical content, updates, feeds, and other sources relevant to your work.

- **Capture context**  
  Save notes, ideas, links, or useful information as you encounter them.

- **Remember what you worked with**  
  Keep track of things you read, viewed, or referenced throughout the day so useful context is easier to rediscover later.

- **Organize knowledge**  
  Tag, categorize, search, and connect content rather than letting useful information disappear into browser history or scattered notes.

- **Turn information into something useful**  
  Over time, DevPulse can help summarize, extract, or generate notes from the information you consume.

- **Bring developer workflows together**  
  The broader direction includes developer-focused utilities such as monitoring, calendars, timers, and other day-to-day tools.

## Code quality

Run checks with the repository's Gradle wrapper and a JDK supported by Gradle 9.3.1. CI uses JDK 21.

```bash
sh scripts/install-hooks.sh
./gradlew qualityQuick
./gradlew qualityCheck
./gradlew spotlessApply
```

`qualityQuick` runs Spotless with ktlint and one Detekt scan, including Compose Rules. It does not compile app code or run tests. The pre-push hook runs this gate without changing or staging files. Installation respects `core.hooksPath`, refuses an existing different pre-push hook, and uses Git's shared hook directory for worktrees. Integrate `.githooks/pre-push` manually when an existing hook must remain.

`bridge/readability` is excluded from formatting, Detekt and Compose rules, Android Lint reporting, test gates, and scoped verification. Consumer checks can still compile it and generate lint dependency models. Its compilation remains part of normal app builds.

Kotlin test source sets are excluded from formatting, Detekt, Compose rules and Android Lint. `qualityCheck` and the default `scripts/verify.sh` plan do not compile or execute Kotlin tests. Use `scripts/verify.sh --test MODULE CLASS` only to opt in to an individual JVM test class. CI retains the Python hook and verification-runner fixtures.

`qualityCheck` also runs each configured Android KMP library's `lintAndroidMain`, the app's `lintStagingDebug`, and JVM production compilation. It requires the Android SDK and existing development configuration. Native compilation, device tests and iOS analysis remain separate platform checks. `scripts/verify.sh` retains its offline, scoped checks and its guard against tasks that write tracked generated artifacts.

Spotless owns formatting. `.editorconfig` contains Kotlin preferences, including Compose function names. Validation never formats automatically. The fixed adoption commit `da167f6ccefe45154ecf13ce567d00019b1b63bb` limits formatting enforcement to added or changed files, avoiding hundreds of unrelated existing violations. Any file changed after that commit must be formatted in full. `spotlessApply` uses the same scope. Keep this commit available in local history; CI fetches full history.

Detekt owns Kotlin bug and exception checks, deep nesting outside composables, and selected Compose state, remember and modifier rules. It scans handwritten production common, platform and buildSrc Kotlin sources and Gradle scripts. Build and generated directories are excluded. Adjust rules in `config/detekt.yml`; use a narrow `@Suppress("RuleId")` only for an intentional exception. `config/detekt-baseline.xml` records five findings present at adoption, including four Compose findings. Remove resolved entries rather than regenerating the baseline to hide new findings. Detekt formatting and naming rules are omitted to avoid overlap with ktlint.

[Stable Detekt 1.23.8 embeds Kotlin 2.0.21](https://detekt.dev/docs/introduction/compatibility/). Its syntax scan successfully parsed the current Kotlin 2.4.10 project, but it cannot guarantee support for newer language syntax. Type-aware rules and target-specific analysis are deliberately omitted because the stable engine does not match the project compiler. [Compose Rules 0.4.23 matches this stable Detekt version](https://mrmans0n.github.io/compose-rules/detekt/). Reassess both together when a compatible stable engine is available. Android Lint supplies platform and type-aware Compose inspections; it does not replace the common/platform Kotlin scan.

CI runs one job for PRs targeting `main` and pushes to `main`. It cancels superseded runs, runs `qualityCheck` once, uses setup-gradle's dependency/build cache, and uploads reports on failure. It does not require deployment credentials. Existing configuration-cache and build-cache settings remain enabled; no new parallelism setting is imposed. Reports are under `build/reports` in the root and modules. Android Lint uses `config/lint.xml` to exclude generated files under `build/generated` while retaining errors in handwritten sources.

No Konsist or Kover dependency is added. Gradle already controls module dependencies and compilation checks platform imports. New architecture frameworks or coverage thresholds need a concrete rule or reporting requirement. Kermit remains unchanged.

The production gate continues to expose existing production compilation and Android Lint failures. These remain visible in CI and its reports. The setup does not suppress them or change application behavior to obtain a pass.
