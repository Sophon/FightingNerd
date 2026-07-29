# Chapter 3 — News Feed App — Interview Takeaway

## Problem statement & scope

Design the mobile client for a news feed app in the family of
Instagram / Twitter / Facebook. Text-and-image posts, feed-list /
post-creation / post-detail screens, ~10M DAU with reads dominating writes
~100:1.

**In scope:** feed browsing with infinite scroll, post creation (text +
1–N images), post detail with comments, likes/unlikes, comments.

**Out of scope:** authentication (assume valid bearer token), video
content, sharing/reposting, DMs, live updates (no push updates for
like/comment counts), wearables.

**Platform:** native UI on both Android and iOS with a KMP-shared data
layer and use cases. Phone-first; tablets get a scaled-up layout for v1.

**Consistency stance:** eventual consistency on interaction counts is
acceptable — a feed is not a chat app.

## High-level architecture

Layered, KMP-split:

```
UI (Compose / SwiftUI)             NATIVE
ViewModel                          NATIVE
UseCase                            SHARED
DataSources (Remote, Local DB)     SHARED
```

**Data flow:** DB is the single source of truth. UI observes DB via
`Flow` / `StateFlow`. Network calls write to DB; UI never reads from
network directly. UDF: state flows down as reactive streams, events go
up as `suspend fun` calls.

**Reactive wiring:** ViewModel exposes `state: StateFlow<UiState>` for
the persistent UI picture and `events: SharedFlow<UiEvent>` for one-shot
signals (transient errors, navigation). State is replayed on
resubscription; events are not.

**Two-pipeline media split:** JSON metadata flows through remote source →
DB → UI. Media bytes flow through CDN → media loader (Coil / Kingfisher)
→ UI directly. Repository never sees image bytes.

## API contract (summary)

- `GET /v1/feed?cursor=<>&limit=<>` — cursor-paginated list of
  `PostPreview` + `PagingMetadata { nextCursor, hasMore }`.
- `GET /v1/posts/{postId}` — full `Post` with first page of comments
  embedded.
- `POST /v1/posts` — create, returns full created resource. Body:
  content + `List<Attachment>` (each with `type`, `contentUrl`,
  `caption`). Header: `Idempotency-Key: <uuid>`.
- `POST /v1/posts/{postId}/likes` and `DELETE` — toggle like. Idempotent
  via `PUT`-style semantics.
- `GET /v1/posts/{postId}/comments`, `POST /v1/posts/{postId}/comments`,
  `DELETE /v1/comments/{commentId}`.
- `POST /v1/media/uploads` — returns pre-signed URL. Client uploads
  bytes directly to object storage, then references `contentUrl` in
  post creation.
- All authenticated via `Authorization: Bearer <token>`.
- Errors: structured JSON body with machine-readable `code` +
  human-readable `message`, correct HTTP status codes (`201`, `400`,
  `401`, `403`, `409`, `422`, `429`, `5xx`).

**PostPreview shape (feed):** `postId`, `body`, `likeCount`,
`commentCount`, `hasUserLiked`, ISO timestamp, `author` (id/name/thumbnail),
first attachment preview URL + attachment count.

**Full Post shape (detail):** superset of preview — full-res
`List<Attachment>` with `width`, `height`, `blurhash`, first page of
comments embedded with own cursor for pagination.

## Key patterns & technologies covered

**REST + JSON** chosen for CDN-cacheability and mobile-team familiarity.
GraphQL rejected mainly on caching complexity and BE effort; gRPC
rejected as niche for public mobile APIs.

**Cursor pagination** for the feed. Opaque tokens, immune to inserts
during scroll, handles timestamp ties naturally via `(rank, postId)`
tuple comparison server-side.

**Two-phase media upload:** client requests pre-signed URL, uploads
bytes directly to object storage (S3/GCS), then creates the post
referencing the returned URL. API server never handles bytes.

