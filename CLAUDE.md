# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Backend REST API for internal banking management operations (Spring Boot 4.0.0-M2, Java 21, Maven, JPA + H2, Lombok, MapStruct, Log4j2). Base package: `io.github.andreaspapadakis.banking.backoffice`.

**Read `LESSONS.md` before making changes**, and append to it whenever a mistake or surprising behavior is found and resolved.

**Keep this file up to date.** When a code change affects anything documented here (commands, build/test config, architecture, package layout, conventions, gotchas, coverage setup), update `CLAUDE.md` in the same change. Remove or correct statements that are no longer true, e.g. when a gotcha is fixed, and don't just append.

## Commands

Use the Maven wrapper (`./mvnw`).

- Build / run tests: `./mvnw clean verify` (JaCoCo report is generated in the `test` phase)
- Run the app: `./mvnw spring-boot:run`
- Run a single test class: `./mvnw test -Dtest=SomeSpec`
- Run a single Spock feature: `./mvnw test -Dtest='SomeSpec#feature name'`

Testing notes (see `pom.xml`):
- Tests are **Spock (Groovy)** specs compiled by `gmavenplus-plugin`; Surefire only picks up classes matching `**/*Spec` or `**/*Specification`. The existing JUnit `BackofficeApplicationTests` does **not** match that pattern and is not run by Maven.
- The Surefire `argLine` loads the Mockito agent from `${settings.localRepository}\org\mockito\...` using Windows-style backslashes, so it may need adjusting on macOS/Linux if tests fail on agent startup.
- Checkstyle config is `google_checks.xml` (Google Java style, 2-space indent) with `checkstyle-suppressions.xml` disabling all missing-Javadoc checks. No Checkstyle plugin is wired into the Maven build; it is applied via the IDE.

## Test coverage requirement

Test coverage must never drop below **95%** in every JaCoCo counter: instruction, line, branch, complexity, method, and class. Every change that adds or modifies code must come with Spock specs that keep all of them at or above 95%. Check the report at `target/site/jacoco/index.html` after `./mvnw clean verify`.

- JaCoCo excludes `**/*DTO.*`, `**/*Exception.*` and `model/*` from the report (see `pom.xml`); everything else counts.
- The build doesn't enforce this yet: there is no `jacoco:check` execution with these thresholds, and there are currently no Spock specs. Until that's added, verify the report manually.

## Architecture

Package-by-feature layout: `accounts/` (controller, service, repository, model, dto, mapper, exception, validator) and `shared/` (cross-cutting config, validation annotations + validators, utils, `ApiException` error body).

- **Controller contract lives in an interface.** `AccountApi` declares the endpoints and carries validation annotations (`@Valid`, `@Pattern`, `@AllowedCurrency`) on method parameters; `AccountController` implements it. Put new validation constraints on the interface, not the implementation. The interface is `@Validated`, so parameter-level violations surface as method validation exceptions.
- **Service** is an interface (`AccountService`) + `AccountServiceImpl` (class-level `@Transactional`, reads marked `readOnly`). Entities are converted to `AccountResponseDto` via the MapStruct `AccountMapper`.
- **Errors:** `CustomExceptionHandler` (`@ControllerAdvice`, extends `ResponseEntityExceptionHandler`) maps exceptions to `ApiException` bodies. `NoSuchElementException` → 404 (services use `Optional.orElseThrow()` and throw it directly for "not found"), `RussianRouletteException` → 403. Validation messages are resolved from `ValidationMessages.properties` through the Spring `MessageSource` (`spring.messages.basename=ValidationMessages`).
- **Custom validation:** annotation in `shared/validation/` + validator in `shared/validator/` (e.g. `@AllowedCurrency`, `@AtLeastOneFieldPresent`); account-specific validators live under `accounts/validator/`.
- **Config properties:** `PropertiesConfig` enables `@ConfigurationPropertiesScan`; typed records such as `RandomNumberGeneratorProperty` (prefix `app.random-number-generator`) bind from `application.properties`.
- **External call:** `AccountServiceImpl.russianRoulette` calls the random.org JSON-RPC API over raw `HttpURLConnection` for randomness. The API key in `application.properties` is encrypted with Jasypt (`ENC(...)`); running the app needs the Jasypt encryptor password supplied externally (e.g. `JASYPT_ENCRYPTOR_PASSWORD` env var), which is not stored in the repo.

## Gotchas

- `application.properties` points the H2 file DB at `~/IdeaProjects/banking-backoffice/data/demo` (MySQL mode, `ddl-auto=update`), a path outside this repo; `data/` is git-ignored. The H2 console is enabled.
- `maven-compiler-plugin` config in `pom.xml` sets `<target>${mapstruct.version}</target>` and pins Lombok `1.18.34` in the annotation processor paths, differing from the `lombok.version` property. Be aware of this before touching compiler settings.
- Annotation processors (Lombok then MapStruct) generate code at compile time; if mappers or getters appear missing in the IDE, enable annotation processing.
