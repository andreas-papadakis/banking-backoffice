# LESSONS.md

Lessons learned while implementing this project, so the same mistakes aren't made twice. Read this before making changes. Add an entry whenever a mistake or surprising behavior is found and resolved.

Entry format: **Problem** → **Cause** → **Rule going forward**.

## Known issues found during initial review (not yet fixed)

- **Compiler target is a version-number typo.**
  `pom.xml` sets `<target>${mapstruct.version}</target>` (1.6.3) instead of `${java.version}`.
  Rule: use `${java.version}` for `source`/`target` (or `<release>`); don't touch it without checking the build still compiles.

- **Lombok version mismatch.**
  The `lombok.version` property is 1.18.40, but the annotation processor path pins 1.18.34.
  Rule: keep the processor path on `${lombok.version}` so the two don't drift.

- **Tests silently don't run.**
  Surefire only includes `**/*Spec` and `**/*Specification`, so JUnit-style `*Tests` classes (e.g. `BackofficeApplicationTests`) are skipped.
  Rule: write new tests as Spock specs named `*Spec`, or widen the Surefire `includes`.

- **Surefire Mockito agent path is Windows-only.**
  The `argLine` uses backslashes in `${settings.localRepository}\org\mockito\...`, which breaks on macOS/Linux.
  Rule: use forward slashes or `${file.separator}` when editing the `argLine`.

- **H2 database path is outside the repo.**
  `spring.datasource.url` points to `~/IdeaProjects/banking-backoffice/data/demo`, which doesn't match this checkout's location.
  Rule: don't assume the DB lives in the repo; check this URL when data seems missing or the app creates a new empty DB.

## Conventions confirmed in the code

- Validation constraints go on the `AccountApi` interface parameters, not on `AccountController`.
- "Not found" in services is signaled with `NoSuchElementException` (→ 404 via `CustomExceptionHandler`); new domain errors need their own `@ExceptionHandler`.
- Config property keys that start with `random.` are intercepted by Spring's `RandomValuePropertySource`; that's why the property prefix is `app.random-number-generator`. Don't rename it to start with `random.`.