**Idempotency keys** on all mutations. Persisted client-side alongside
the pending op. Server caches responses per key so retries are safe
regardless of whether the previous attempt succeeded or failed silently.

**CDN-fronted media** with a dedicated media loader (Coil / Kingfisher)
handling the memory + disk cache tiers. Placeholder rendering via
BlurHash/dimensions in the post DTO so layout doesn't shift on load
and offline degrades gracefully.

**DB as single source of truth.** SQLDelight on the shared layer,
queries return `Flow`. Multi-screen consistency comes free: same post
visible in feed and detail both update when the DB row changes.

**Split observe / refresh in use cases:**
```
fun observeFeed(): Flow<List<Post>>
suspend fun refreshFeed(): Result<Unit, FeedError>
```
Different lifetimes, different error semantics, different signatures.
Observe never errors; refresh returns a one-shot result.

**Optimistic writes via persistent per-resource queue:**
- Op persisted to a `pending_ops` table with idempotency key.
- Partitioned by `resourceKey` (e.g. `post:A`) — ops on the same
  resource serialize; ops on different resources run in parallel.
- Coalesce redundant ops in the queue before dispatch (like → unlike →
  like collapses to like).
- On Android, implement via WorkManager with unique work names keyed by
  resource. On iOS, custom lightweight queue + `BGProcessingTaskRequest`.

**Error handling model:**
- Remote source maps transport errors to typed `RemoteError`.
- Use case (or repository if you keep one) merges remote + local errors
  into a domain `FeedError` / `PostError` sealed hierarchy.
- ViewModel splits errors into UI *state* (persistent — empty screen +
  retry) vs *event* (transient — toast/banner). Same underlying error,
  different UX based on current state (empty cache vs stale cache).

**Three-cache eviction model:**
- `feed_items` — position-based eviction, short TTL (hours), refresh
  replaces top-of-feed rather than merging.
- `posts` — LRU on last-accessed, longer TTL (days), count cap ~500.
- Media disk cache — size-capped LRU (~200MB), owned by media loader
  library, independent of SQLite.

Eviction is opportunistic and synchronous with writes (in the same
transaction), not a background scheduler.

## Trade-offs discussed

**REST vs GraphQL** — REST wins on CDN cacheability and infrastructure
maturity. GraphQL would let one endpoint serve feed and detail shapes
of the same post, but the caching and BE complexity cost is real.

**Live updates vs eventual consistency** — chose eventual consistency.
Live like/comment counts require WebSockets or long-polling for every
open app; at 10M DAU that's massive infra cost for what's essentially a
vanity number. Chat apps justify this; feed apps generally don't.

**Denormalized vs normalized response payloads** — chose denormalized
(comment carries `Author` inline). Simpler client rendering; payload
overhead is negligible compared to added client complexity.

**Client-side "importance" categorization for revert-on-failure UX:**
- Silent revert acceptable when the op is a state toggle with no
  downstream side effect (likes).
- Notify on revert when the user believes they sent something to
  another human (comments, post creation).
- Never optimistically apply for writes with real-world consequences
  (payments, deletions, DMs to specific recipients).

**Stale cache vs empty screen** — show stale content on cold start with
a subtle "offline / cached from X ago" indicator, refresh aggressively.
Empty screen is worse for perceived responsiveness than a two-week-old
feed. Feed content is graceful under staleness because the content
itself (photos, text) doesn't get worse with time — only the metadata
(counts) does.

**Per-resource queue partitioning** — partition by the resource being
mutated, not by operation type. Ordering constraints exist between ops
that touch the same thing, not ops of the same kind. Naive "one global
queue" either couples independent failures or defeats its own purpose.

**Server wins on refresh, with pending-op awareness** — feed refresh
should not overwrite rows that have pending optimistic ops in the
queue; otherwise UI flickers as the optimistic state gets clobbered
and then restored when the op lands.

## Gaps & things to revisit

