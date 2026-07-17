# Quickstart: Feed Query Engine Validation

## Prerequisites

- Work from the repository root.
- Keep generated schema JSON diffs when schema or index annotations change.
- Use targeted DB and compile checks first; broader feed repository tests may include unrelated stale failures in this checkout.

## Validation Commands

Run DB query and paging tests:

```sh
./gradlew :core:data:db:jvmTest
```

Compile shared DB code for iOS:

```sh
./gradlew :core:data:db:compileKotlinIosSimulatorArm64
```

Compile repository callers after query API changes:

```sh
./gradlew :core:data:feed:compileKotlinIosSimulatorArm64
```

Compile domain and feature callers if expanded query options are exposed above data:

```sh
./gradlew :core:domain:feed:compileKotlinIosSimulatorArm64
./gradlew :feature:feed:compileKotlinIosSimulatorArm64
```

Check whitespace and patch safety:

```sh
git diff --check
```

## Scenario Checks

Default timeline:

- Request no filters with `PublishedNewest`.
- Expect posts ordered by published sort time, updated time, and ID.
- Expect no offset behavior and no duplicate IDs across appended pages.

Feed detail timeline:

- Request `FeedIds` with one feed ID and `PublishedNewest`.
- Expect only that feed's posts.
- Expect cursor refresh around an anchor to keep the anchor neighborhood stable.

Bookmarked timeline:

- Request `Bookmarked=true` with `PublishedNewest`.
- Expect only bookmarked posts.
- Verify the common optimized profile is selected.

Feed bookmarked timeline:

- Request `FeedIds` plus `Bookmarked=true` with `PublishedNewest`.
- Expect only bookmarked posts from selected feeds.
- Verify the common optimized profile is selected.

Media filters:

- Request `HasAudio=true`, `HasVideo=false`, and default sort.
- Expect each result to have derived audio presence and no derived video presence.
- Repeat with missing media fields to confirm false derivation.

Category filtering:

- Request `Category` with at least two normalized values.
- Expect exact category matches through post-category values.
- Verify substring-only matches do not appear.

Search filtering:

- Request `SearchText` with punctuation and wildcard-like characters.
- Expect escaped matching and no unexpected broadening.
- Verify it is classified as rare unless promoted.

Sort coverage:

- Exercise every supported sort with at least one page append.
- Expect each cursor shape to match the sort terms.
- Expect no duplicates or skipped IDs compared with the full ordered result set.

Performance matrix:

- Seed at least 10,000 posts across at least 200 feeds.
- Run each common optimized profile.
- Expect first visible page under 1 second for 95% of attempts and next page under 500 ms for 95% of attempts on target development hardware.
