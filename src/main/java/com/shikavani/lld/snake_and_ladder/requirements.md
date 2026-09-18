# Snake and Ladder — Low Level Design Requirements

## 1. Problem Statement

Design and implement a **Snake and Ladder game engine** that can be played by multiple players on a configurable board containing snakes and ladders. The design should be object-oriented, follow SOLID principles, use appropriate design patterns where they add value, and cleanly separate game logic from presentation (UI/console/API). The system should be safe to run as multiple simultaneous, independent game instances.

This is an **LLD interview / learning exercise** — the goal is clean architecture, extensibility, and correct handling of edge cases, not building a production-scale distributed system. Non-functional requirements around scale are included only to inform design choices (e.g., avoiding global mutable state, keeping game instances independent), not to imply actual infrastructure work (load balancers, DBs, etc.).

---

## 2. Functional Requirements

### 2.1 Board
- FR1: The board has a configurable number of cells, `N` (default 100).
- FR2: Cells are numbered `1` to `N`, laid out conceptually in a boustrophedon (snake-like) path — this affects only how a UI renders the board, not the game logic, which only cares about linear cell numbers.
- FR3: The board holds a configurable set of **Snakes** and **Ladders**, each defined by a `(start, end)` pair.
  - A Snake's `start > end` (head above tail).
  - A Ladder's `start < end` (base below top).
- FR4: Board configuration is data-driven (passed in at construction), not hardcoded into game/movement logic.

### 2.2 Snake & Ladder Rules
- FR5: A cell can be the start of **at most one** snake or ladder (never both, and no two snakes/ladders may share a start cell).
- FR6: A cell cannot simultaneously be a valid start and end for a chain that creates a cycle (validation required at board-setup time).
- FR7: When a player's move lands them **exactly** on a snake's head, they are immediately moved to the snake's tail.
- FR8: When a player's move lands them **exactly** on a ladder's base, they are immediately moved to the ladder's top.
- FR9: Snake/ladder resolution is a single-step transformation (no chaining multiple snakes/ladders in one turn) unless explicitly configured otherwise (see §5 Extensibility).

### 2.3 Players
- FR10: The game supports `N` players, where `2 ≤ N ≤ 10` (configurable upper bound).
- FR11: Each player has: a unique ID/name, a current position (starts at `0`, i.e., off-board, or `1` per configuration), and turn order.
- FR12: Players take turns in a fixed rotating order (round-robin).

### 2.4 Dice
- FR13: The game supports configurable dice: `k` dice, each with `s` sides (default: `k=1`, `s=6`).
- FR14: A turn's total move value is the sum of all dice rolled in that turn.
- FR15: Dice rolling must be pluggable/mockable (for deterministic testing), not tightly coupled to a specific RNG implementation.
- FR16 (optional/extensible): Support a "roll again on max value" house rule (e.g., rolling all 6s), off by default.

### 2.5 Movement & Turn Resolution
- FR17: A player's new position = current position + turn's dice total.
- FR18: **Overshoot rule**: if new position > `N`, the move is invalid; the player stays at their current position, and the turn passes to the next player. (Configurable — see §5.)
- FR19: If the new position lands exactly on `N`, the player wins immediately.
- FR20: If the new position lands on a snake or ladder start cell, apply FR7/FR8 before finalizing the position for that turn.
- FR21: Turn order advances to the next active (non-winning) player after each turn.

### 2.6 Win Condition
- FR22: The first player to land **exactly** on cell `N` wins the game.
- FR23: Once a player wins, the game ends (or, optionally, continues to rank remaining players — extensibility item).
- FR24: The system must be able to report the winner and final game state/history.

### 2.7 Game Lifecycle
- FR25: Support creating a new game with a given board config, player list, and dice config.
- FR26: Support querying current game state at any time (positions, current turn, board config).
- FR27: Support playing a single turn on demand (step-by-step) — not just running to completion — so the engine can be driven by a UI, console, or automated test.
- FR28: Maintain a move history/log per game (player, roll, from-position, to-position, snake/ladder triggered) for replay/debugging/audit.

---

## 3. Non-Functional Requirements

