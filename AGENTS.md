# AGENTS.md

## Code style
- 4 spaces indentation
- Use functional patterns where possible
- Avoid using Optional and instead mark optional parameters with checkerframework @Nullable

## Repository Layout
- `scientist4jCore`: core library implementation.
- `docs`: documentation
- `examples`: examples

## General Guidance
- Avoid changing public API signatures. Anything under an `internal` directory
  is not part of the public API and may change freely.
- The SDK code is written for Java 21.

## Building and Testing
1. Format the code before committing:
   ```bash
   mvn --offline spotless:apply
   ```
2. Run the tests. This can take a long time so you may prefer to run individual tests.
   ```bash
   mvn test
   ```
   To run only the core SDK tests or a single test:
   ```bash
   TODO: figure this out
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```

## Commit Messages and Pull Requests
- Follow the [Chris Beams](http://chris.beams.io/posts/git-commit/) style for
  commit messages.
- Every pull request should answer:
  - **What changed?**
  - **Why?**
  - **Breaking changes?**
  - **Server PR** (if the change requires a coordinated server update)
- Comments should be complete sentences and end with a period.

## Review Checklist
- `mvn spotlessCheck` must pass.
- All tests from `mvn test` must succeed.
- Add new tests for any new feature or bug fix.
- Update documentation for user facing changes.