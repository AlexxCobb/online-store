package ru.zinovev.online.store.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class CartUpdateListWrapper {

    private List<CartUpdateDto> updates;

    @NoArgsConstructor
    @Setter
    @Getter
    public static class CartUpdateDto {
        private String publicProductId;
        private Integer availableQuantity;
    }
}