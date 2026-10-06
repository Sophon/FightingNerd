# Test suite

- `testUnit` lives in root `build.gradle.kts`; `testCoverage` and `testArch` live in the `testSuite` module (Konsist, `hex` and `coverage` packages)
- one workflow, `.github/workflows/test.yml`, one job per task - PR checks: `test / unit`, `test / coverage`, `test / arch`

## `testUnit`
- runs all test classes
- `./gradlew testUnit --rerun-tasks`

## `testCoverage`
- checks each covered file has a `<Name>Test.kt` in the same package (existence only)
- covered: everything in `service` / `util` packages, `*Mapper.kt` / `*Mappers.kt` inside `adapter`
- scans `feat/*`, `composeApp`, `bot/discord`; opt out with `@ExcludeFromCoverage`
- `./gradlew testCoverage --rerun-tasks`

## `testArch`
- packaging, visibility, ports, services, dependency direction
- only scans migrated modules - add a module to `HexScope.modules` once it's migrated
- `./gradlew testArch --rerun-tasks`
