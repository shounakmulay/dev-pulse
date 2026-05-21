# Feed Import and Sync Implementation Issues

Parent docs:
- PRD: `docs/prd/feed-import-sync-processing.md`
- ERD: `docs/prd/feed-import-sync-erd.md`
- ADR: `docs/adr/0001-queue-first-feed-import-and-sync.md`
- Glossary: `CONTEXT.md`

This backlog is local-only for now. It is written as issue-ready Markdown and ordered by dependency. Each slice is intended to be narrow, demoable, and safe for an AFK implementation agent unless marked otherwise.

## Proposed Breakdown

1. **Enqueue and observe Feed Processing Jobs**
   - **Type**: AFK
   - **Blocked by**: None
   - **User stories covered**: 2, 4, 15, 17, 18, 28, 29

2. **Process one feed-level core job at a time**
   - **Type**: AFK
   - **Blocked by**: 1
   - **User stories covered**: 2, 4, 17, 18

3. **Import feed metadata from a real Feed Source URL**
   - **Type**: AFK
   - **Blocked by**: 1, 2
   - **User stories covered**: 1, 2, 3, 4, 23, 28, 29

4. **Import visible articles with UUID7 local IDs and Article Fingerprints**
   - **Type**: AFK
   - **Blocked by**: 3
   - **User stories covered**: 1, 3, 7, 23, 24, 25, 26

5. **Handle incomplete articles and weak identity safely**
   - **Type**: AFK
   - **Blocked by**: 4
   - **User stories covered**: 12, 13, 14, 24, 27

6. **Sync existing feeds without deleting unseen articles**
   - **Type**: AFK
   - **Blocked by**: 4, 5
   - **User stories covered**: 8, 9, 10, 11, 24, 25, 26

7. **Support bounded batch storage and partial failure recovery**
   - **Type**: AFK
   - **Blocked by**: 4
   - **User stories covered**: 5, 6, 7, 17

8. **Add Core Processing Hook pipeline**
   - **Type**: AFK
   - **Blocked by**: 4, 5
   - **User stories covered**: 3, 20, 24, 25, 26, 27

9. **Add Enrichment Hook extension point with isolated failures**
   - **Type**: AFK
   - **Blocked by**: 7, 8
   - **User stories covered**: 3, 19, 21, 22, 31

10. **Expose minimal feed import UI path**
    - **Type**: HITL
    - **Blocked by**: 1, 3, 4, 7
    - **User stories covered**: 1, 2, 3, 4, 5, 12, 13

11. **Wire processor startup at app root**
    - **Type**: HITL
    - **Blocked by**: 2, 3, 4, 7
    - **User stories covered**: 2, 3, 4, 8, 17, 18

12. **Create end-to-end import and sync verification suite**
    - **Type**: AFK
    - **Blocked by**: 6, 7, 8, 9
    - **User stories covered**: 1-31

## Issue 1: Enqueue and observe Feed Processing Jobs

## What to build

Build the durable **Feed Processing Job** foundation. A caller should be able to enqueue a **Feed Import** for a raw **Feed Source URL**, receive a job ID, and observe the job state from the database. Repeated enqueue calls for the same exact raw source URL should coalesce while an active job exists.

This slice should not fetch or parse feeds. It proves the queue contract, job state vocabulary, exact raw URL identity, and Flow-observable state.

## Acceptance criteria

- [ ] A **Feed Processing Job** can be created for a **Feed Import** using a raw **Feed Source URL**.
- [ ] Enqueue returns a stable job ID.
- [ ] Enqueuing the same exact raw **Feed Source URL** while a job is active returns or reuses the active job instead of creating duplicates.
- [ ] Enqueuing a different raw URL creates a separate job.
- [ ] Job state includes job type, stage, execution state, source URL, counts, timestamps, and nullable error detail.
- [ ] Callers can observe job state changes as a Flow.
- [ ] Fetch URL normalization is not persisted; the raw source URL remains the stored identity.
- [ ] Tests cover enqueue, exact raw URL coalescing, separate URL creation, and observed state.

