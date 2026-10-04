package com.shikavani.lld.librarymanagement.enums;

public enum HoldStatus {
    WAITING,           // in the queue
    READY_FOR_PICKUP,  // a copy is kept aside for this member until pickupDeadline
    FULFILLED,         // member borrowed the reserved copy
    EXPIRED,           // member did not pick up in time
    CANCELLED          // member cancelled
}
