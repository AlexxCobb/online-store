package ru.zinovev.online.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zinovev.online.store.controller.dto.ProductForStandDto;
import ru.zinovev.online.store.service.ProductService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("api/stand")
@RequiredArgsConstructor
public class AdvertisingController {

    private final ProductService productService;

    @GetMapping("/products")
    public Set<ProductForStandDto> getAllProductsForStand() {
        log.debug("Received GET request to get products for advertising_stand App");
        return productService.getProductsForStand();
    }
}