package ru.kappers.logic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kappers.model.Role;
import ru.kappers.model.User;
import ru.kappers.model.dto.RoleDto;
import ru.kappers.service.MessageTranslator;
import ru.kappers.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MessageTranslator messageTranslator;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, MessageTranslator messageTranslator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.messageTranslator = messageTranslator;
    }

    protected String getCurrentFirstRole() {
        return getCurrentAuthentication().getAuthorities().iterator().next().getAuthority();
    }

    protected Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Кастомная авторизация.
     * @param user пользователь
     * @return {@literal true}, если пользователь найден в БД и пароль совпадает, иначе - {@literal false}
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean login(@RequestBody User user) {
        checkNotNull(user, "user is required");
        getCurrentAuthentication();
        String password = user.getPassword();

        user = userService.getByUserName(user.getUserName());

        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    private <T> void checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    @ResponseBody
    @RequestMapping("/user")
    public Principal user(HttpServletRequest request) {
        checkNotNull(request, "request is required");
        String authToken = request.getHeader("Authorization")
                .substring("Basic".length()).trim();
        return () ->  new String(Base64.getDecoder()
                .decode(authToken)).split(":")[0];
    }

    @RequestMapping(value = "/sign-in/get-authority", method = RequestMethod.GET)
    @ResponseBody
    public RoleDto getCurrentUserRole() {
        return new RoleDto(getCurrentFirstRole());
    }

    @RequestMapping(value = "/sign-in/logout-success", method = RequestMethod.GET)
    @ResponseBody
    public RoleDto logoutSuccess() {
        return new RoleDto(getCurrentFirstRole());
    }

    @RequestMapping(value = "/sign-in/perform-logout", method = RequestMethod.GET)
    @ResponseBody
    public RoleDto logout() {
        return new RoleDto(Role.Names.ANONYMOUS);
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User create(@RequestBody User user) {
        checkNotNull(user, "user is required");
        user.setDateOfRegistration(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.addUser(user);
    }

    @ResponseBody
    @RequestMapping(value = "/rest/user/get-current-authorized", method = RequestMethod.GET)
    public User getCurrentAuthenticatedUser() {
        String username = getCurrentAuthentication().getName();
        log.info("username = {}", username);
        User user = userService.getByUserName(username);
        if (user == null) {
            throw new IllegalStateException(messageTranslator.byCode("user.authenticatedNotFound"));
        }
        return user;
    }
}
