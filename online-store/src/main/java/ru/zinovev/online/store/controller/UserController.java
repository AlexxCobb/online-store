package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.CategoryDetails;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.service.AddressService;
import ru.zinovev.online.store.service.CategoryService;
import ru.zinovev.online.store.service.OrderService;
import ru.zinovev.online.store.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/users")
public class UserController {

    private final AddressService addressService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;


    @PostMapping("/{publicUserId}/addresses")
    public AddressDetails addAddress(@PathVariable String publicUserId, @Valid AddressDto addressDto) {
        log.debug("Received POST request to add user delivery address with userId - {}, dto - {}", publicUserId,
                  addressDto);
        return addressService.addAddress(publicUserId, addressMapper.toAddressDetails(addressDto));
    }

    @PatchMapping("/{publicUserId}/addresses/{publicAddressId}")
    public AddressDetails updateAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                        @Valid AddressUpdateDto addressUpdateDto) {
        log.debug("Received PATCH request to update user's (id - {}) delivery address (id - {}), dto - {}",
                  publicUserId, publicAddressId, addressUpdateDto);
        return addressService.updateAddress(publicUserId, publicAddressId,
                                            addressMapper.toAddressUpdateDetails(addressUpdateDto));
    }

    @GetMapping("/{publicUserId}/addresses")
    public List<AddressDetails> getAddresses(@PathVariable String publicUserId,
                                             @RequestParam(required = false) AddressTypeName name,
                                             @RequestParam(required = false) Boolean isSystem) {
        log.debug("Received GET request to get addresses");
        return addressService.getAddresses(publicUserId, name, isSystem);
    }

    @DeleteMapping("/{publicUserId}/addresses/{publicAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId) {
        log.debug("Received DELETE request to delete address with id = {} from user id - {}", publicAddressId,
                  publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, false);
    }

    @GetMapping("/{publicUserId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CategoryDetails> getCategories(@PathVariable String publicUserId) {
        log.debug("Received GET request to get all categories");
        return categoryService.getCategories(publicUserId);
    }

    @GetMapping("/{publicUserId}/products")
    public List<ProductDetails> searchProducts(
            @RequestParam(required = false) List<String> publicCategoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @Valid ProductParamDto productParamDto) { // если все параметры null вернуть все товары постранично
        log.debug("Received GET request to search products with parameters");
        return productService.searchProductsWithParameters(publicCategoryIds, minPrice, maxPrice,
                                                           productMapper.toProductParamDetails(productParamDto));
    }

    @PostMapping("/{publicUserId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetails addOrder(@PathVariable String publicUserId, @Valid @RequestBody OrderDto orderDto) {
        log.debug("Received POST request to add order, dto - {}, from user with id - {}", orderDto, publicUserId);
        return orderService.createOrder(publicUserId, orderDto);
    }

    @GetMapping("/{publicUserId}/orders")
    public List<OrderShortDetails> getOrders(@PathVariable String publicUserId) {
        log.debug("Received GET request to get user orders with userId - {}", publicUserId);
        return orderService.getUserOrders(publicUserId);
    }
}
