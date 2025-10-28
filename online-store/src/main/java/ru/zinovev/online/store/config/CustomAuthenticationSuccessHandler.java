package ru.zinovev.online.store.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.zinovev.online.store.controller.dto.UserDto;
import ru.zinovev.online.store.service.CartService;
import ru.zinovev.online.store.service.UserService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final CartService cartService;
    private final UserDto sessionUserDto;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {

            var email = authentication.getName();
            var user = userService.findUserDetailsByEmail(email);

            sessionUserDto.setPublicUserId(user.publicUserId());
            sessionUserDto.setEmail(user.email());
            sessionUserDto.setName(user.name());
            sessionUserDto.setIsAuthenticated(true);

            String redirectUrl = "/api/users/products";
            boolean isAdmin = false;

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    isAdmin = true;
                    redirectUrl = "/api/admins/home";
                    break;
                }
            }

            if (!isAdmin) {
                var cookieCartId = "";
                if (request.getCookies() != null) {
                    cookieCartId = Arrays.stream(request.getCookies())
                            .filter(cookie -> cookie.getName().equals("CART_ID"))
                            .findFirst()
                            .map(Cookie::getValue)
                            .orElse(null);
                }
                if (cookieCartId != null && !cookieCartId.isEmpty()) {
                    cartService.updateCartWithRegisteredUser(user.publicUserId(), cookieCartId);
                }
            } else {
                Cookie cartCookie = new Cookie("CART_ID", null);
                cartCookie.setPath("/");
                cartCookie.setMaxAge(0);
                response.addCookie(cartCookie);
            }
            response.sendRedirect(redirectUrl);
        }
    }
}
