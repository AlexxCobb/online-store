package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductParamDto(

        List<@Size(min = 4, max = 20) String> brand,
        List<@Size(min = 3, max = 10) String> color,
        List<@Positive @Size(min = 1, max = 64) Integer> ram,
        List<@Positive @Size(min = 64, max = 2048) String> memory
) {
}
