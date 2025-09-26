package ru.zinovev.online.store.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.mapper.CategoryMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.exception.model.BadRequestException;
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

    @GetMapping("/home")
    public String homePage() {
        return "admin/admin-home";
    }

    @GetMapping("/{publicUserId}/addresses")
    public String getAddresses(@PathVariable String publicUserId,
                               @RequestParam(required = false) AddressTypeName name, Model model,
                               HttpServletRequest request, @ModelAttribute AddressDto addressDto) {
        log.debug("Received GET request to get system addresses");
        var addresses = addressService.getAddresses(publicUserId, name, true);

        model.addAttribute("addressTypes", List.of(AddressTypeName.STORE_ADDRESS, AddressTypeName.PARCEL_LOCKER));
        model.addAttribute("nameParam", request.getParameter("name"));
        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("addresses", addresses);
        return "admin/addresses";
    }

    @PostMapping("/{publicUserId}/addresses")
    public String addSystemAddress(@PathVariable String publicUserId, @Valid AddressDto addressDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to add system address dto - {}, from admin with id - {}",
                  addressDto, publicUserId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("addressTypes", List.of(AddressTypeName.STORE_ADDRESS, AddressTypeName.PARCEL_LOCKER));
            return "admin/addresses";
        }
        addressService.addSystemAddress(publicUserId, addressMapper.toAddressDetails(addressDto), addressDto.name());
        redirectAttributes.addFlashAttribute("successMessage", "АДРЕС УСПЕШНО ДОБАВЛЕН");
        return "redirect:/api/admins/" + publicUserId + "/addresses";
    }

    @GetMapping("/{publicUserId}/addresses/{publicAddressId}")
    public String editSystemAddress(@PathVariable String publicUserId,
                                    @ModelAttribute AddressUpdateDto addressUpdateDto,
                                    @PathVariable String publicAddressId, Model model) {
        log.debug(
                "Received GET request to edit system address id - {}, from user with id - {}",
                publicAddressId,
                publicUserId);
        var address = addressService.getAddressByPublicId(publicAddressId);

        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("address", address);
        return "admin/edit-address";
    }

    @PatchMapping("/{publicUserId}/addresses/{publicAddressId}")
    public String updateSystemAddress(@PathVariable String publicUserId,
                                      @Valid AddressUpdateDto addressUpdateDto, BindingResult bindingResult,
                                      @PathVariable String publicAddressId, Model model,
                                      RedirectAttributes redirectAttributes) {
        log.debug(
                "Received PATCH request to update system address (id - {}), dto - {}, from user with id - {}",
                publicAddressId,
                addressUpdateDto, publicUserId);
        if (bindingResult.hasErrors()) {
            var address = addressService.getAddressByPublicId(publicAddressId);
            model.addAttribute("address", address);
            return "admin/edit-address";
        }
        addressService.updateSystemAddress(publicUserId, publicAddressId,
                                           addressMapper.toAddressUpdateDetails(addressUpdateDto));
        redirectAttributes.addFlashAttribute("successMessage", "УСПЕШНО ОБНОВЛЕНО");
        return "redirect:/api/admins/" + publicUserId + "/addresses";
    }

    @DeleteMapping("/{publicUserId}/addresses/{publicAddressId}")
    public String deleteSystemAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                      RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete system address with id = {}, from user with id - {}",
                  publicAddressId, publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, true);
        redirectAttributes.addFlashAttribute("successMessage", "УСПЕШНО УДАЛЕНО");
        return "redirect:/api/admins/" + publicUserId + "/addresses";
    }

    @GetMapping("/{publicUserId}/categories")
    public String getCategories(@PathVariable String publicUserId, @ModelAttribute CategoryDto categoryDto,
                                Model model) {
        log.debug("Received GET request to get all categories");
        var categories = categoryService.getCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("publicUserId", publicUserId);
        return "admin/categories";
    }

    @GetMapping("/{publicUserId}/categories/{publicCategoryId}")
    public String editCategory(@PathVariable String publicUserId,
                               @ModelAttribute CategoryDto categoryDto,
                               @PathVariable String publicCategoryId, Model model) {
        log.debug(
                "Received GET request to edit category with id - {}, from user with id - {}",
                publicCategoryId,
                publicUserId);

        var category = categoryService.getCategoryByPublicId(publicCategoryId);

        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/{publicUserId}/categories")
    public String addCategory(@PathVariable String publicUserId, @Valid CategoryDto categoryDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to add category");
        if (bindingResult.hasErrors()) {
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            return "admin/categories";
        }
        try {
            categoryService.createCategory(publicUserId, categoryMapper.toCategoryDetails(categoryDto));
        } catch (BadRequestException e) {
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("errorMessage", "КАТЕГОРИЯ " + categoryDto.name() + " УЖЕ СУЩЕСТВУЕТ");
            return "admin/categories";
        }

        redirectAttributes.addFlashAttribute("successMessage", "КАТЕГОРИЯ УСПЕШНО ДОБАВЛЕНА");
        return "redirect:/api/admins/" + publicUserId + "/categories";
    }

    @PatchMapping("/{publicUserId}/categories/{publicCategoryId}")
    public String updateCategory(@PathVariable String publicUserId, @PathVariable String publicCategoryId,
                                 @Valid CategoryDto categoryDto, BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Received PATCH request to update category with id = {}", publicCategoryId);
        if (bindingResult.hasErrors()) {
            var category = categoryService.getCategoryByPublicId(publicCategoryId);
            model.addAttribute("category", category);
            return "admin/edit-category";
        }
        try {
            categoryService.updateCategory(publicUserId, publicCategoryId,
                                           categoryMapper.toCategoryDetails(categoryDto));
        } catch (BadRequestException e) {
            var category = categoryService.getCategoryByPublicId(publicCategoryId);
            model.addAttribute("errorMessage", "КАТЕГОРИЯ " + categoryDto.name() + " УЖЕ СУЩЕСТВУЕТ");
            model.addAttribute("category", category);
            return "admin/edit-category";
        }

        redirectAttributes.addFlashAttribute("successMessage", "КАТЕГОРИЯ УСПЕШНО ОБНОВЛЕНА");
        return "redirect:/api/admins/" + publicUserId + "/categories";
    }

    @DeleteMapping("/{publicUserId}/categories/{publicCategoryId}")
    public String deleteCategory(@PathVariable String publicUserId, @PathVariable String publicCategoryId,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete category with id = {}", publicCategoryId);
        categoryService.deleteCategory(publicUserId, publicCategoryId);
        redirectAttributes.addFlashAttribute("successMessage", "КАТЕГОРИЯ УСПЕШНО УДАЛЕНА");
        return "redirect:/api/admins/" + publicUserId + "/categories";
    }

    @GetMapping("/{publicUserId}/products")
    public String searchProducts(@PathVariable String publicUserId,
                                 @RequestParam(required = false) List<String> publicCategoryIds,
                                 @RequestParam(required = false) BigDecimal minPrice,
                                 @RequestParam(required = false) BigDecimal maxPrice,
                                 @RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "5") Integer limit,
                                 @Valid ProductParamDto productParamDto,
                                 Model model, @ModelAttribute
                                 ProductDto productDto) {
        log.debug("Received GET request to search products with parameters");
        var products = productService.searchProductsWithParameters(publicCategoryIds, minPrice, maxPrice,
                                                                   productMapper.toProductParamDetails(
                                                                           productParamDto), page, limit);
        var categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("products", products);

        return "admin/products";
    }

    @GetMapping("/{publicUserId}/products/{publicProductId}")
    public String editProduct(@PathVariable String publicUserId,
                              @ModelAttribute ProductUpdateDto productUpdateDto,
                              @PathVariable String publicProductId, Model model) {
        log.debug(
                "Received GET request to edit product with id - {}, from user with id - {}",
                publicProductId,
                publicUserId);

        var product = productService.getByPublicId(publicProductId);
        var categories = categoryService.getCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    @PostMapping("/{publicUserId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public String addProduct(@PathVariable String publicUserId, @Valid ProductDto productDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to add product");
        if (bindingResult.hasErrors()) {
            var categories = categoryService.getCategories();
            model.addAttribute("categories", categories);
            return "admin/products";
        }
        productService.createProduct(publicUserId, productMapper.toProductDetails(productDto));
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УСПЕШНО ДОБАВЛЕН");

        return "redirect:/api/admins/" + publicUserId + "/products";
    }

    @PatchMapping("/{publicUserId}/products/{publicProductId}")
    public String updateProduct(@PathVariable String publicUserId, @PathVariable String publicProductId,
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
        var categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        productService.updateProduct(publicUserId, productMapper.toProductUpdateDetails(productUpdateDto),
                                     publicProductId);
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УСПЕШНО ОТРЕДАКТИРОВАН");
        return "redirect:/api/admins/" + publicUserId + "/products";
    }

    @DeleteMapping("/{publicUserId}/products/{publicProductId}")
    public String deleteProduct(@PathVariable String publicUserId, @PathVariable String publicProductId,
                                RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete product with id = {}", publicProductId);
        productService.deleteProduct(publicUserId, publicProductId);
        redirectAttributes.addFlashAttribute("successMessage", "ТОВАР УСПЕШНО УДАЛЕН");

        return "redirect:/api/admins/" + publicUserId + "/products";
    }

    @PatchMapping("/{publicUserId}/orders/{publicOrderId}")
    public String changeOrderStatus(@PathVariable String publicUserId, @PathVariable String publicOrderId,
                                    @RequestParam OrderStatusName orderStatusName,
                                    @RequestParam(required = false) PaymentStatusName paymentStatusName, Model model, RedirectAttributes redirectAttributes) {
        log.debug(
                "Received PATCH request to change order status, with id - {}, orderStatusName - {}, paymentStatusName -{}",
                publicOrderId, orderStatusName, paymentStatusName);
        try {
            orderService.changeOrderStatus(publicUserId, publicOrderId, orderStatusName, paymentStatusName);
            model.addAttribute("publicUserId", publicUserId);

        } catch (BadRequestException e) {
            var order = orderService.getOrderById(publicOrderId, publicUserId);
            model.addAttribute("publicUserId", publicUserId);
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", OrderStatusName.values());
            model.addAttribute("paymentStatuses", PaymentStatusName.values());
            model.addAttribute("errorMessage", "Статус ДОСТАВЛЕН можно поменять только на статус ОТМЕНЕН");
            return "admin/edit-order";
        }
        redirectAttributes.addFlashAttribute("successMessage", "ЗАКАЗ УСПЕШНО ОТРЕДАКТИРОВАН");
        return "redirect:/api/admins/" + publicUserId + "/orders";
    }

    @GetMapping("/{publicUserId}/orders")
    public String getOrders(@PathVariable String publicUserId, Model model,
                            @RequestParam(defaultValue = "0") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("Received GET request to get all user orders");
        var orders = orderService.getAllOrders(publicUserId, page, limit);

        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/{publicUserId}/orders/{publicOrderId}/edit")
    public String editOrder(@PathVariable String publicUserId,
                            @PathVariable String publicOrderId,
                            Model model) {
        log.debug("Received GET request to edit order: {}", publicOrderId);
        var order = orderService.getOrderById(publicOrderId, publicUserId);

        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("order", order);
        model.addAttribute("orderStatuses", OrderStatusName.values());
        model.addAttribute("paymentStatuses", PaymentStatusName.values());
        return "admin/edit-order";
    }

    @GetMapping("/{publicUserId}/statistics")
    public String getStatistic(@PathVariable String publicUserId,
                               Model model) {
        log.debug("Received GET request to get statistic");
        model.addAttribute("publicUserId", publicUserId);
        return "admin/statistics";
    }

    @GetMapping("/{publicUserId}/statistics/top-products")
    public String getProductStatistic(@PathVariable String publicUserId,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "5") Integer limit, Model model) {
        log.debug("Received GET request to get product statistic");
        var topProducts = statisticService.getTopProducts(publicUserId, PageRequest.of(page, limit));
        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("topProducts", topProducts);
        return "admin/top-products";
    }

    @GetMapping("/{publicUserId}/statistics/top-users")
    public String getTopUsers(
            @PathVariable String publicUserId,
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

        var topUsers = statisticService.getTopUsersByOrders(publicUserId, PageRequest.of(page, limit), startDateTime,
                                                            endDateTime);
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("publicUserId", publicUserId);
        return "admin/top-users";
    }

    @GetMapping("/{publicUserId}/statistics/top-revenue")
    public String getTopRevenue(
            @PathVariable String publicUserId,
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

        var topRevenue =
                statisticService.getStatistic(publicUserId, PageRequest.of(page, limit), startDateTime, endDateTime);
        var totalRevenue = statisticService.getTotalRevenue(topRevenue);

        model.addAttribute("topRevenue", topRevenue);
        model.addAttribute("publicUserId", publicUserId);
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin/top-revenue";
    }
}