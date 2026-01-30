package ru.zinovev.online.store.exception.model;

import lombok.Getter;
import ru.zinovev.online.store.exception.dto.OutOfStockDto;

import java.util.List;

@Getter
public class OutOfStockException extends RuntimeException {
    private final List<OutOfStockDto> issues;

    public OutOfStockException(String message, List<OutOfStockDto> issues) {
        super(message);
        this.issues = issues;
    }
}
