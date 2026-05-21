# Queue-first feed import and sync processing

DevPulse will treat feed imports and feed syncs as queued feed-level processing jobs instead of synchronous parser calls. This keeps the user free to leave the import flow immediately, gives the UI durable job state to observe, and allows the core processing stage to write visible article data before optional enrichment work runs later. Only one feed-level core processing job runs at a time; later enrichment may fan out at article level.

**Considered Options**

- Process feed URLs synchronously from the repository call.
- Queue feed-level jobs and let a processor fetch, parse, store, and enrich.

**Consequences**

- Repository actions enqueue or coalesce jobs rather than returning parsed feed data.
- Feed progress must be stored durably enough for UI observation and background processing.
- Core feed processing is serialized to avoid duplicate import and sync races.
