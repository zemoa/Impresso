# Specifications

This directory contains feature specifications. A specification defines the intended behavior before or alongside implementation.

## Naming

Name specification files with a two-digit ordering prefix followed by a lowercase kebab-case, readable name:

```text
spec/XX-readable-name.md
```

Example: `spec/01-create-impression.md`.

## Required Content

Each feature specification should include:

- Context and goal
- Scope and explicit non-goals
- User-facing behavior
- Acceptance criteria
- Error and empty states
- Open questions or dependencies

Start from [TEMPLATE.md](TEMPLATE.md) when creating a specification.
