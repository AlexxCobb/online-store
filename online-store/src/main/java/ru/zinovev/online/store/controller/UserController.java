package ru.zinovev.online.store.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethodName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.OutOfStockException;
import ru.zinovev.online.store.model.CartDetails;
import ru.zinovev.online.store.model.CategoryDetails;
import ru.zinovev.online.store.model.ParametersDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.service.AddressService;
import ru.zinovev.online.store.service.CartService;
import ru.zinovev.online.store.service.CategoryService;
import ru.zinovev.online.store.service.OrderService;
import ru.zinovev.online.store.service.ProductService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/users")
public class UserController {

    private final AddressService addressService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final CartService cartService;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @PostMapping("/{publicUserId}/addresses")
    public String addAddress(@PathVariable String publicUserId, @Valid AddressDto addressDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Received POST request to add user delivery address with userId - {}, dto - {}", publicUserId,
                  addressDto);
        if (bindingResult.hasErrors()) {
            return "addresses";
        }
        addressService.addAddress(publicUserId, addressMapper.toAddressDetails(addressDto));
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ДОБАВЛЕН");
        return "redirect:/api/users/" + publicUserId + "/addresses?name=USER_ADDRESS";
    }

    @PatchMapping("/{publicUserId}/addresses/{publicAddressId}")
    public String updateAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                @Valid AddressUpdateDto addressUpdateDto, BindingResult bindingResult, Model model,
                                RedirectAttributes redirectAttributes) {
        log.debug("Received PATCH request to update user's (id - {}) delivery address (id - {}), dto - {}",
                  publicUserId, publicAddressId, addressUpdateDto);
        if (bindingResult.hasErrors()) {
            var address = addressService.getAddressByPublicId(publicAddressId);
            model.addAttribute("address", address);
            return "edit-address";
        }
        addressService.updateAddress(publicUserId, publicAddressId,
                                     addressMapper.toAddressUpdateDetails(addressUpdateDto));
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ОБНОВЛЁН");
        return "redirect:/api/users/" + publicUserId + "/addresses?name=USER_ADDRESS";
    }

    @GetMapping("/{publicUserId}/addresses/{publicAddressId}")
    public String editUserAddress(@PathVariable String publicUserId,
                                  @ModelAttribute AddressUpdateDto addressUpdateDto,
                                  @PathVariable String publicAddressId, Model model) {
        log.debug(
                "Received GET request to edit user address with id - {}, from user with id - {}",
                publicAddressId,
                publicUserId);
        var address = addressService.getAddressByPublicId(publicAddressId);

        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("address", address);
        return "edit-address";
    }

    @GetMapping("/{publicUserId}/addresses")
    public String getAddresses(@PathVariable String publicUserId,
                               @RequestParam(required = false) AddressTypeName name,
                               @RequestParam(required = false) Boolean isSystem, Model model,
                               HttpServletRequest request, @ModelAttribute AddressDto addressDto) {
        log.debug("Received GET request to get addresses");

        model.addAttribute("nameParam", request.getParameter("name"));
        model.addAttribute("isSystemParam", request.getParameter("isSystem"));
        if (name == null && isSystem == null) {
            return "redirect:/api/users/" + publicUserId + "/addresses?name=USER_ADDRESS";
        }
        var addresses = addressService.getAddresses(publicUserId, name, isSystem);
        model.addAttribute("addresses", addresses);
        return "addresses";
    }

    @DeleteMapping("/{publicUserId}/addresses/{publicAddressId}")
    public String deleteAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete address with id = {} from user id - {}", publicAddressId,
                  publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, false);
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО УДАЛЕН");
        return "redirect:/api/users/" + publicUserId + "/addresses?name=USER_ADDRESS";
    }

    @GetMapping("/categories")
    public String getCategories(Model model) {
        log.debug("Received GET request to get all categories");
        var categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @ModelAttribute("categories")
    public List<CategoryDetails> getCategoriesForAllPages() {
        return categoryService.getCategories();
    }


    @GetMapping("/products")
    public String searchProducts(
            @RequestParam(required = false) List<String> publicCategoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @Valid @ModelAttribute ProductParamDto productParamDto,
            Model model) { // если все параметры null вернуть все товары постранично
        log.debug("Received GET request to search products with parameters");
        var products = productService.searchProductsWithParameters(publicCategoryIds, minPrice, maxPrice,
                                                                   productMapper.toProductParamDetails(
                                                                           productParamDto));
        var brandValues = getUniqueParametersByKey(products, "brand");
        var colorValues = getUniqueParametersByKey(products, "color");
        var ramValues = getUniqueParametersByKey(products, "ram");
        var memoryValues = getUniqueParametersByKey(products, "memory");
        var priceMin =
                products.stream().map(ProductDetails::price).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        var priceMax =
                products.stream().map(ProductDetails::price).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

        model.addAttribute("brandValues", brandValues);
        model.addAttribute("colorValues", colorValues);
        model.addAttribute("ramValues", ramValues);
        model.addAttribute("memoryValues", memoryValues);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("products", products);
        return "products";
    }

    @PostMapping("/cart")
    public String addProductsToCart(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                                    @RequestParam(required = false) String publicUserId,
                                    @RequestParam String publicProductId,
                                    @RequestParam(defaultValue = "1") Integer quantity,
                                    HttpServletResponse response, RedirectAttributes redirectAttributes) {
        log.debug("Received POST request to add product to cart");
        try {
            var cart = cartService.addProductToCart(publicCartId, publicUserId, publicProductId, quantity);
            setCartCookie(response, cart.publicCartId());
            redirectAttributes.addFlashAttribute("successMessage", "ТОВАР ДОБАВЛЕН В КОРЗИНУ");
        } catch (OutOfStockException e) {
            var product = productService.getByPublicId(publicProductId);
            redirectAttributes.addFlashAttribute("errorMessage",
                                                 "Максимальное количество для добавления: " + product.stockQuantity()
                                                         + " шт.");
            redirectAttributes.addFlashAttribute("productId", publicProductId);
            redirectAttributes.addFlashAttribute("requestedQuantity", quantity);
        }
        return "redirect:/api/users/products";
    }

    @PatchMapping("/cart/{publicProductId}")
    public String removeProductFromCart(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                                        @RequestParam(required = false) String publicUserId,
                                        @PathVariable String publicProductId,
                                        RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received PATCH request to remove product from cart");
        cartService.removeProductFromCart(publicCartId, publicUserId, publicProductId);
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УДАЛЕН ИЗ КОРЗИНЫ");
        model.addAttribute("publicProductId", publicProductId);
        return "redirect:/api/users/cart";
    }

    @DeleteMapping("/cart")
    public String clearCart(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                            @RequestParam(required = false) String publicUserId,
                            RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to clear cart");
        cartService.clearCart(publicCartId, publicUserId);
        redirectAttributes.addFlashAttribute("successMessage", "КОРЗИНА УСПЕШНО ОЧИЩЕНА");
        return "redirect:/api/users/products";
    }

    @GetMapping("/cart")
    public String getCart(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                          @RequestParam(required = false) String publicUserId) {
        return "cart";
    }

    @ModelAttribute("cart")
    public CartDetails getCartForNavbar(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                                        @RequestParam(required = false) String publicUserId) {
        if (publicCartId == null) {
            return null;
        }
        return cartService.getCart(publicCartId, publicUserId);
    }

    @GetMapping("/{publicUserId}/cart")
    public String editCartToOrder(@PathVariable String publicUserId,
                                  Model model, @ModelAttribute OrderDto orderDto,
                                  @ModelAttribute AddressDto addressDto) {
        log.debug("Received GET request to edit cart to order from user with id- : {}", publicUserId);

        var userAddresses = addressService.getAddresses(publicUserId, AddressTypeName.USER_ADDRESS, false);
        var parcelAddresses = addressService.getAddresses(publicUserId, AddressTypeName.PARCEL_LOCKER, true);
        var storeAddresses = addressService.getAddresses(publicUserId, AddressTypeName.STORE_ADDRESS, true);
        model.addAttribute("userAddresses", userAddresses);
        model.addAttribute("parcelAddresses", parcelAddresses);
        model.addAttribute("storeAddresses", storeAddresses);
        model.addAttribute("deliveryMethods", DeliveryMethodName.values());
        model.addAttribute("paymentMethods", PaymentMethodName.values());
        return "edit-cart";
    }

    @PostMapping("/{publicUserId}/orders")
    public String addOrder(@PathVariable String publicUserId, @Valid OrderDto orderDto, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to add order, dto - {}, from user with id - {}", orderDto, publicUserId);
        if (bindingResult.hasErrors()) {
            var userAddresses = addressService.getAddresses(publicUserId, AddressTypeName.USER_ADDRESS, false);
            var parcelAddresses = addressService.getAddresses(publicUserId, AddressTypeName.PARCEL_LOCKER, true);
            var storeAddresses = addressService.getAddresses(publicUserId, AddressTypeName.STORE_ADDRESS, true);
            model.addAttribute("userAddresses", userAddresses);
            model.addAttribute("parcelAddresses", parcelAddresses);
            model.addAttribute("storeAddresses", storeAddresses);
            model.addAttribute("deliveryMethods", DeliveryMethodName.values());
            model.addAttribute("paymentMethods", PaymentMethodName.values());
            return "edit-cart";
        }
        try {
            orderService.createOrder(publicUserId, orderDto);
        } catch (OutOfStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", //доработать
                                                 e.getMessage());
        } catch (BadRequestException e) {
            if (e.getMessage()
                    .equals("Address with id - " + orderDto.publicAddressId()
                                    + " is not the address of the user with id - "
                                    + publicUserId)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                                                     "Выбранный адрес не является адресом покупателя");
                return "redirect:/api/users/" + publicUserId + "/cart";
            } else if (e.getMessage().equals("The selected address does not match the selected delivery method - "
                                                     + orderDto.deliveryMethodName().name())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                                                     "Выбранный адрес не соответствует выбранному способу доставки");
                return "redirect:/api/users/" + publicUserId + "/cart";
            }
        }
        model.addAttribute("publicUserId", publicUserId);
        redirectAttributes.addFlashAttribute("successMessage", "ЗАКАЗ УСПЕШНО ОФОРМЛЕН");
        return "redirect:/api/users/" + publicUserId + "/orders";
    }

    @GetMapping("/{publicUserId}/orders")
    public String getOrders(@PathVariable String publicUserId, Model model) {
        log.debug("Received GET request to get user orders with userId - {}", publicUserId);
        var orders = orderService.getUserOrders(publicUserId);

        model.addAttribute("paymentMethods", PaymentMethodName.values());
        model.addAttribute("orders", orders);
        return "orders";
    }

    private void setCartCookie(HttpServletResponse response, String publicCartId) {
        Cookie cookie = new Cookie("CART_ID", publicCartId);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private Set<String> getUniqueParametersByKey(List<ProductDetails> products, String paramKey) {
        return products.stream()
                .flatMap(productDetails -> productDetails.parameters().stream())
                .filter(parametersDetails1 -> parametersDetails1.key().equals(paramKey))
                .map(ParametersDetails::value)
                .collect(Collectors.toSet());
    }
}
