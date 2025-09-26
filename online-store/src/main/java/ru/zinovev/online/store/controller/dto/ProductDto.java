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
        @NotBlank(message = "Укажите название товара")
        @Size(min = 3, max = 150, message = "Название должно содержать от {min} до {max} символов")
        String name,
        @NotNull(message = "Укажите цену товара")
        @Positive(message = "Цена товара только положительная")
        BigDecimal price,
        @NotBlank(message = "Укажите цену товара")
        String categoryPublicId,
        List<ParametersDto> parameters,
        @NotNull(message = "Укажите вес товара")
        @Positive(message = "Вес товара только положительное число")
        BigDecimal weight,
        @NotNull(message = "Укажите объем товара")
        @Positive(message = "Объем товара только положительное число")
        BigDecimal volume,
        @NotNull(message = "Укажите количество товара")
        @PositiveOrZero(message = "Количество товара положительное число или 0")
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