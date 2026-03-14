# Android Personal Agent Roadmap (Cloud-API First)

## Scope and outcomes

This roadmap converts our discussion into an execution plan for a private, Android-first personal assistant experience:

- Private deployment posture (not publicly exposed).
- Cloud API brain (OpenAI/Gemini/Claude), no on-device LLM requirement.
- Permission-first behavior for risky actions.
- On-demand runtime (default off), optional background mode only when explicitly enabled.
- Future-ready path to remove separate gateway host dependency.

## Assumptions

- Primary runtime mode uses external model APIs.
- Android app remains feature-rich (chat/voice/tools) but with stricter defaults.
- We optimize for reliability and safety over maximum automation on day one.

## Workstreams

### WS1: Product + security contract (must complete first)

Deliverables:

1. Threat model and trust boundaries for Android-first mode.
2. Capability policy matrix:
   - camera
   - screen capture/record
   - accessibility automation
   - background execution
   - link open/navigation
3. Action-risk classes:
   - read-only
   - reversible write
   - destructive/system-impacting
4. Confirmation policy by risk class.

Exit criteria:

- Every privileged action has an explicit policy path (allow, ask, deny).
- "No UI available" behavior is fail-closed for destructive actions.

### WS2: On-demand runtime UX (default OFF)

Deliverables:

1. Runtime control states:
   - Off
   - Active (foreground)
   - Active (background window)
2. Explicit controls:
   - Start agent
   - Stop agent
   - Run in background for N minutes / until task completion
3. Auto-stop conditions:
   - idle timeout
   - task complete
   - battery threshold policy (optional)

Exit criteria:

- App does not stay always-on unless user explicitly enables it.
- Background mode is visible, temporary, and user-revocable.

### WS3: Permission-first execution guardrails

Deliverables:

1. Default policy presets:
   - Safe default (deny/ask-heavy)
   - Balanced
   - Power user
2. Per-capability prompts and persistent toggles.
3. Destructive action interlock (double confirmation / typed confirmation where needed).
4. Audit trail for approvals and denied attempts.

Exit criteria:

- Any file-delete/system-impacting action is blocked or confirmed first.
- Permission prompts are deterministic and testable.

### WS4: Link and navigation safety

Deliverables:

1. URL risk classifier pipeline:
   - scheme validation
   - hostname/IP risk checks
   - optional reputation gate
2. Open policy:
   - trusted domains auto-open (optional)
   - unknown/risky links require confirmation
3. Navigation result validation and block reason UX.

Exit criteria:

- No silent navigation to blocked/risky protocols or destinations.
- User sees clear reason and override path (where allowed).

### WS5: Cloud model integration profile (no local LLM)

Deliverables:

1. Provider adapters and fallback order:
   - OpenAI
   - Gemini
   - Anthropic
2. API key storage hardening and rotation UX.
3. Token/cost guardrails:
   - max tokens
   - budget caps
   - fallback behavior
4. Quality/performance profile per device tier.

Exit criteria:

- Stable chat/agent operation with external APIs only.
- Failover works without user confusion.

### WS6: Android-only architecture path (phased)

Deliverables:

1. Gap analysis between current companion-node model and embedded control plane.
2. Minimal embedded runtime spike (prototype).
3. Migration plan with compatibility mode.

Exit criteria:

- Decision memo: keep companion mode short-term vs. embedded mode target date.
- If proceeding, validated prototype milestones and rollback path.

## Sequenced execution plan

### Phase 0 (Week 1): Spec freeze

- Finalize WS1 policy matrix.
- Freeze MVP scope and non-goals.
- Define acceptance tests and demo script.

### Phase 1 (Weeks 2-3): On-demand runtime + permission defaults

- Implement WS2 controls and runtime states.
- Implement WS3 safe default preset.
- Add logs/events for approvals and runtime transitions.

### Phase 2 (Weeks 3-4): Link safety + destructive action interlocks

- Implement WS4 checks and confirmation UX.
- Add destructive action confirmations in execution paths.
- Regression tests for false-positive/false-negative cases.

### Phase 3 (Weeks 4-5): Cloud provider profile hardening

- Implement WS5 fallback and budget policies.
- Add provider health checks and user-facing failure messages.

### Phase 4 (Weeks 6+): Android-only runtime spike

- Execute WS6 architecture spike.
- Compare complexity/risk against companion model continuation.
- Decide go/no-go for full embedded runtime project.

## Testing strategy

- Unit tests: policy engine, risk classifier, state transitions.
- Integration tests: approval flow, runtime mode transitions, provider failover.
- Device tests: 4 GB / 6 GB / 8 GB RAM tiers with cloud-only model setup.
- Security tests: deny-by-default behavior, no-UI fallback, blocked link scenarios.

## Operational guardrails

- Default deployment profile is private/local-first.
- Public exposure disabled by default.
- Dangerous overrides are explicit and auditable.

## Immediate next actions (execution starts now)

1. Create policy matrix doc (WS1 artifact).
2. Implement runtime state model + Start/Stop controls (WS2 initial slice).
3. Implement safe-default preset and destructive-action confirmation hooks (WS3 initial slice).
4. Add first integration tests for runtime + approval transitions.

## Execution status tracker

Use this tracker to avoid "plan-only" drift and keep delivery measurable.

| Item                                                      | Status      | Output                                        |
| --------------------------------------------------------- | ----------- | --------------------------------------------- |
| WS1 policy matrix + spec freeze                           | in progress | `plans/android-personal-agent-spec-freeze.md` |
| WS2 runtime state controls (Start/Stop/Background window) | pending     | Android runtime + UI changes                  |
| WS3 safe-default permission preset                        | pending     | policy defaults + confirmations               |
| WS4 link safety confirmation flow                         | pending     | URL checks + confirmation UX                  |
| WS5 cloud provider failover profile                       | pending     | provider policy + fallback tests              |
| WS6 embedded runtime spike                                | pending     | architecture spike report                     |

## Sprint 1 delivery contract (what must ship first)

The first coding sprint is complete only when all checks below are true:

1. App starts in `Off` runtime state by default.
2. User can explicitly `Start` and `Stop` runtime from UI.
3. Background run is opt-in, time-bound, and visible while active.
4. Destructive actions require explicit confirmation.
5. Unknown/risky links require confirmation before open.
6. Integration tests cover runtime transitions and approval gate behavior.

## Non-goals for Sprint 1

- Full Android-only embedded gateway replacement.
- Advanced autonomous background workflows.
- Broad provider optimization beyond baseline failover.

These are intentionally deferred so we can ship safe, verifiable behavior first.
