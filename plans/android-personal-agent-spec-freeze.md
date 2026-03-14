# Android Personal Agent Spec Freeze (Sprint 1)

## Product mode (fixed for Sprint 1)

- Cloud-API brain only (OpenAI/Gemini/Anthropic via external APIs).
- No on-device LLM requirement.
- Private-by-default posture.
- Runtime default state: Off.

## Capability policy matrix

| Capability                      | Default | User override             | Confirmation rule                                    |
| ------------------------------- | ------- | ------------------------- | ---------------------------------------------------- |
| Chat send/receive               | Allow   | n/a                       | none                                                 |
| Link open (external)            | Ask     | trusted domains (future)  | always ask for unknown/risky links                   |
| Camera snap/clip                | Ask     | session-scoped allow      | ask per action unless explicitly enabled for session |
| Screen capture/record           | Ask     | session-scoped allow      | ask per action                                       |
| Accessibility actions           | Ask     | explicit enable           | ask for destructive / risky actions                  |
| File/system destructive actions | Deny    | allow per action          | double confirmation                                  |
| Background runtime              | Off     | explicit time-bound start | user must choose duration                            |

## Runtime state model

- `Off`
  - No background runtime.
  - No active long-lived execution loop.
- `ActiveForeground`
  - User started runtime from app UI.
  - Foreground controls visible.
- `ActiveBackgroundWindow`
  - User explicitly enabled background mode for bounded time/task.
  - Persistent indicator + one-tap stop.

## Runtime transitions

1. `Off -> ActiveForeground`
   - Trigger: user taps Start.
2. `ActiveForeground -> Off`
   - Trigger: user taps Stop.
3. `ActiveForeground -> ActiveBackgroundWindow`
   - Trigger: user chooses background mode + duration.
4. `ActiveBackgroundWindow -> Off`
   - Trigger: timeout, task complete, or user stop.
5. `ActiveBackgroundWindow -> ActiveForeground`
   - Trigger: app returned to foreground + continue.

## Destructive action policy

Actions tagged destructive/system-impacting must:

1. show risk summary,
2. require explicit confirm action,
3. log audit event with timestamp and action metadata.

If confirmation UI is unavailable, fail closed (deny).

## Link safety policy

Before opening a link:

1. parse + validate scheme,
2. run host/IP risk checks,
3. if risky/unknown -> confirmation prompt,
4. if blocked -> show reason + do not open.

## Acceptance criteria (Sprint 1)

1. App launches in `Off` state.
2. Start/Stop controls reliably switch runtime state.
3. Background mode cannot activate silently.
4. Destructive actions are never executed without explicit confirmation.
5. Risky links are never opened silently.
6. Integration tests cover:
   - runtime state transitions,
   - destructive action confirmation gate,
   - risky-link confirmation gate.

## Test checklist

- Unit: runtime state reducer/transitions.
- Unit: link classification decision logic.
- Unit: destructive action gating logic.
- Integration: Start/Stop/background lifecycle transitions.
- Integration: deny-on-no-UI for destructive actions.

## Out of scope (Sprint 1)

- Full embedded Android gateway architecture replacement.
- Continuous autonomous background operation.
- Multi-device sync improvements beyond baseline behavior.
