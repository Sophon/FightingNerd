# Database

## Migration strategy

Destructive. On schema version mismatch, all tables are dropped and recreated from the current `.sq` files. Cached wiki data is re-downloaded on next launch.

This is safe because the database is purely a cache of remote wiki data — no user-generated content.

Implemented per-platform in `DatabaseDriverFactory`:
- Android: `AndroidSqliteDriver.Callback.onUpgrade` drops all user tables, then calls `schema.create(...)`.
- iOS: `NativeSqliteDriver` with `onConfiguration` replacing the default `upgrade` lambda to do the same.

## Schema version

SQLDelight derives `Schema.version` from the count of `.sqm` migration files in each database's `srcDir`. Filename `N.sqm` means "migrate from N to N+1." Highest filename + 1 = current `Schema.version`.

Verify after build:
```bash
grep -rn "override val version" composeApp/build/generated/sqldelight
```

Migration files live next to the `.sq` files:
- `composeApp/src/commonMain/sqldelight/character/character/`
- `composeApp/src/commonMain/sqldelight/move/move/`

`verifyMigrations.set(false)` is set in `composeApp/build.gradle.kts` for both databases — required because the `.sqm` files are intentionally empty (the destructive callback ignores them).

## Changing the schema

1. Edit the relevant `.sq` file (add/remove columns, tables, etc.).
2. Update INSERT statements and Kotlin mappers to match the new schema. Out-of-sync INSERTs abort SQLDelight codegen for the entire package.
3. `ls` the migrations directory to find the highest existing `.sqm` number.
4. `touch <highest+1>.sqm` in the affected database's directory. Only the affected database needs a new file — the other can stay at its current version.
5. Build and verify the version bumped:
```bash
   ./gradlew :composeApp:clean :composeApp:assembleDebug
```
6. Commit the `.sq` change, mapper updates, and new `.sqm` file together.

On the next release, existing installs will see a version mismatch on first launch, `onUpgrade` fires, tables drop, and cached data re-downloads.

## Auto Backup

`android:allowBackup="false"` and `dataExtractionRules` exclude the database from Google Drive backup and device-to-device transfer. Without this, reinstalls can restore a stale DB from cloud — defeating the migration logic on fresh-looking installs.