| Category | Requirement |
|---|---|
| **Separation of Concerns** | Game logic (rules engine) must be fully decoupled from I/O/presentation (console, web API, etc.). The engine exposes a clean API/interface; no `System.out`/`print` or I/O calls inside core logic classes. |
| **Extensibility** | New rule variants (e.g., overshoot behavior, chained snakes/ladders, multiple dice, "must roll exact number to start") should be addable with minimal changes to existing classes — favor Open/Closed Principle via strategy interfaces. |
| **Testability** | Core logic (dice roll outcome, movement resolution, win detection) must be unit-testable in isolation, with deterministic/mocked dice. |
| **Configurability** | Board size, snake/ladder positions, player count, and dice count/sides must all be runtime configuration, not compile-time constants. |
| **Concurrency Safety** | Each game instance's state must be independently manageable; concurrent turns *within* a single game should be serialized (no two players moving simultaneously in the same game), while multiple *different* games must be able to run concurrently without interfering with each other (thread-safety per game instance). |
| **Scalability (design-level only)** | The design should not preclude running many (1000+) independent game instances in the same process — i.e., no shared global mutable state, no singleton game state. This is a design constraint, not a request to build actual distributed infra. |
| **Performance (design-level only)** | Core operations (roll dice, resolve a turn, check win) should conceptually be O(1) or O(log n) — e.g., snake/ladder lookups via a map, not a linear scan — reflecting good data-structure choices rather than a literal latency SLA. |
| **Maintainability** | Follow SOLID principles; favor composition over inheritance; use design patterns where they genuinely simplify the design (e.g., Strategy for dice/movement rules, Factory/Builder for board & game setup, Observer for notifying a UI of state changes, State pattern for game phases). Patterns should not be forced in where a simple class suffices. |
| **Validation** | Invalid configurations (overlapping snake/ladder start cells, out-of-range positions, snake start < end, ladder start > end, player count out of bounds) must be rejected at setup time with clear errors, not silently accepted. |
| **Immutability where sensible** | Board configuration (snakes/ladders/size), once validated and constructed, should be treated as immutable during a game. |

---

## 4. Out of Scope

- Persistence/database storage of games (in-memory is sufficient for this exercise).
- Networking, matchmaking, or a real multiplayer server/client protocol.
- Actual UI implementation (console/web) — only the clean interface/contract the UI would call.
- Authentication, authorization, or user accounts.
- Real load-balancing/horizontal scaling infrastructure — only design choices that wouldn't block it.

---

## 5. Extensibility Ideas (for discussion, not mandatory to implement)

- Pluggable **overshoot strategies**: bounce-back, stay-in-place (default), or forced retry.
- Pluggable **win strategies**: exact-landing-only vs. first-to-cross.
- Chained snake/ladder resolution (landing on a ladder top that's also a snake head).
- Multiple simultaneous game "modes" reusing the same core engine (e.g., team play, elimination mode).
- Event/observer hooks for UI updates (`onDiceRolled`, `onPlayerMoved`, `onSnakeBite`, `onLadderClimb`, `onGameWon`).
- Replay a game from its move history.

---

## 6. Suggested Core Abstractions (hint, not prescriptive)

- `Board` — size, snake/ladder map.
- `Snake`, `Ladder` (or a common `Jump`/`BoardEntity` interface) — start, end.
- `Dice` — roll(); supports `k` dice via a `DiceSet`/composite.
- `Player` — id, position.
- `Game` / `GameEngine` — orchestrates turns, holds players + board + dice, exposes `playTurn()`, `getState()`, `getWinner()`.
- `MovementStrategy` / `OvershootRule` — interface for how out-of-bounds rolls are handled.
- `GameObserver` / `GameListener` — interface for state-change notifications to a UI layer.

---

## 7. Acceptance Criteria (sample, for interview discussion)

- AC1: Given a board of size 30 with a ladder `(3 → 22)`, a player at position 1 rolling a 2 ends up at position 22.
- AC2: Given a board of size 100 with a snake `(99 → 5)`, a player at 94 rolling a 5 ends up at position 5.
- AC3: A player at position 98 on a 100-cell board rolling a 5 (overshoot) stays at 98, and turn passes to the next player.
- AC4: A player landing exactly on cell `N` is declared the winner and no further turns are processed for them.
- AC5: Two independently created `Game` instances do not share or corrupt each other's state when played concurrently.
