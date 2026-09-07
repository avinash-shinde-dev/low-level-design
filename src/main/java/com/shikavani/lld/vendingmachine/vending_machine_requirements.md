# Vending Machine — Consolidated LLD Requirements

Consolidated from all the variations you shared, so you have one clear, unambiguous spec to design against. Organized as: Functional Requirements, Non-Functional Requirements, State Model, Exceptional Scenarios, and Explicit Design Expectations.

---

## 1. Functional Requirements

### 1.1 Product & Inventory
- FR1: The machine supports **multiple products**, each with its own price and available quantity.
- FR2: The machine displays all available items along with their prices and stock status (in-stock / out-of-stock).
- FR3: The machine maintains **inventory records** — quantity per product must always be accurate and queryable.
- FR4: New products should be addable to the machine **without modifying existing product-handling code** (open for extension).

### 1.2 Selection & Payment
- FR5: A user can **select a particular item** (by code/button/id).
- FR6: The user can insert **cash** — both **coins and notes** of different denominations.
- FR7: The system supports **multiple payment types** (cash now; card/UPI/wallet as future extensions) via a common abstraction — adding a new payment type shouldn't require touching core transaction logic.
- FR8: Cash can be inserted incrementally (multiple coins/notes across several insertions) — the machine tracks a running balance.
- FR9: The machine **verifies the inserted amount against the price of the selected item**:
  - If inserted amount == price → proceed to dispense.
  - If inserted amount > price → dispense item **and return correct change**.
  - If inserted amount < price → do **not** dispense; prompt for more money or allow cancellation.
- FR10: The user can **cancel a transaction** mid-way (before dispensing) and get a full refund of whatever was inserted.

### 1.3 Dispensing & Change
- FR11: Upon successful verification, the machine **dispenses the selected item**.
- FR12: If change is owed, the machine **returns change** using the minimum number of denominations, drawn from its own internal cash/change inventory.
- FR13: If the machine cannot make exact change with its current denomination inventory, it must **fail gracefully** — reject the transaction (or block that product) rather than dispense without giving correct change.
- FR14: After a successful transaction, the item's stock count decrements by exactly 1 (or by quantity purchased, if multi-buy is in scope).

### 1.4 Admin / Operator Interface
- FR15: An admin/operator interface allows:
  - **Restocking** existing products (increase quantity).
  - **Adding new products** entirely.
  - **Updating prices**.
  - **Collecting cash** (emptying accumulated payments) from the machine.
  - **Replenishing change denominations** (coins/notes available for making change).
- FR16: The admin interface is logically **separate from the customer-facing interface** — different capabilities, different access path.

---

## 2. Non-Functional Requirements

- NFR1 (**Concurrency**): The machine must handle **multiple simultaneous transactions safely** — no overselling the last unit of a product, no double-dispensing, no corrupted balance state. This applies whether it's multiple users on one machine, or a distributed fleet sharing central inventory.
- NFR2 (**Consistency**): Inventory and cash state must remain consistent even under concurrent access or partial failure (e.g., dispense hardware jam after payment is confirmed) — needs a rollback/refund path, not a silent inconsistency.
- NFR3 (**Extensibility**): Adding a new product type, new payment method, or new machine state should be possible by **adding new code, not editing existing tested code** (Open/Closed Principle).
- NFR4 (**Testability**): Core logic (change calculation, state transitions, balance verification) should be unit-testable independent of any real hardware/dispensing mechanism.
- NFR5 (**Auditability**): Every transaction — successful, failed, cancelled, refunded — should be traceable/loggable for reconciliation by the admin.

---

## 3. State Model (Core of the Design)

The machine's behavior is best modeled as an explicit **finite state machine**, not as conditionals scattered across methods. Expected states:

| State | Meaning | Valid actions from here |
|---|---|---|
| `NoCoinState` (Idle) | No money inserted yet. | `insertCoin()` → moves to `HasCoinState`. `selectProduct()` without money → rejected/error. |
| `HasCoinState` | Money has been inserted; waiting for product selection or more money. | `insertCoin()` (add more), `selectProduct()` → validates balance vs. price, `cancel()` → refund + back to `NoCoinState`. |
| `DispenseState` | Actively dispensing the chosen product. | System-driven; on success → decrement inventory, return change if any, go back to `NoCoinState`. On failure (jam) → refund, log, go to `NoCoinState` or `SoldOutState`. |
| `SoldOutState` | No stock for a product (or globally out of stock). | Rejects `selectProduct()` for that item with a clear error; may still allow selecting a different in-stock item. |

