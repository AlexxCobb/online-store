package ru.zinovev.online.store.controller.dto;

import ru.zinovev.online.store.controller.dto.enums.EventType;

public record ProductEventDto(
        EventType eventType,
        String publicProductId,
        ProductForStandDto product,
        Long timestamp
) {
}
