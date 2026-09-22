# Vending Machine — LLD Requirements

Note on terminology: "item" and "product" mean the same thing here — the thing being sold. Use a single `Product` class (name, price, category) with a `quantity` count in inventory. No need for a separate `Item` class unless per-unit tracking (batches, expiry) comes up.

---

## 1. Functional Requirements

**Product & Inventory**
- FR1: Machine supports multiple products, each with its own price and quantity.
- FR2: Machine displays all products with price and stock status.
- FR3: Machine tracks inventory accurately (quantity per product).

**Selection & Payment**
- FR4: User selects a product first.
- FR5: Machine checks stock. If unavailable → error immediately, no payment requested.
- FR6: If available, machine shows the price and accepts payment (coins/notes of different denominations, inserted incrementally if needed).
- FR7: Machine verifies inserted amount against the product's price:
    - Amount == price → dispense.
    - Amount > price → dispense + return change.
    - Amount < price → no dispense, prompt for more or allow cancel.
- FR8: User can cancel before dispensing and get a full refund.
- FR9: Support multiple payment types (cash now, card/UPI later) via a common abstraction.

**Dispensing & Change**
- FR10: On success, dispense the product and decrement its stock by 1.
- FR11: Return change using the fewest denominations possible, from the machine's own change inventory.
- FR12: If exact change isn't possible, reject the transaction rather than shortchange the user.

**Admin**
- FR13: Admin can restock products, add new products, update prices, collect cash, and refill change denominations — through a separate interface from the customer-facing one.

---

## 2. Non-Functional Requirements

- **Concurrency**: Multiple simultaneous transactions must not oversell stock or double-dispense.
- **Consistency**: If dispensing fails after payment, auto-refund — never keep money without giving the product.
- **Extensibility**: New products, payment types, or states should be addable without editing existing tested code (Open/Closed Principle).
- **Testability**: Core logic (state transitions, change calculation) should be unit-testable without real hardware.

---

## 3. State Model

Model this as a finite state machine, not if-else/switch on a state string.

| State | Meaning | Transitions |
|---|---|---|
| `IdleState` | No product selected. | `selectProduct()` → checks stock → `SoldOutState` (if unavailable) or `HasProductSelectedState`. |
| `ProductSelectedState` | Product chosen, waiting for payment. | `insertMoney()` → tracks balance, checks against price → `DispensingState` once fully paid. `cancel()` → refund → `IdleState`. |
| `DispensingState` | Dispensing in progress. | On success → decrement stock, return change if any → `IdleState`. On failure (jam) → refund, log → `IdleState`. |
| `SoldOutState` | Selected product has no stock. | Rejects that selection with an error; user can pick a different product. |

**Be ready to explain:** why not a big switch/if-else on a state variable inside every method?
- Every new state would mean editing every method → violates Open/Closed, high regression risk.
- Logic duplicates and drifts across methods instead of living in one place.
- Hard to unit-test one state in isolation.
- **Fix — State Pattern**: each state is its own class implementing a common interface (`selectProduct()`, `insertMoney()`, `dispense()`, `cancel()`). The `VendingMachine` just delegates to `currentState`. Adding a state = adding a class, not editing existing ones.

---

## 4. Exceptional Scenarios

1. Product out of stock → reject before asking for payment.
2. Insufficient funds → no dispense, prompt for more or allow cancel.
3. Exact change unavailable → reject transaction rather than shortchange.
4. Two users select the last unit at nearly the same time → exactly one succeeds.
5. Dispense fails after payment (jam) → auto-refund.
6. User cancels mid-transaction → full refund.

---

## 5. Design Patterns to Use

| Concern | Pattern |
|---|---|
| Machine lifecycle | State Pattern |
| Multiple payment types | Strategy Pattern |
| Admin vs. customer capabilities | Separate interfaces |
| Errors | Custom exceptions (`InsufficientFundsException`, `OutOfStockException`, `ExactChangeUnavailableException`) |

---

## 6. Core Classes

- `VendingMachine` — holds `currentState`, delegates actions.
- `VendingMachineState` (interface) + `IdleState`, `ProductSelectedState`, `DispensingState`, `SoldOutState`.
- `Product` — id, name, price.
- `Inventory` — product → quantity map (thread-safe).
- `CashInventory` — denomination → count map, change-making logic.
- `PaymentStrategy` (interface) + `CashPayment`, `CardPayment`.
- `AdminController` — restock, updatePrice, addProduct, collectCash.

---

## 7. End-to-End Flow

1. User views products (`IdleState`).
2. User selects a product → machine checks stock.
    - Out of stock → error, stay idle.
    - In stock → `ProductSelectedState`, price shown.
3. User inserts money → balance tracked.
4. Once balance ≥ price → `DispensingState`: decrement stock, dispense, return change if overpaid.
5. State resets → `IdleState`.