# Development Guide

## Setup

Install Android Studio and JDK 17.

Install Android SDK Platform 37. An Android emulator or physical device is required for device-based testing.

Use the Gradle wrapper included in the repository; do not install or invoke a project-specific Gradle version manually.

Run the following commands for local validation:

```bash
./gradlew assembleDebug
./gradlew test
```

## Implementation Plans

Implementation plans for features belong in the top-level `plan/` directory. Each
plan should reference the relevant specification under `spec/` and describe the
architecture, implementation sequence, testing strategy, and verification criteria.

## Implementation Practices

- Write all new application and test code in Kotlin.
- Keep changes minimal and focused on the requested behavior. Do not include unrelated refactoring.
- Obtain explicit approval before adding a dependency.
- Define all user-facing strings in Android string resources; do not hard-code them in application code.
- Document technical interfaces and DTOs with comments that explain their intent, never their code contents.
- Configure and run ktlint and detekt for Kotlin style and static analysis. Their Gradle integration and commands must be documented when introduced.

## Testing

- Follow test-driven development: write a failing test before implementing a behavior.
- Add or update unit tests for every behavior change, including business rules, state transformations, and regressions.
- Add instrumented tests for every feature.
- Cover the feature's acceptance criteria.
- Cover error and empty states whenever the feature defines them.

## Code Review

Before requesting review:

- Run the relevant unit and instrumented tests.
- Run ktlint and detekt.
- Build the debug application successfully.
- Link the behavior change to its specification, and create or update that specification when necessary.
- Confirm that the change remains within scope and follows the documented architecture.
- Verify user-facing success, error, loading, and empty states where applicable.

## Git Workflow

- Create a dedicated feature branch from the default branch for each change.
- Use Conventional Commits for commit messages, such as `feat:`, `fix:`, `test:`, `docs:`, and `chore:`.
- Do not include AI attribution, co-author trailers, tool names, or generated-by references in commits.
- The author may merge a reviewed change after all required checks have passed.
