# Repository Governance & Agent Rules (`GEMINI.md`)

## 1. Orientation First
- Always read `docs/CODEBASE_MAP.md` before planning, researching, or editing.
- Use the Task-to-File Routing Table ("Where to Look First") instead of running broad repository scans.

## 2. Branching & Commit Hygiene
- Never commit, merge, or push directly to `main`.
- Create a dedicated branch from `main` (or the active prerequisite feature/docs branch) for each logical task.
- Use granular Conventional Commits (`feat(...)`, `fix(...)`, `test(...)`, `docs(...)`, `ci(...)`, `chore(...)`).
- Run `git diff --name-only` (and `git status --short`) before committing to confirm 100% in-scope edits.

## 3. Codebase Map Maintenance
- Update `docs/CODEBASE_MAP.md` in the **same branch** whenever files are added, moved, renamed, or deleted, or when architectural, routing, or security wiring changes.

## 4. Security, Privacy & Data Contracts
- **Secrets**: Never read, print, or commit `.env*` files (other than `.env.example`), private keys, or real credentials.
- **Logging**: Never log cleartext PII, passwords, tokens, or raw entity payloads (watch AOP advice such as `LoggingAspect`).
- **API & Binding**: Never expose JPA entities directly in REST responses or bind them directly to public registration/input endpoints; use explicit DTOs and enforce ownership/role checks server-side.
- **Seed Data**: Never seed hardcoded default credentials outside explicit local/dev profiles.

## 5. Verification & Documentation Integrity
- Run `./mvnw test` (or `mvnw.cmd test` on Windows) after final edits and report verbatim test results and counts—never claim passes from memory.
- Include negative and authorization/boundary test cases (`401`, `403`, `400`, `404`, and cross-user access checks), not only happy-path `200` tests.
- Keep `README.md` and all documentation factual and modest: document only verified current behavior and clearly separate current capabilities from future improvements.

## 6. End-of-Task PR Summary
- At the end of every completed task (before the user opens a Pull Request), always provide a concise, copy-pasteable **PR Summary** containing:
  - A short PR title
  - A bullet list of what changed
  - Verified checks/commands run and their exact outcomes
