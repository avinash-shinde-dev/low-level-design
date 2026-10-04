package com.shikavani.lld.librarymanagement.enums;

/** Allowed changes between these statuses are listed in BookCopy. */
public enum BookCopyStatus {
    AVAILABLE,    // on the shelf
    BORROWED,     // with a member
    ON_HOLD,      // kept aside for the member at the head of the hold queue
    IN_TRANSIT,   // moving between branches (reserved for the future transfer feature)
    LOST,
    REMOVED       // taken out of the inventory for good
}