## Blocked by

None - can start immediately.

## Issue 2: Process one feed-level core job at a time

## What to build

Build the minimal feed queue processor loop that claims queued jobs and advances one feed-level **Core Processing Stage** at a time. This is a processor skeleton: it does not need to fetch real feed data yet, but it must prove serialized claiming, FIFO behavior, stage/state transitions, and safe failure marking.

## Acceptance criteria

- [ ] Processor can claim the next queued job in FIFO order.
- [ ] Only one feed-level core job can be marked running at a time.
- [ ] Claimed jobs transition from `Queued/Waiting` to `CoreProcessing/Running`.
- [ ] Successful no-op processing can transition the job to `Complete/Succeeded`.
- [ ] Failed no-op processing can transition the job to `Failed/Failed`.
- [ ] Duplicate active jobs for the same raw **Feed Source URL** cannot be processed concurrently.
- [ ] Tests cover FIFO claim, single active core job, success transition, and failure transition.

## Blocked by

- Issue 1: Enqueue and observe Feed Processing Jobs

## Issue 3: Import feed metadata from a real Feed Source URL

## What to build

Connect the queue processor to real feed parsing for the channel/feed metadata path. A user-facing enqueue call should eventually fetch a feed from the stored **Feed Source URL**, parse channel metadata, create a **Local Feed ID** as UUID7, and store the feed subscription. The processor should try a trimmed URL first and fall back to the raw stored URL if appropriate.

This slice should store feed metadata only. Article storage can come later.

## Acceptance criteria

- [ ] A queued **Feed Import** can fetch and parse feed channel metadata.
- [ ] Fetch attempts use trimmed source URL first and can fall back to raw stored source URL.
- [ ] Feed rows use UUID7 **Local Feed ID** values.
- [ ] Feed rows store raw **Feed Source URL**.
- [ ] Feed metadata preserves publisher-owned channel fields already represented by the model.
- [ ] A successful metadata import updates the job stage/state.
- [ ] Parser/fetch failures update the job as failed with useful error detail.
- [ ] Tests cover successful metadata import, trimmed URL fetch, raw URL fallback, and parser/fetch failure.

## Blocked by

- Issue 1: Enqueue and observe Feed Processing Jobs
- Issue 2: Process one feed-level core job at a time

## Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints

## What to build

Extend import processing from feed metadata to article storage. Parsed article items should produce visible local articles with UUID7 **Local Article ID** values, **Article Fingerprints** for confident matching, and remote-owned article fields stored without waiting for enrichment.

## Acceptance criteria

- [ ] A successful **Feed Import** stores parsed article rows after feed metadata is available.
- [ ] Article rows use UUID7 **Local Article ID** values.
- [ ] Article rows belong to the imported feed via **Local Feed ID**.
- [ ] Strong **Article Fingerprints** are generated from GUID when present.
- [ ] Medium **Article Fingerprints** are generated from canonical URL when GUID is absent.
- [ ] Article rows store available publisher-owned fields without enrichment work.
- [ ] Article data becomes visible through existing or updated paginated database read paths.
- [ ] Tests cover GUID fingerprint insert, canonical URL fallback insert, and visible article reads.

## Blocked by

- Issue 3: Import feed metadata from a real Feed Source URL

## Issue 5: Handle incomplete articles and weak identity safely

## What to build

Add incomplete article handling and weak identity safety. Articles with useful payload but missing important fields should still be inserted and marked as **Incomplete Articles** with **Incomplete Reasons**. Weak identity matches must not overwrite existing articles.

## Acceptance criteria

