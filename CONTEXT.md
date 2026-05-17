# DevPulse

DevPulse helps a user collect and read content from web feeds across devices.

## Language

**Feed Import**:
A first-time addition of a web feed from a user-provided URL.
_Avoid_: Parse feed, add URL

**Feed Sync**:
A later reconciliation of an existing RSS feed with the latest remote channel and article data.
_Avoid_: Re-import, refresh

**Remote-Owned Article Data**:
Article fields supplied by the feed publisher that may change during a **Feed Sync**.
_Avoid_: User data

**User-Owned Article Data**:
Reader-controlled article fields that must survive **Feed Sync** updates.
_Avoid_: Feed data

**Article Fingerprint**:
A stable publisher-derived article identity used to match a remote article to an existing local article.
_Avoid_: Local article ID

**Local Article ID**:
The app-generated stable identifier for a stored article.
_Avoid_: Article fingerprint

**Feed Source URL**:
The raw URL stored for a feed subscription and used as the source for fetch attempts.
_Avoid_: Feed fingerprint

**Local Feed ID**:
The app-generated stable identifier for a stored feed subscription.
_Avoid_: Feed source URL

**Possible Duplicate Article**:
A newly received article that resembles an existing article but lacks enough identity confidence to overwrite it.
_Avoid_: Duplicate

**Core Processing Hook**:
A feed-processing extension that runs before storage and may shape cheaply available publisher data.
_Avoid_: Enrichment hook

**Enrichment Hook**:
A feed-processing extension that runs after storage to add complementary data without delaying article visibility.
_Avoid_: Core hook

**Incomplete Article**:
A stored article whose feed payload lacks enough data for a complete reading experience.
_Avoid_: Invalid article

**Incomplete Reason**:
A machine-readable explanation for why an article is incomplete.
_Avoid_: Validation error

**Feed Processing Job**:
A queued unit of work that imports or syncs one feed and records its current stage.
_Avoid_: Progress callback

**Core Processing Stage**:
The initial feed processing stage that fetches, parses, shapes, and stores publisher data.
_Avoid_: Enrichment

**Enrichment Stage**:
The later processing stage that adds complementary data after articles are visible.
_Avoid_: Core processing

**Partial Feed Processing Failure**:
A feed-processing failure that happens after some feed data has already been stored.
_Avoid_: Rollback

## Relationships

- A **Feed Import** creates one feed subscription.
- A **Feed Sync** belongs to exactly one existing feed subscription.
- **Remote-Owned Article Data** can be updated by a **Feed Sync**.
- **User-Owned Article Data** is preserved during a **Feed Sync**.
- An **Article Fingerprint** identifies the same publisher article across **Feed Sync** runs.
- A **Local Article ID** identifies the app's stored article row.
- A **Feed Source URL** identifies a feed subscription for now.
- A **Local Feed ID** identifies the app's stored feed subscription.
- A **Possible Duplicate Article** is reviewed or resolved separately instead of overwriting an existing article.
- A **Core Processing Hook** runs before article storage.
- An **Enrichment Hook** runs after article storage.
- A **Core Processing Hook** uses only already-fetched feed data and must not perform slow enrichment work.
- An **Incomplete Article** is still visible to the user.
- An **Incomplete Article** has one or more **Incomplete Reasons**.
- A **Feed Processing Job** is either a **Feed Import** or a **Feed Sync**.
- A **Feed Processing Job** starts with a **Core Processing Stage** and may continue with an **Enrichment Stage**.
- Starting a **Feed Import** or **Feed Sync** creates or reuses a **Feed Processing Job** instead of processing inline.
- A **Feed Processing Job** records both its current stage and execution state.
- Feed processing behavior belongs to the feed data implementation.
- The **Core Processing Stage** does not need deterministic progress.
- The **Enrichment Stage** may expose deterministic progress.
- Only one feed can be in the **Core Processing Stage** at a time.
- Duplicate active jobs for the same **Feed Source URL** are coalesced.
- The **Core Processing Stage** stores articles in bounded batches instead of one unbounded write.
- A **Partial Feed Processing Failure** leaves stored articles visible and relies on retry-safe matching.
- A **Feed Sync** does not remove previously stored articles that are missing from the latest payload.
- An **Enrichment Hook** writes derived article data without overwriting publisher-owned or user-owned fields.
- An **Enrichment Hook** failure does not fail the **Feed Processing Job**.

