package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;

public record AddressDto(
        @NotBlank(message = "Укажите страну")
        @Size(min = 3, max = 100, message = "Название должно содержать от {min} до {max} символов")
        String country,
        @NotBlank(message = "Укажите город")
        @Size(min = 3, max = 100, message = "Название должно содержать от {min} до {max} символов")
        String town,
        @NotNull(message = "Укажите Индекс")
        @Positive(message = "Индекс состоит из шести положительных цифр")
        @Min(value = 100000, message = "Индекс должен состоять из 6 цифр")
        @Max(value = 999999, message = "Индекс должен состоять из 6 цифр")
        Integer zipCode,
        @NotBlank(message = "Укажите улицу/проспект")
        @Size(min = 3, max = 200, message = "Название должно содержать от {min} до {max} символов")
        String street,
        @NotNull(message = "Укажите номер дома")
        @Positive(message = "Номер дома может быть только положительным числом")
        Integer houseNumber,
        @Positive(message = "Номер квартиры может быть только положительным числом")
        Integer flatNumber,
        @NotNull(message = "Пожалуйста, выберите тип адреса.")
        AddressTypeName name
) {
}
