# Problem: Multi-Branch Library Management System

## 1. Context

Design and implement the core of a library management system for a **network of branches**. All branches share **one common catalog**, but each branch keeps its **own physical inventory**. Members can search, borrow, return, and reserve books. Librarians administer the catalog and members. The system computes fines and sends notifications.

Implement this in memory (no database, no UI, no frameworks).

---

## 2. Functional Requirements

### 2.1 Catalog and Inventory

- A **Book** (title-level record) has: ISBN, title, one or more authors, one or more genres, publisher, and publication year. The catalog is shared across all branches.
- A **BookCopy** is a physical copy. Each copy belongs to exactly one branch, has a unique copy ID, and has a status.
- A copy's status can be: `AVAILABLE`, `BORROWED`, `ON_HOLD` (reserved for a member, awaiting pickup), `IN_TRANSIT` (moving between branches), `LOST`, or `REMOVED`.
- Only valid status changes are allowed (for example, `AVAILABLE → BORROWED → AVAILABLE`). Invalid changes must be rejected.
- Librarians can:
    - Add a book to the catalog and update its details.
    - Add or remove copies at a branch. A copy that is currently borrowed cannot be removed.
    - Remove a book from the catalog, only if no active loans or holds exist for it.

### 2.2 Users and Roles

- There are two roles: **MEMBER** and **LIBRARIAN**.
- Role-based access control applies to every operation. For example, only librarians can add or remove books, and only members can borrow.
- Members register with a **membership tier** (for example `BASIC`, `PREMIUM`, `STUDENT`). Each tier defines:
    - Maximum concurrent borrows
    - Loan duration in days
    - Fine rate multiplier or fine policy
- All limits are **configurable**, not hardcoded.
- Librarians administer members: register, suspend, reactivate, and upgrade or downgrade tier.
- A member cannot borrow if they are suspended, or if their unpaid fines exceed a configurable threshold.

### 2.3 Search

- Search by **title**, **author**, **ISBN**, or **genre**.
- Title and author use case-insensitive partial matching. ISBN and genre use exact matching.
- Results can optionally be filtered by branch, or by "available only".
- **Compound queries** (for example `genre = "SciFi" AND author contains "Asimov"`, and combinations using OR / NOT) must be addable in the future **without modifying existing search code**. You do not need to implement them now, but your design must make it clear how they would be added.

### 2.4 Borrow and Return

**Borrow** a copy at a branch:
- Validate member status, borrow limit, and outstanding fines.
- Set the due date from the member's tier.
- Create a **Transaction** record.

**Return** a copy:
- A copy may be returned at **any branch** in the network. If returned to a branch other than its own, either mark it `IN_TRANSIT` or treat it as belonging to the new branch. The choice is yours; document it.
- Close the transaction, record the return timestamp, and calculate the fine if overdue.
- If the title has an active hold queue, the copy goes to the head of the queue (see 2.5).

**Renewal** (bonus): allowed once, and only if no holds exist for that title.

### 2.5 Hold / Reservation Queue

- If **no copy** of a title is available (within the scope you choose, see Assumptions), a member can place a hold.
- Holds for a title are served **strictly FIFO**.
- When a copy is returned and a hold exists:
    - The copy becomes `ON_HOLD` for the first member in the queue.
    - That member is notified.
    - The member has a configurable **pickup window** (for example 48 hours). If they do not borrow within the window, the hold expires and the copy is offered to the next member in the queue.
- A member cannot place a duplicate hold on the same title, and cannot place a hold on a title that has an available copy.
- A member can cancel their hold. Cancelling a hold that currently owns an `ON_HOLD` copy must correctly pass that copy to the next member.

### 2.6 Fine System

- Base fine = `overdueDays × dailyRate`, adjusted by a per-tier rate multiplier.
- A **grace period** (for example 2 days with no fine) must be configurable and replaceable without modifying existing classes.
- **Long-term penalty**: if the overdue period crosses a threshold (for example 30 days), an additional flat or escalating penalty applies. Multiple rules can apply together.
- **Cap**: the total fine may be capped (for example, at the price of the book).
- Adding a new fine rule (for example weekend-free or holiday-free) must **not require changing existing classes**.
- Fine details (base fine, penalties, waivers, final amount) are stored on the transaction.

### 2.7 Transactions

- Track the full lifecycle: `BORROWED → RETURNED`, `OVERDUE → RETURNED`, or `LOST`.
- Record the borrow timestamp, due date, return timestamp, and fine breakdown.
- Support these queries:
    - Active loans for a member
    - History for a copy
    - All overdue loans
- Time-dependent logic (overdue, hold expiry, reminders) must be testable without real waiting or `sleep`.

