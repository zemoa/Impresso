# Documentation Index

Use this file as the entry point for project documentation.

## Read First

1. Read [ARCHITECTURE.md](ARCHITECTURE.md) before changing application structure, dependencies, or Android configuration.
2. Read [DEVELOPPEMENT.md](DEVELOPPEMENT.md) before implementing, reviewing, or testing code.
3. Read [spec/README.md](spec/README.md) before implementing a feature. Locate and follow the relevant feature specification when one exists.

## Documentation Map

| Document | Purpose | Update when |
| --- | --- | --- |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Architectural principles, current baseline, and decision rules. | A structural or cross-cutting decision changes. |
| [DEVELOPPEMENT.md](DEVELOPPEMENT.md) | Development workflow, coding, testing, and review practices. | Team practices or tooling change. |
| [spec/](spec/) | Feature specifications and acceptance criteria. | A feature is planned, clarified, or materially changed. |

## Working Rules

- Prefer the smallest change that satisfies the applicable specification.
- Do not invent product requirements. Record unresolved questions in the relevant specification.
- Keep documentation aligned with shipped behavior and architectural decisions.
- Treat specifications as the source of truth for feature behavior; treat code as the source of truth for the current implementation.
