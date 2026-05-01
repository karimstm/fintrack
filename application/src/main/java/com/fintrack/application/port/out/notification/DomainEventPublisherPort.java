package com.fintrack.application.port.out.notification;

import com.fintrack.domain.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisherPort {

    void publish(DomainEvent event);

    void publishAll(List<DomainEvent> events);
}