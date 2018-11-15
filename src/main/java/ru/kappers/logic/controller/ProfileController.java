package ru.kappers.logic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kappers.model.User;
import ru.kappers.service.RolesService;
import ru.kappers.service.UserService;
import ru.kappers.util.DateUtil;

@Slf4j
@Controller
@RequestMapping(value = "/rest/profile")
public class ProfileController {

    private RolesService rolesService;

    private UserService userService;

    @Autowired
    public void setRolesService(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Пример REST взаимодействия
     *
     * @param id идентификатор пользователя
     * @return пользователь
     */
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserInfoExampleRest(@PathVariable Long id) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getById(id.intValue());
        if (user == null) {
            // return userRepository.findById(id);
            user = User.builder()
                    .currency("USD")
                    .lang("RUSSIAN")
                    .name("Вася")
                    .password("vasya96")
                    .role(rolesService.getById(1))
                    .userName("vasya")
                    .email("vasya@gmail.com")
                    .dateOfBirth(DateUtil.convertDate("19850429"))
                    .dateOfRegistration(DateUtil.getCurrentTime())
                    .isblocked(false)
                    .id(id.intValue())
                    .build();
        }
        return user;
    }
}
