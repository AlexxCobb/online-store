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
import ru.zinovev.online.store.controller.dto.UserDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethodName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.OutOfStockException;
import ru.zinovev.online.store.exception.model.UserNotAuthenticatedException;
import ru.zinovev.online.store.model.CartDetails;
import ru.zinovev.online.store.model.CategoryDetails;
import ru.zinovev.online.store.service.AddressService;
import ru.zinovev.online.store.service.CartService;
import ru.zinovev.online.store.service.CategoryService;
import ru.zinovev.online.store.service.OrderService;
import ru.zinovev.online.store.service.ProductService;
import ru.zinovev.online.store.service.StatisticService;

import java.math.BigDecimal;
import java.util.List;
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
    private final StatisticService statisticService;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;
    private final UserDto sessionUserDto;

    @PostMapping("/addresses")
    public String addAddress(@Valid AddressDto addressDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "addresses";
        }
        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        log.debug("Received POST request to add user delivery address with userId - {}, dto - {}",
                  publicUserId, addressDto);
        addressService.addAddress(publicUserId, addressMapper.toAddressDetails(addressDto));
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ДОБАВЛЕН");
        return "redirect:/api/users/addresses?name=USER_ADDRESS";
    }

    @PatchMapping("/addresses/{publicAddressId}")
    public String updateAddress(@PathVariable String publicAddressId,
                                @Valid AddressUpdateDto addressUpdateDto, BindingResult bindingResult, Model model,
                                RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()) {
            var address = addressService.getAddressByPublicId(publicAddressId);
            model.addAttribute("address", address);
            return "edit-address";
        }
        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        log.debug("Received PATCH request to update user's (id - {}) delivery address (id - {}), dto - {}",
                  publicUserId, publicAddressId, addressUpdateDto);
        addressService.updateAddress(publicUserId, publicAddressId,
                                     addressMapper.toAddressUpdateDetails(addressUpdateDto));
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ОБНОВЛЁН");
        return "redirect:/api/users/addresses?name=USER_ADDRESS";
    }

    @GetMapping("/addresses/{publicAddressId}")
    public String editUserAddress(@ModelAttribute AddressUpdateDto addressUpdateDto,
                                  @PathVariable String publicAddressId, Model model) {

        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        log.debug(
                "Received GET request to edit user address with id - {}, from user with id - {}",
                publicAddressId, publicUserId);
        var address = addressService.getAddressByPublicId(publicAddressId);
        model.addAttribute("address", address);

        return "edit-address";
    }

    @GetMapping("/addresses")
    public String getAddresses(
            @RequestParam(required = false) AddressTypeName name,
            @RequestParam(required = false) Boolean isSystem, Model model,
            HttpServletRequest request, @ModelAttribute AddressDto addressDto) {
        log.debug("Received GET request to get addresses");

        model.addAttribute("nameParam", request.getParameter("name"));
        model.addAttribute("isSystemParam", request.getParameter("isSystem"));
        if (name == null && isSystem == null) {
            return "redirect:/api/users/addresses?name=USER_ADDRESS";
        }
        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        var addresses = addressService.getAddresses(publicUserId, name, isSystem);
        model.addAttribute("addresses", addresses);

        return "addresses";
    }

    @DeleteMapping("/addresses/{publicAddressId}")
    public String deleteAddress(@PathVariable String publicAddressId,
                                RedirectAttributes redirectAttributes) {
        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        log.debug("Received DELETE request to delete address with id = {} from user id - {}", publicAddressId,
                  publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, false);

        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО УДАЛЕН");
        return "redirect:/api/users/addresses?name=USER_ADDRESS";
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
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer limit,
            @Valid @ModelAttribute ProductParamDto productParamDto,// добавить параметры в Dto
            Model model) {
        log.debug("Received GET request to search products with parameters");
        var products = productService.searchProductsWithParameters(
                publicCategoryIds, minPrice, maxPrice,
                productMapper.toProductParamDetails(productParamDto), page, limit);

        var brandValues = productService.getUniqueParametersByKey("brand");
        var colorValues = productService.getUniqueParametersByKey("color");
        var ramValues = productService.getUniqueParametersByKey("ram");
        var memoryValues = productService.getUniqueParametersByKey("memory");
        var priceMin = productService.getMinPrice();
        var priceMax = productService.getMaxPrice();
        var topProducts = statisticService.findSixPopularProducts();
        var categories = categoryService.getCategories();
        var categoryMap =
                categories.stream().collect(Collectors.toMap(CategoryDetails::publicCategoryId, CategoryDetails::name));

        model.addAttribute("brandValues", brandValues);
        model.addAttribute("colorValues", colorValues);
        model.addAttribute("ramValues", ramValues);
        model.addAttribute("memoryValues", memoryValues);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("products", products);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("sessionUserDto", sessionUserDto);
        return "products";
    }

    @PostMapping("/cart")
    public String addProductsToCart(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                                    @RequestParam String publicProductId,
                                    @RequestParam(defaultValue = "1") Integer quantity,
                                    HttpServletResponse response, RedirectAttributes redirectAttributes) {
        log.debug("Received POST request to add product to cart");
        try {
            var publicUserId = sessionUserDto.getPublicUserId()
                    .orElse(null);
            var cart = cartService.addProductToCart(publicCartId, publicUserId, publicProductId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "ТОВАР ДОБАВЛЕН В КОРЗИНУ");
            setCartCookie(response, cart.publicCartId());
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
                                        @PathVariable String publicProductId,
                                        RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received PATCH request to remove product from cart");
        var publicUserId = sessionUserDto.getPublicUserId()
                .orElse(null);
        cartService.removeProductFromCart(publicCartId, publicUserId, publicProductId);

        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УДАЛЕН ИЗ КОРЗИНЫ");
        return "redirect:/api/users/cart";
    }

    @DeleteMapping("/cart")
    public String clearCart(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                            RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to clear cart");
        var publicUserId = sessionUserDto.getPublicUserId()
                .orElse(null);
        cartService.clearCart(publicCartId, publicUserId);
        redirectAttributes.addFlashAttribute("successMessage", "КОРЗИНА УСПЕШНО ОЧИЩЕНА");
        return "redirect:/api/users/products";
    }

    @GetMapping("/cart")
    public String getCart(@CookieValue(value = "CART_ID", required = false) String publicCartId, Model model) {
        return "cart";
    }

    @ModelAttribute("cart")
    public CartDetails getCartForNavbar(@CookieValue(value = "CART_ID", required = false) String publicCartId) {
        var publicUserId = sessionUserDto.getPublicUserId()
                .orElse(null);
        return cartService.getCart(publicCartId, publicUserId);
    }

    @GetMapping("/cart/edit")
    public String getEditCartToOrder(Model model, @ModelAttribute OrderDto orderDto) {
        log.debug("Received GET request to edit cart to order from user with email- : {}", sessionUserDto.getEmail());

        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
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

    @PostMapping("/orders")
    public String addOrder(@Valid OrderDto orderDto, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Model model) {

        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        log.debug("Received POST request to add order, dto - {}, from user with id - {}", orderDto,
                  publicUserId);

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
            redirectAttributes.addFlashAttribute("stockIssues", e.getIssues());
            redirectAttributes.addFlashAttribute("errorMessage", "Невозможно оформить заказ. Обнаружены проблемы с наличием товаров.");
            return "redirect:/api/users/cart/edit";
        } catch (BadRequestException e) {
            if (e.getMessage()
                    .equals("Address with id - " + orderDto.publicAddressId()
                                    + " is not the address of the user with id - "
                                    + publicUserId)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                                                     "Выбранный адрес не является адресом покупателя");
                return "redirect:/api/users/cart/edit";
            } else if (e.getMessage().equals("The selected address does not match the selected delivery method - "
                                                     + orderDto.deliveryMethodName().name())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                                                     "Выбранный адрес не соответствует выбранному способу доставки");
                return "redirect:/api/users/cart/edit";
            }
        }
        redirectAttributes.addFlashAttribute("successMessage", "ЗАКАЗ УСПЕШНО ОФОРМЛЕН");
        return "redirect:/api/users/orders";
    }

    @GetMapping("/orders")
    public String getOrders(@RequestParam(defaultValue = "0") Integer page,
                            @RequestParam(defaultValue = "5") Integer limit,
                            Model model) {
        var publicUserId = getPublicUserIdOrThrowException(sessionUserDto);
        log.debug("Received GET request to get user orders with userId - {}", publicUserId);

        var orders = orderService.getUserOrders(publicUserId, page, limit);
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

    private String getPublicUserIdOrThrowException(UserDto userDto) {
        return userDto.getPublicUserId()
                .orElseThrow(() -> new UserNotAuthenticatedException("Что-то пошло не так. Попробуйте еще раз!"));
    }
}