package com.fintrack.application.port.out.notification;

import com.fintrack.domain.event.DomainEvent;

public interface NotificationPort {

    void notify(DomainEvent event);
}