**Key design question to be ready for:** *why not just use a `switch`/`if-else` on a state string inside every method (`insertCoin()`, `selectProduct()`, `dispense()`, `cancel()`)?*

Be ready to articulate:
- A switch-per-method approach means every new state requires touching **every single method** — violates Open/Closed Principle, high regression risk.
- Logic for "what's legal in this state" ends up **duplicated and drifting** across multiple methods instead of living in one place.
- It's hard to unit test one state's behavior in isolation — you'd need to fake the whole machine.
- **State Pattern fix:** each state is its own class implementing a common interface (`insertCoin()`, `selectProduct()`, `dispense()`, `cancel()`). The `VendingMachine` context just delegates to `currentState`. Each state class only implements the transitions valid *from itself* — illegal actions are localized, not spread out. Adding a new state = adding a new class, not editing four existing ones.

---

## 4. Exceptional Scenarios to Handle Explicitly

1. **Insufficient funds**: user selects item, inserted amount < price → clear error, no dispense, money stays as pending balance (refundable on cancel).
2. **Out-of-stock item**: user selects an item with zero quantity → clear error immediately, before asking for money (or immediately reject/refund if money was already inserted).
3. **Exact change unavailable**: machine can't return correct change with current denomination stock → reject transaction proactively (ideally before accepting payment for that item) rather than shortchange the user.
4. **Concurrent purchase of last unit**: two near-simultaneous selections for the last unit of a product → exactly one succeeds, the other gets an out-of-stock error, not a race condition where both proceed.
5. **Dispense failure after payment success** (jam/hardware fault): must trigger automatic refund and transaction rollback, not silently keep the money.
6. **Mid-transaction cancellation**: user backs out after inserting money but before selection/dispense → full refund.
7. **Admin action during an in-flight transaction** (e.g., price change or restock while a user is mid-purchase): define whether in-flight transactions see old or new state (typically: in-flight transaction should complete with the price/stock snapshot it started with).

---

## 5. Explicit Design Expectations

You should be able to name and justify these patterns/structures in your design:

| Concern | Expected Pattern/Approach |
|---|---|
| Machine lifecycle (Idle → HasMoney → Dispense → SoldOut) | **State Pattern** |
| Multiple payment types (cash, card, UPI) | **Strategy Pattern** |
| Creating new product/machine variants | **Factory Pattern** (optional, if product creation gets complex) |
| Notifying admin/systems of low stock, transaction events | **Observer Pattern** (optional/stretch) |
| Inventory thread-safety | Atomic decrement per slot, or lock per product, or optimistic concurrency with versioning if distributed |
| Change calculation | Greedy or DP algorithm over available denomination counts, with a defined failure mode when exact change isn't possible |
| Admin vs. customer capabilities | **Interface segregation** — separate interfaces/classes, not one god object doing everything |
| Custom errors | `InsufficientFundsException`, `OutOfStockException`, `ExactChangeUnavailableException`, `InvalidProductException` — not generic exceptions |

---

## 6. Core Classes You'll Likely Need

- `VendingMachine` — orchestrator/context; holds `currentState`, delegates actions.
- `VendingMachineState` (interface) + `NoCoinState`, `HasCoinState`, `DispenseState`, `SoldOutState`.
- `Product` — id, name, price, category.
- `Inventory` — product → quantity map, thread-safe operations.
- `CashInventory` / `ChangeDispenser` — denomination → count map, change-making logic.
- `PaymentStrategy` (interface) + `CashPayment`, `CardPayment`, etc.
- `Transaction` — records what happened (product, amount paid, change returned, status) for auditability.
- `AdminController` — restock, updatePrice, collectCash, addProduct.
- Custom exception classes as listed above.

---

## 7. End-to-End Flow to Rehearse Out Loud

1. User views available items and prices (`NoCoinState`).
2. User inserts cash → balance updates, state → `HasCoinState`.
3. User selects a product.
4. System checks: is it in stock? Is balance ≥ price?
   - No stock → error, stay in current state (or `SoldOutState` for that slot).
   - Insufficient balance → error, prompt for more money, stay in `HasCoinState`.
   - Both pass → proceed.
5. State → `DispenseState`: decrement inventory, dispense item.
6. If overpaid, calculate and return change from `CashInventory`.
7. Log the transaction.
8. State resets → `NoCoinState`.

---

This should give you one unambiguous version of the requirements to design against — every use case from the variations you pasted is folded in here (multi-product, multi-denomination cash, change-making, concurrency, admin ops, exceptional flows, and the explicit State-pattern reasoning question).