- [ ] Articles missing title can still be inserted when they have useful payload.
- [ ] Articles missing stable identity can still be inserted when they have useful payload.
- [ ] Articles missing readable content can still be inserted when they have useful payload.
- [ ] Empty items with no useful payload are not inserted.
- [ ] Incomplete articles store machine-readable reasons from the initial reason set.
- [ ] Weak title/date/author-style matches are treated as possible duplicates or safe new inserts, not overwrite candidates.
- [ ] UI-facing/domain mapping can expose article data quality and incomplete reasons.
- [ ] Tests cover each incomplete reason, useful-payload insertion, empty item rejection, and no overwrite on weak match.

## Blocked by

- Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints

## Issue 6: Sync existing feeds without deleting unseen articles

## What to build

Implement **Feed Sync** enqueue and processing for existing feeds. Sync should fetch the feed again, reconcile publisher-owned feed/article data, insert new articles, update existing articles matched by **Article Fingerprint**, preserve user-owned data, and leave unseen local articles untouched.

## Acceptance criteria

- [ ] A **Feed Sync** job can be enqueued for an existing feed.
- [ ] Sync fetches from the feed's raw **Feed Source URL**.
- [ ] New articles are inserted.
- [ ] Existing articles matched by strong or medium **Article Fingerprint** are updated with remote-owned field changes.
- [ ] User-owned fields are preserved by design, either by separate modeling or update behavior.
- [ ] Local articles missing from the latest feed payload are not deleted or hidden.
- [ ] Feed metadata can be updated from latest channel metadata.
- [ ] Tests cover new article insert, matched article update, unseen article preservation, and feed metadata update.

## Blocked by

- Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints
- Issue 5: Handle incomplete articles and weak identity safely

## Issue 7: Support bounded batch storage and partial failure recovery

## What to build

Make core storage resilient for large feeds. Article writes should happen in bounded batches, progress counts should update after each successful batch, and failures after partial storage should produce a **Partial Feed Processing Failure** without hiding already stored articles.

## Acceptance criteria

- [ ] Article storage uses bounded batches rather than one unbounded write.
- [ ] Batch size is configurable or centralized enough to tune.
- [ ] Job processed count updates after successful batches.
- [ ] Failure before any storage marks the job failed.
- [ ] Failure after one or more successful batches marks the job failed partial.
- [ ] Articles stored before partial failure remain visible.
- [ ] Retrying the job reconciles stored articles by **Article Fingerprint** without duplicating confident matches.
- [ ] Tests cover large import batching, pre-storage failure, mid-storage partial failure, visible partial data, and retry-safe upsert.

## Blocked by

- Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints

## Issue 8: Add Core Processing Hook pipeline

## What to build

Add a **Core Processing Hook** pipeline before storage. Hooks should allow cheap deterministic shaping of already-fetched feed data while preventing slow enrichment behavior from entering the core import path.

## Acceptance criteria

- [ ] Core hooks can run before feed/article storage.
- [ ] Core hooks can shape already parsed payload data, such as display field fallback, trimming, media extraction, fingerprint inputs, and viability checks.
- [ ] Hook ordering is deterministic.
- [ ] Hook outputs feed the storage-ready article/feed model.
- [ ] Core hook failures fail or skip according to explicit pipeline rules, without corrupting storage state.
- [ ] The hook API does not require network, AI, full HTML fetch, image probing, or expensive dedupe work.
- [ ] Tests cover deterministic ordering, field fallback, viability shaping, and hook failure handling.

## Blocked by

- Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints
- Issue 5: Handle incomplete articles and weak identity safely

## Issue 9: Add Enrichment Hook extension point with isolated failures

## What to build

Add the **Enrichment Hook** extension point after article storage. Enrichment should write derived data without delaying article visibility and without failing the core import/sync job when an enrichment hook fails.

This slice does not need to ship a real AI/tagging feature. It should establish the extension point and one simple deterministic sample enrichment or fake hook for verification.

## Acceptance criteria

