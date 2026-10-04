package com.shikavani.lld.librarymanagement.notification;

import com.shikavani.lld.librarymanagement.enums.EventType;
import com.shikavani.lld.librarymanagement.models.Member;

public record Notification(Member recipient, EventType eventType, String message) { }
