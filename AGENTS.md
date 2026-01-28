# AGENTS.md

## Coding Standards and Preferences

- Use Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) to reduce boilerplate in model classes.
- Scope enums (e.g., ForecastScenario) inside their related model classes when possible.
- Repository implementations should follow the style and structure of InvestmentRepository, using in-memory storage and proper Spring annotations.
- Avoid wildcard imports; use explicit imports in all Java files, especially test classes.
- Create comprehensive tests for new classes, following the pattern of existing tests.

## Test Standards

- Place helper methods at the end of the test file.
- After creating, modifying, or deleting any test, always run the test suite to verify all tests are passing.

- When mocking or verifying service/controller/repository methods with arguments, use `any()` for stubbing and `eq()` for verification whenever possible to ensure clarity and avoid brittle tests.

## Additional Notes
- Use `var` for local variable declarations within methods wherever possible to improve readability and reduce verbosity. Do not use `var` for class-level fields or method parameters; explicit types are required for clarity and maintainability.
- Apply these standards consistently for all new features and code changes in this repository.
