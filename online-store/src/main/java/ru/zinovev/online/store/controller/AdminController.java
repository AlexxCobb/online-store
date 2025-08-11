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
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.mapper.CategoryMapper;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admins")
public class AdminController {

    private final AddressService addressService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @PostMapping("/{publicUserId}/addresses")
    public AddressDetails addSystemAddress(@PathVariable String publicUserId, @Valid AddressDto addressDto,
                                           @RequestParam AddressTypeName name) {
        log.debug("Received POST request to add system address dto - {}, with type - {}, from user with id - {}",
                  addressDto, name, publicUserId);
        return addressService.addSystemAddress(publicUserId, addressMapper.toAddressDetails(addressDto), name);
    }

    @PatchMapping("/{publicUserId}/addresses/{publicAddressId}")
    public AddressDetails updateSystemAddress(@PathVariable String publicUserId,
                                              @Valid AddressUpdateDto addressUpdateDto,
                                              @PathVariable String publicAddressId,
                                              @RequestParam AddressTypeName name) {
        log.debug(
                "Received PATCH request to update system address (id - {}), dto - {}, type - {}, from user with id - {}",
                publicAddressId,
                addressUpdateDto, name, publicUserId);
        return addressService.updateSystemAddress(publicAddressId,
                                                  addressMapper.toAddressUpdateDetails(addressUpdateDto), name);
    }

    @DeleteMapping("/{publicUserId}/addresses/{publicAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSystemAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId) {
        log.debug("Received DELETE request to delete system address with id = {}, from user with id - {}",
                  publicAddressId, publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, true);
    }

    @PostMapping("/{publicUserId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDetails addCategory(@PathVariable String publicUserId, @Valid @RequestBody CategoryDto categoryDto) {
        log.debug("Received POST request to add category");
        return categoryService.createCategory(publicUserId, categoryMapper.toCategoryDetails(categoryDto));
    }

    @PatchMapping("/{publicUserId}/categories/{publicCategoryId}")
    public CategoryDetails updateCategory(@PathVariable String publicUserId, @PathVariable String publicCategoryId,
                                          @Valid @RequestBody
                                          CategoryDto categoryDto) {
        log.debug("Received PATCH request to update category with id = {}", publicCategoryId);
        return categoryService.updateCategory(publicUserId, publicCategoryId,
                                              categoryMapper.toCategoryDetails(categoryDto));
    }

    @DeleteMapping("/{publicUserId}/categories/{publicCategoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String publicUserId, @PathVariable String publicCategoryId) {
        log.debug("Received DELETE request to delete category with id = {}", publicCategoryId);
        categoryService.deleteCategory(publicUserId, publicCategoryId);
    }

    @PostMapping("/{publicUserId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetails addProduct(@PathVariable String publicUserId, @Valid @RequestBody ProductDto productDto) {
        log.debug("Received POST request to add product");
        return productService.createProduct(publicUserId, productMapper.toProductDetails(productDto));
    }

    @PatchMapping("/{publicUserId}/products/{publicProductId}")
    public ProductDetails updateProduct(@PathVariable String publicUserId, @PathVariable String publicProductId,
                                        @Valid @RequestBody
                                        ProductUpdateDto productUpdateDto) {
        log.debug("Received PATCH request to update product with id = {}", publicProductId);
        return productService.updateProduct(publicUserId, productMapper.toProductUpdateDetails(productUpdateDto),
                                            publicProductId);
    }

    @DeleteMapping("/{publicUserId}/products/{publicProductId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String publicUserId, @PathVariable String publicProductId) {
        log.debug("Received DELETE request to delete product with id = {}", publicProductId);
        productService.deleteProduct(publicUserId, publicProductId);
    }

    @PatchMapping("/{publicUserId}/orders/{publicOrderId}")
    public OrderDetails changeOrderStatus(@PathVariable String publicUserId, @PathVariable String publicOrderId,
                                          @RequestParam OrderStatusName orderStatusName,
                                          @RequestParam(required = false) PaymentStatusName paymentStatusName) {
        log.debug(
                "Received PATCH request to change order status, with id - {}, orderStatusName - {}, paymentStatusName -{}",
                publicOrderId, orderStatusName, paymentStatusName);
        return orderService.changeOrderStatus(publicOrderId, publicUserId, orderStatusName, paymentStatusName);
    }

    @GetMapping("/{publicUserId}/orders") // id
    public List<OrderShortDetails> getOrders(@PathVariable String publicUserId) {
        log.debug("Received GET request to get all user orders");
        return orderService.getAllOrders(publicUserId);
    }
}
