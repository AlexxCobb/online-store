package ru.zinovev.online.store.controller.dto;

import ru.zinovev.online.store.controller.dto.enums.EventType;

import java.time.Instant;

public record ProductEventDto(
        String eventId,
        EventType eventType,
        String publicProductId,
        ProductForStandDto product,
        Instant createdAt
) {
}