## Example Dialogue

> **Dev:** "When a user starts a **Feed Import**, should articles wait for enrichment?"
> **Domain expert:** "No. Store readable articles first; enrichment can follow later."
>
> **Dev:** "If a **Feed Sync** receives an article we already have, can it update the title or image?"
> **Domain expert:** "Yes, if that field came from the publisher. It must not overwrite user-created data like saved state or tags."
>
> **Dev:** "Does the local article ID prove two remote articles are the same?"
> **Domain expert:** "No. The local ID identifies our row; the **Article Fingerprint** is what links publisher data across syncs."
>
> **Dev:** "If two articles only weakly match, should sync overwrite the existing article?"
> **Domain expert:** "No. Treat the incoming article as a **Possible Duplicate Article** unless a strong or medium identity match exists."
>
> **Dev:** "Should tag generation block a **Feed Import**?"
> **Domain expert:** "No. Store the parsed article first; tag generation belongs to an **Enrichment Hook**."
>
> **Dev:** "Can a **Core Processing Hook** call an AI service before saving an article?"
> **Domain expert:** "No. It can normalize and choose fields from the feed payload, but slow or external work belongs after storage."
>
> **Dev:** "If an article has no title, should the importer drop it?"
> **Domain expert:** "No. Store it as an **Incomplete Article** so the user can still see and potentially fix it later."
>
> **Dev:** "Should the UI need to infer why an article is incomplete?"
> **Domain expert:** "No. Store a small **Incomplete Reason** set, then the UI can decide how much detail to reveal."
>
> **Dev:** "Should a user wait on a **Feed Import** screen until parsing finishes?"
> **Domain expert:** "No. Create a **Feed Processing Job**, let processing continue, and show progress from the job state."
>
> **Dev:** "Does adding a feed URL parse the feed before returning?"
> **Domain expert:** "No. It enqueues a **Feed Processing Job** so processing can continue outside the import action."
>
> **Dev:** "Should a screen ViewModel run the queue?"
> **Domain expert:** "No. Screens enqueue work and observe state; the feed data implementation owns processing behavior."
>
> **Dev:** "Can two feeds run core processing at once?"
> **Domain expert:** "No. Process one feed-level job at a time; parallelism belongs to later article-level enrichment."
>
> **Dev:** "Should a feed with hundreds of articles be written in one huge batch?"
> **Domain expert:** "No. Store bounded batches so large feeds do not create oversized writes."
>
> **Dev:** "If storage fails after several article batches, should we hide those articles?"
> **Domain expert:** "No. Keep visible data and let a later retry reconcile by **Article Fingerprint**."
>
> **Dev:** "If an old article is missing from the latest feed payload, should sync delete it?"
> **Domain expert:** "No. RSS feeds are rolling windows; absence from one payload does not mean the publisher deleted the article."
>
> **Dev:** "Can an **Enrichment Hook** replace the article title?"
> **Domain expert:** "No. Sync owns publisher fields, users own user fields, and enrichment writes derived fields."
>
> **Dev:** "If tag generation fails, did the **Feed Import** fail?"
> **Domain expert:** "No. Log the enrichment error when logging exists; visible feed data remains successful."

## Flagged Ambiguities

- "Import" can mean both first-time feed addition and later feed updates; resolved so far: **Feed Import** means first-time addition only.
- "Refresh" was used for later feed updates; resolved: use **Feed Sync** for existing-feed reconciliation.
- Robust feed duplicate detection across URL variants is future scope; for now, exact raw **Feed Source URL** match is the feed identity.
- Enrichment retry is future scope; current enrichment failures are ignored after logging when logging exists.
- Processor startup location is future scope; current decision only places feed processing behavior in the feed data implementation.
- Automatic feed sync scheduling is future scope; current processing model only needs to support enqueued sync jobs.
- Raw feed payload storage is out of scope and should not be added to the database for current feed processing.