**API design fluency.** Initial pass had `/posts/new` (not RESTful),
timestamp-based pagination (fragile), and bundled likes/comments behind
one interactions endpoint. Should reach production-shaped conventions
on the first pass, not after correction.

**Idempotency layering.** Understand both the client-queue-level
protection *and* the API-level protection (`Idempotency-Key` header).
Transport retries, app kills, and network middleboxes can produce
duplicates even with a perfect client queue.

**Two-phase media upload internalization.** Send-bytes-through-API is
the default mental model to unlearn. Pre-signed URLs are the pattern —
should come out automatically for image/video-heavy apps.

**Response DTO habit.** First pass of API design must include response
shapes, not just requests. Overfetching lives in the response.

**Cache eviction dimensions across tables.** LRU, TTL, size cap, and
count cap don't apply uniformly. Feed items evict by position + short
TTL; post rows evict by LRU + long TTL + count cap; media evicts by
size cap. Different tables, different characteristics, different
policies.

**UDF discipline in use-case signatures.** `Result<Flow<T>>` is a
smell. State and one-shot operations have different lifetimes and
different error semantics — they belong in different function
signatures.

**Queue partitioning intuition.** Partition by resource, not op type.
This is a common misconception and worth locking in.

**Topics not covered that were in scope for Chapter 3:**
- Feed DB schema details — `feed_items(feedPosition, postId, cursor)`
  as an ordered index into a shared `posts` table. The join query,
  the "insert new page + trim old positions" transaction.
- Native rendering vs WebViews for embedded content (link previews,
  articles inside the feed).
- Prefetching strategy — top N feed items, images visible in the next
  scroll window, detail data on hover/near-tap.
- Image resolution negotiation with CDN — `?w=` params, Client Hints,
  format negotiation (WebP / AVIF vs JPEG).
- Push notifications for feed events (someone liked/commented on your
  post) — FCM / APNs, silent notifications for state sync.
- Analytics, observability, crash reporting — feed load timing,
  scroll depth, image load failures.
- Deep linking into specific posts.
- Tombstones for deleted posts (server tells client which post IDs
  to evict on refresh).

**BTW questions raised during the session** (for research before next
chapter):
- Media upload strategies (direct / pre-signed / resumable) — covered
  during session, worth deeper practice in Chapter 8 (Drive).
- Media loader + CDN pattern — covered during session.
- Cursor pagination metadata shape — covered during session.
- Optimistic write flow correctness (UI-first vs DB-first) — covered
  during session.

## Anything else useful

**For Phase 3 in Fighting Nerd:**

- The KMP-shared-data-layer / native-VM-and-UI split is worth
  comparing against how Fighting Nerd's Android and iOS clients
  currently share code (if at all). If both clients duplicate DTOs,
  networking, and domain logic, this is a concrete refactor target.
- The persistent pending-ops queue pattern applies to any client that
  writes to the JVM backend under flaky network. Even Discord bot
  commands issued from a client (if such a flow exists) benefit from
  the same idempotency + queue semantics.
- The three-cache model (feed_items / posts / media) generalizes: any
  list-of-things screen backed by a REST feed has the same shape.
  Whatever list Fighting Nerd shows (users, matches, sessions, bot
  commands, etc.) probably wants the same split.
- DB-as-source-of-truth + UDF with `Flow` is idiomatic Kotlin/Android
  and worth auditing Fighting Nerd for. Common regressions: VMs that
  read from the network directly on some paths, cached data that's
  fetched imperatively rather than observed reactively, ViewModels
  that decide UI concerns like "show a toast."

**Related chapters that link into this one:**
- Chapter 4 (Chat) — WebSockets, server-controlled IDs, real-time push.
  Contrast with the "no live updates" decision here.
- Chapter 8 (Drive) — resumable uploads, block-level sync, encryption.
  Deeper cousin of the media upload conversation.
- Chapter 10 (Building Blocks) — architecture patterns, DI, testing,
  observability. Meta-topics that inform every chapter above.
