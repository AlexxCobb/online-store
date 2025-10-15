package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductParamDto(

        List<@Size(min = 1, max = 30) String> brand,
        List<@Size(min = 1, max = 20) String> color,
        List<@Positive @Min(1) @Max(256) Integer> ram,
        List<@Positive @Min(1) @Max(2048) Integer> memory
) {
}
