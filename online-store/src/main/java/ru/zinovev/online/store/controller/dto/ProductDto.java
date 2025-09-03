package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record ProductDto(
        @NotBlank(message = "Название товара ")
        @Size(min = 3, max = 150)
        String name,
        @NotNull
        @Positive
        BigDecimal price,
        @NotBlank
        String categoryPublicId,
        List<ParametersDto> parameters,
        @NotNull
        @Positive
        BigDecimal weight,
        @NotNull
        @Positive
        BigDecimal volume,
        @NotNull
        @PositiveOrZero
        Integer stockQuantity,
        String imagePath
) {
        public ProductDto {
                if (parameters == null) {
                        parameters = new ArrayList<>();
                        parameters.add(new ParametersDto("brand", ""));
                        parameters.add(new ParametersDto("color", ""));
                        parameters.add(new ParametersDto("ram", ""));
                        parameters.add(new ParametersDto("memory", ""));
                }
        }
}