### 2.8 Notifications

Notifications are triggered by system events. Each notification carries a recipient, a type, and a message.

- **Due-date reminder** (for example 2 days before the due date)
- **Overdue and fine notice**
- **Hold available** (copy ready for pickup)
- **Hold expired**

Delivery channels (Email, SMS, Push) must be pluggable, and a member may have one or more preferred channels. A failing channel must not break the borrow or return flow.

### 2.9 Scheduled / System Operations

The system must expose operations that a scheduler could trigger, at minimum:
- Sending due-date reminders
- Expiring holds whose pickup window has passed

---

## 3. Non-Functional Requirements

| Area | Requirement |
|---|---|
| **Fairness** | The hold queue is FIFO and stays correct under concurrent adds, cancels, and returns. |
| **Multi-branch** | The catalog (book metadata) is shared. Inventory (copies) is branch-local. Adding a branch must not affect existing code. |
| **Extensibility** | New fine rules, search criteria, notification channels, membership tiers, and item types must be addable with minimal changes to existing code. |
| **Testability** | Time and notification delivery must be controllable or replaceable in tests. |
| **Consistency** | The system must never enter an invalid state (for example, two members holding the same copy, or a negative available count). |

---

## 4. Concurrency Requirements

Assume many threads (members and librarians) call the system at the same time.

1. **Atomic checkout per copy.** Two members must never successfully borrow the same copy. When only one copy remains and two members try to borrow it simultaneously, exactly one succeeds and the other fails or is queued.
2. **Atomic return-to-hold handoff.** Returning a copy, polling the hold queue, and changing the copy's status must behave as one consistent step. A copy must not be borrowable by a walk-in member while it is being handed to a member in the hold queue.
3. **Hold queue safety.** Concurrent place, cancel, and expire operations must not lose entries, duplicate entries, or break FIFO order.
4. **Borrow-limit safety.** Two concurrent borrows by the same member must not push them past their limit.
5. **No deadlocks.** Your design must be deadlock-free.
6. **No single global lock.** Concurrency must not be serialized behind one lock for the whole system.

You must include a **multi-threaded demo or test** that proves requirement 1 (for example, 50 threads competing for 1 copy results in exactly 1 success).

---

## 5. Extension Hooks (Design for These; Do Not Fully Implement)

In comments or a short design note, explain how your design would absorb each of the following **without rewriting core classes**:

1. **E-books**: no physical copy limit, but a licensing constraint (for example, max N concurrent digital loans per license, and licenses expire).
2. **Inter-branch transfer requests**: a member at Branch A requests a copy that sits at Branch B, with a transfer lifecycle of `REQUESTED → IN_TRANSIT → ARRIVED`.
3. **Fine waivers and tier-upgrade workflows**: a librarian waives a fine, and a member requests an upgrade that goes through approval.
4. **Compound search queries** using AND, OR, and NOT.

---

## 6. Assumptions (You May Override, but State Them)

- A single currency. Fines must not use floating-point types.
- Payment processing is out of scope. Only a way to record a fine payment by a member is needed.
- Holds are scoped by title. Decide whether a hold covers one branch or the whole network, and justify your choice.
- No persistence. Use in-memory storage only.
- Authentication is out of scope. Authorization (role checks) is in scope.

---

## 7. Sample Scenarios Your Demo Must Cover

1. Register a librarian and two members (`BASIC` with limit 2, `PREMIUM` with limit 5). Add a book with 1 copy at Branch-1.
2. Member A borrows it. Member B's borrow fails, so B places a hold. Member C also places a hold (queue: B, C).
3. A returns the book 5 days late under a 2-day grace period and a ₹10/day rate. The fine is calculated correctly (3 chargeable days), and B is notified that the copy is on hold for them.
4. B does not pick up within the window. The hold expires, the copy goes to C, and C is notified.
5. A `BASIC` member tries to borrow a 3rd book. It is rejected with a clear error.
6. A member tries to add a book. It is rejected (role check).
7. Concurrency: 50 threads try to borrow the last copy, and exactly 1 succeeds.
8. Search by author (partial), ISBN (exact), and genre.
9. A book returned 45 days late triggers the long-term penalty rule on top of the daily fine, and the fine breakdown is visible on the transaction.

---

## 8. Deliverables

- Any object-oriented language.
- Class diagram (or a list of classes with their responsibilities).
- The code.
- A driver that runs all the scenarios in Section 7.
- A short note on your concurrency decisions.

---

## 9. Bonus (Optional)

- Renewal with a hold-aware check
- Automatic scheduling of reminders and hold expiry
- Lost-book flow: fine equals the replacement cost, and the copy is marked `LOST`
- Audit log of librarian actions