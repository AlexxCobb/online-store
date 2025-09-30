package ru.zinovev.online.store.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.controller.dto.UserDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.mapper.CategoryMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.exception.model.AlreadyExistException;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.DuplicateProductException;
import ru.zinovev.online.store.exception.model.UserNotAuthenticatedException;
import ru.zinovev.online.store.service.AddressService;
import ru.zinovev.online.store.service.CategoryService;
import ru.zinovev.online.store.service.OrderService;
import ru.zinovev.online.store.service.ProductService;
import ru.zinovev.online.store.service.StatisticService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
    private final StatisticService statisticService;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final UserDto sessionUserDto;

    @GetMapping("/home")
    public String homePage() {
        return "admin/admin-home";
    }

    @GetMapping("/addresses")
    public String getAddresses(@RequestParam(required = false) AddressTypeName name, Model model,
                               HttpServletRequest request, @ModelAttribute AddressDto addressDto) {
        log.debug("Received GET request to get system addresses");
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        var addresses = addressService.getAddresses(publicUserId, name, true);

        model.addAttribute("addressTypes", List.of(AddressTypeName.STORE_ADDRESS, AddressTypeName.PARCEL_LOCKER));
        model.addAttribute("nameParam", request.getParameter("name"));
        model.addAttribute("addresses", addresses);
        return "admin/addresses";
    }

    @PostMapping("/addresses")
    public String addSystemAddress(@Valid AddressDto addressDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, Model model) {
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        log.debug("Received POST request to add system address dto - {}, from admin with id - {}",
                  addressDto, publicUserId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("addressTypes", List.of(AddressTypeName.STORE_ADDRESS, AddressTypeName.PARCEL_LOCKER));
            return "admin/addresses";
        }
        addressService.addSystemAddress(publicUserId, addressMapper.toAddressDetails(addressDto), addressDto.name());
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ДОБАВЛЕН");
        return "redirect:/api/admins/addresses";
    }

    @GetMapping("/addresses/{publicAddressId}")
    public String getEditSystemAddress(@ModelAttribute AddressUpdateDto addressUpdateDto,
                                       @PathVariable String publicAddressId, Model model) {
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        log.debug(
                "Received GET request to edit system address id - {}, from admin with id - {}",
                publicAddressId,
                publicUserId);
        var address = addressService.getAddressByPublicId(publicAddressId);
        model.addAttribute("address", address);
        return "admin/edit-address";
    }

    @PatchMapping("/addresses/{publicAddressId}")
    public String updateSystemAddress(@Valid AddressUpdateDto addressUpdateDto, BindingResult bindingResult,
                                      @PathVariable String publicAddressId, Model model,
                                      RedirectAttributes redirectAttributes) {
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        log.debug(
                "Received PATCH request to update system address (id - {}), dto - {}, from admin with id - {}",
                publicAddressId,
                addressUpdateDto, publicUserId);
        if (bindingResult.hasErrors()) {
            var address = addressService.getAddressByPublicId(publicAddressId);
            model.addAttribute("address", address);
            return "admin/edit-address";
        }
        addressService.updateSystemAddress(publicUserId, publicAddressId,
                                           addressMapper.toAddressUpdateDetails(addressUpdateDto));
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ОБНОВЛЕН");
        return "redirect:/api/admins/addresses";
    }

    @DeleteMapping("/addresses/{publicAddressId}")
    public String deleteSystemAddress(@PathVariable String publicAddressId,
                                      RedirectAttributes redirectAttributes) {
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        log.debug("Received DELETE request to delete system address with id = {}, from admin with id - {}",
                  publicAddressId, publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, true);
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО УДАЛЕН");
        return "redirect:/api/admins/addresses";
    }

    @GetMapping("/categories")
    public String getCategories(@ModelAttribute CategoryDto categoryDto,
                                Model model) {
        log.debug("Received GET request to get all categories");
        var categories = categoryService.getCategories();

        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @GetMapping("/categories/{publicCategoryId}")
    public String editCategory(@ModelAttribute CategoryDto categoryDto,
                               @PathVariable String publicCategoryId, Model model) {
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        log.debug(
                "Received GET request to edit category with id - {}, from admin with id - {}",
                publicCategoryId, publicUserId);
        var category = categoryService.getCategoryByPublicId(publicCategoryId);
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/categories")
    public String addCategory(@Valid CategoryDto categoryDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to add category");
        if (bindingResult.hasErrors()) {
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            return "admin/categories";
        }
        try {
            var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
            categoryService.createCategory(publicUserId, categoryMapper.toCategoryDetails(categoryDto));
        } catch (BadRequestException e) {
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("errorMessage", "КАТЕГОРИЯ " + categoryDto.name() + " УЖЕ СУЩЕСТВУЕТ");
            return "admin/categories";
        }

        redirectAttributes.addFlashAttribute("successMessage", "КАТЕГОРИЯ УСПЕШНО ДОБАВЛЕНА");
        return "redirect:/api/admins/categories";
    }

    @PatchMapping("/categories/{publicCategoryId}")
    public String updateCategory(@PathVariable String publicCategoryId,
                                 @Valid CategoryDto categoryDto, BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Received PATCH request to update category with id = {}", publicCategoryId);
        if (bindingResult.hasErrors()) {
            var category = categoryService.getCategoryByPublicId(publicCategoryId);
            model.addAttribute("category", category);
            return "admin/edit-category";
        }
        try {
            var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
            categoryService.updateCategory(publicUserId, publicCategoryId,
                                           categoryMapper.toCategoryDetails(categoryDto));
        } catch (BadRequestException e) {
            var category = categoryService.getCategoryByPublicId(publicCategoryId);
            model.addAttribute("errorMessage", "КАТЕГОРИЯ " + categoryDto.name() + " УЖЕ СУЩЕСТВУЕТ");
            model.addAttribute("category", category);
            return "admin/edit-category";
        }
        redirectAttributes.addFlashAttribute("successMessage", "КАТЕГОРИЯ УСПЕШНО ОБНОВЛЕНА");
        return "redirect:/api/admins/categories";
    }

    @DeleteMapping("/categories/{publicCategoryId}")
    public String deleteCategory(@PathVariable String publicCategoryId,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete category with id = {}", publicCategoryId);
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        categoryService.deleteCategory(publicUserId, publicCategoryId);
        redirectAttributes.addFlashAttribute("successMessage", "КАТЕГОРИЯ УСПЕШНО УДАЛЕНА");
        return "redirect:/api/admins/categories";
    }

    @GetMapping("/products")
    public String searchProducts(
            @RequestParam(required = false) List<String> publicCategoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer limit,
            @Valid ProductParamDto productParamDto,
            Model model) {
        log.debug("Received GET request to search products with parameters");
        var products = productService.searchProductsWithParameters(publicCategoryIds, minPrice, maxPrice,
                                                                   productMapper.toProductParamDetails(
                                                                           productParamDto), page, limit);
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/products/{publicProductId}")
    public String editProduct(@PathVariable String publicProductId, Model model) {
        log.debug(
                "Received GET request to edit product with id - {}",
                publicProductId);
        var product = productService.getByPublicId(publicProductId);
        var categories = categoryService.getCategories();

        var productUpdateDto = new ProductUpdateDto(
                product.name(),
                product.price(),
                product.categoryPublicId(),
                product.stockQuantity()
        );
        if (model.containsAttribute("conflictMessage")) {
            model.addAttribute("hasConflict", true);
            model.addAttribute("conflictMessage", model.asMap().get("conflictMessage"));

            var conflictUpdateDto = new ProductUpdateDto(
                    product.name(),
                    (BigDecimal) model.asMap().get("newPrice"),
                    product.categoryPublicId(),
                    (Integer) model.asMap().get("newQuantity")
            );
            model.addAttribute("conflictUpdateDto", conflictUpdateDto);
        }
        model.addAttribute("productUpdateDto", productUpdateDto);
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(@ModelAttribute ProductDto productDto, Model model) {
        var categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        return "admin/add-product";
    }

    @PostMapping("/products")
    public String addProduct(@Valid ProductDto productDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to add product");
        if (bindingResult.hasErrors()) {
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            return "admin/add-product";
        }
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        try {
            productService.createProduct(publicUserId, productMapper.toProductDetails(productDto));
        } catch (DuplicateProductException e) {
            redirectAttributes.addFlashAttribute("conflictMessage",
                                                 String.format(
                                                         "Товар уже существует, название - %s, изменения найдены в стоимости - %s руб., и количестве - %d шт.",
                                                         e.getName(), e.getPrice(),
                                                         e.getStockQuantity()));
            redirectAttributes.addFlashAttribute("newPrice", e.getPrice());
            redirectAttributes.addFlashAttribute("newQuantity", e.getStockQuantity());
            redirectAttributes.addFlashAttribute("hasConflict", true);
            return "redirect:/api/admins/products/" + e.getPublicProductId();
        } catch (AlreadyExistException e) {
            model.addAttribute("errorMessage", "Продукт уже существует с такими же параметрами");
            return "admin/add-product";
        }
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УСПЕШНО ДОБАВЛЕН");

        return "redirect:/api/admins/products";
    }

    @PatchMapping("/products/{publicProductId}")
    public String updateProduct(@PathVariable String publicProductId,
                                @Valid ProductUpdateDto productUpdateDto, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received PATCH request to update product with id = {}", publicProductId);

        if (bindingResult.hasErrors()) {
            var product = productService.getByPublicId(publicProductId);
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("product", product);
            return "admin/edit-product";
        }
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        productService.updateProduct(publicUserId, productMapper.toProductUpdateDetails(productUpdateDto),
                                     publicProductId);
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УСПЕШНО ОТРЕДАКТИРОВАН");
        return "redirect:/api/admins/products";
    }

    @DeleteMapping("/products/{publicProductId}")
    public String deleteProduct(@PathVariable String publicProductId,
                                RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete product with id = {}", publicProductId);
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        productService.deleteProduct(publicUserId, publicProductId);
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УСПЕШНО УДАЛЕН");

        return "redirect:/api/admins/products";
    }

    @PatchMapping("/orders/{publicOrderId}")
    public String changeOrderStatus(@PathVariable String publicOrderId,
                                    @RequestParam OrderStatusName orderStatusName,
                                    @RequestParam(required = false) PaymentStatusName paymentStatusName, Model model,
                                    RedirectAttributes redirectAttributes) {
        log.debug(
                "Received PATCH request to change order status, with id - {}, orderStatusName - {}, paymentStatusName -{}",
                publicOrderId, orderStatusName, paymentStatusName);
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        try {
            orderService.changeOrderStatus(publicUserId, publicOrderId, orderStatusName, paymentStatusName);
        } catch (BadRequestException e) {
            if (e.getMessage().equals("The DELIVERED status can only be changed to CANCELLED")) {
                model.addAttribute("errorMessage", "Статус ДОСТАВЛЕН можно поменять только на статус ОТМЕНЕН");
            }
            if (e.getMessage().equals("The CANCELLED status can not be changed")) {
                model.addAttribute("errorMessage", "Статус ОТМЕНЕН не может быть изменен");
            }
            var order = orderService.getOrderById(publicOrderId, publicUserId);

            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", OrderStatusName.values());
            model.addAttribute("paymentStatuses", PaymentStatusName.values());

            return "admin/edit-order";
        }
        redirectAttributes.addFlashAttribute("successMessage", "ЗАКАЗ УСПЕШНО ОТРЕДАКТИРОВАН");
        return "redirect:/api/admins/orders";
    }

    @GetMapping("/orders")
    public String getOrders(Model model,
                            @RequestParam(defaultValue = "0") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("Received GET request to get all user orders");
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        var orders = orderService.getAllOrders(publicUserId, page, limit);
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/orders/{publicOrderId}/edit")
    public String editOrder(@PathVariable String publicOrderId,
                            Model model) {
        log.debug("Received GET request to edit order: {}", publicOrderId);
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        var order = orderService.getOrderById(publicOrderId, publicUserId);

        model.addAttribute("order", order);
        model.addAttribute("orderStatuses", OrderStatusName.values());
        model.addAttribute("paymentStatuses", PaymentStatusName.values());
        return "admin/edit-order";
    }

    @GetMapping("/statistics")
    public String getStatistic() {
        log.debug("Received GET request to get statistic");
        return "admin/statistics";
    }

    @GetMapping("/statistics/top-products")
    public String getProductStatistic(@RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "5") Integer limit, Model model) {
        log.debug("Received GET request to get product statistic");
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        var topProducts = statisticService.getTopProducts(publicUserId, PageRequest.of(page, limit));
        model.addAttribute("topProducts", topProducts);
        return "admin/top-products";
    }

    @GetMapping("/statistics/top-users")
    public String getTopUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        var zone = ZoneId.of("Europe/Moscow");
        var currentUtcTime = OffsetDateTime.now(zone);
        if (dateFrom == null) {
            dateFrom = currentUtcTime.minusDays(7).toLocalDate();
        }
        if (dateTo == null) {
            dateTo = currentUtcTime.toLocalDate();
        }
        var startDateTime = dateFrom.atStartOfDay(zone).toOffsetDateTime();
        var endDateTime = dateTo.atTime(currentUtcTime.toOffsetTime());
        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        var topUsers = statisticService.getTopUsersByOrders(publicUserId, PageRequest.of(page, limit), startDateTime,
                                                            endDateTime);
        model.addAttribute("topUsers", topUsers);
        return "admin/top-users";
    }

    @GetMapping("/statistics/top-revenue")
    public String getTopRevenue(@RequestParam(defaultValue = "0") Integer page,
                                @RequestParam(defaultValue = "10") Integer limit,
                                @RequestParam(required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                @RequestParam(required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                Model model) {

        var zone = ZoneId.of("Europe/Moscow");
        var currentUtcTime = OffsetDateTime.now(zone);
        if (dateFrom == null) {
            dateFrom = currentUtcTime.minusDays(7).toLocalDate();
        }
        if (dateTo == null) {
            dateTo = currentUtcTime.toLocalDate();
        }
        var startDateTime = dateFrom.atStartOfDay(zone).toOffsetDateTime();
        var endDateTime = dateTo.atTime(currentUtcTime.toOffsetTime());

        var publicUserId = getPublicAdminIdOrThrowException(sessionUserDto);
        var topRevenue =
                statisticService.getStatistic(publicUserId, PageRequest.of(page, limit), startDateTime, endDateTime);
        var totalRevenue = statisticService.getTotalRevenue(topRevenue);

        model.addAttribute("topRevenue", topRevenue);
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin/top-revenue";
    }

    private String getPublicAdminIdOrThrowException(UserDto userDto) {
        return userDto.getPublicUserId()
                .orElseThrow(() -> new UserNotAuthenticatedException("Что-то пошло не так. Попробуйте еще раз!"));
    }
}