- [ ] Enrichment starts only after core storage makes articles visible.
- [ ] Enrichment hooks receive article-level work and can be parallelized later.
- [ ] Enrichment writes derived data separately from publisher-owned and user-owned fields.
- [ ] Enrichment failures are ignored for core job success and are logged or captured only as best effort.
- [ ] The feed processing job can complete successfully even when enrichment fails.
- [ ] Enrichment retry is not implemented.
- [ ] Tests cover enrichment success, enrichment failure isolation, and no mutation of publisher-owned/user-owned article fields.

## Blocked by

- Issue 7: Support bounded batch storage and partial failure recovery
- Issue 8: Add Core Processing Hook pipeline

## Issue 10: Expose minimal feed import UI path

## What to build

Add the smallest user-visible **Feed Import** path. A user should be able to enter a feed URL, enqueue import, leave the import flow, observe import status, and see articles appear through existing feed content UI once core storage completes.

This is marked HITL because exact UI placement and return behavior were intentionally deferred.

## Acceptance criteria

- [ ] User can enter a **Feed Source URL** from an agreed feed UI entry point.
- [ ] Submitting creates or reuses a **Feed Processing Job**.
- [ ] UI does not block until parsing/storage completes.
- [ ] UI can show indeterminate core processing state.
- [ ] UI can show failure or partial failure state.
- [ ] Imported articles appear through existing feed content flows after storage.
- [ ] Incomplete articles can show a basic “Incomplete data” indicator.
- [ ] UI uses design system components and follows existing feature patterns.

## Blocked by

- Issue 1: Enqueue and observe Feed Processing Jobs
- Issue 3: Import feed metadata from a real Feed Source URL
- Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints
- Issue 7: Support bounded batch storage and partial failure recovery

## Issue 11: Wire processor startup at app root

## What to build

Decide and implement the app-level lifecycle wiring that starts feed job processing outside screen ViewModels. Screens should enqueue work and observe state; they should not own the processor lifecycle.

This is marked HITL because processor startup location was explicitly deferred.

## Acceptance criteria

- [ ] Processor lifecycle starts from an app-root or app-service location, not from a screen ViewModel.
- [ ] Processor startup does not create duplicate concurrent processors.
- [ ] Processor can resume queued jobs when the app starts.
- [ ] Processor respects one active feed-level core job at a time.
- [ ] Lifecycle behavior is documented enough for future scheduled sync work.
- [ ] Tests or integration verification cover processor startup/resume behavior where feasible.

## Blocked by

- Issue 2: Process one feed-level core job at a time
- Issue 3: Import feed metadata from a real Feed Source URL
- Issue 4: Import visible articles with UUID7 local IDs and Article Fingerprints
- Issue 7: Support bounded batch storage and partial failure recovery

## Issue 12: Create end-to-end import and sync verification suite

## What to build

Create an end-to-end verification suite around feed import and sync behavior using representative feed payloads and local test doubles where needed. The suite should verify external behavior across enqueue, processing, storage, matching, incomplete data, sync updates, and hook isolation.

## Acceptance criteria

- [ ] Tests cover full successful **Feed Import** from URL/payload to visible feed/articles.
- [ ] Tests cover full **Feed Sync** with new articles and changed remote-owned fields.
- [ ] Tests cover preservation of user-owned data.
- [ ] Tests cover no deletion of unseen articles.
- [ ] Tests cover incomplete article insertion and reason exposure.
- [ ] Tests cover partial failure and retry-safe reconciliation.
- [ ] Tests cover core hook behavior.
- [ ] Tests cover enrichment failure isolation.
- [ ] Verification uses shared KMP test structure where practical.
- [ ] Targeted Gradle verification commands are documented in the issue completion notes.

## Blocked by

- Issue 6: Sync existing feeds without deleting unseen articles
- Issue 7: Support bounded batch storage and partial failure recovery
- Issue 8: Add Core Processing Hook pipeline
- Issue 9: Add Enrichment Hook extension point with isolated failures
