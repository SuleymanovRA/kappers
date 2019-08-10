package ru.kappers.logic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kappers.model.User;
import ru.kappers.service.RolesService;
import ru.kappers.service.UserService;

@Slf4j
@RestController
@RequestMapping(value = "/rest/profile")
public class ProfileController {

    private RolesService rolesService;
    private UserService userService;

    @Autowired
    public void setRolesService(RolesService rolesService) {
        this.rolesService = rolesService;
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

        // return userRepository.findById(id);

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("user = " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        log.info("user = " + SecurityContextHolder.getContext().getAuthentication());
        log.info("getName = " + SecurityContextHolder.getContext().getAuthentication().getName());

        return userService.getByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
