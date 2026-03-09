package ru.kappers.service.impl;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.exceptions.MoneyTransferException;
import ru.kappers.model.*;
import ru.kappers.repository.UsersRepository;
import ru.kappers.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса пользователя
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UsersRepository repository;
    private final RolesService rolesService;
    private final KapperInfoService kapperInfoService;
    private final CurrencyService currencyService;
    private final MessageTranslator messageTranslator;
    private final HistoryService historyService;

    @Autowired
    public UserServiceImpl(UsersRepository repository, RolesService rolesService, KapperInfoService kapperInfoService,
                           CurrencyService currencyService, MessageTranslator messageTranslator, HistoryService historyService) {
        this.repository = repository;
        this.rolesService = rolesService;
        this.kapperInfoService = kapperInfoService;
        this.currencyService = currencyService;
        this.messageTranslator = messageTranslator;
        this.historyService = historyService;
    }

    @Override
    public User addUser(User user) {
        log.debug("addUser(user: {})...", user);
        Role role = user.getRole();
        if (role != null && role.getName() == null) {
            role.setName(rolesService.getById(role.getId()).getName());
        }
        user.setRole(role);
        final String userName = user.getUserName();
        User userByUserName = repository.getByUserName(userName);
        if (userByUserName != null) {
            log.info("Пользователь {} уже существует", userName);
            saveKapperInfo(userByUserName);
            return userByUserName;
        }
        if (user.getDateOfRegistration() == null) {
            user.setDateOfRegistration(LocalDateTime.now());
        }
        if (user.getRole() == null) {
            user.setRole(rolesService.getByName(Role.Names.USER));
        }
        User savedUser = repository.save(user);
        saveKapperInfo(savedUser);
        return savedUser;
    }

    private User saveKapperInfo(User user) {
        if (user.getRole().equals(rolesService.getByName(Role.Names.KAPPER))) {
            kapperInfoService.initKapper(user);
            return repository.getOne(user.getId());
        }
        return user;
    }

    @Override
    public void deleteByUserName(String userName) {
        log.debug("deleteByUserName(userName: {})...", userName);
        User user = getByUserName(userName);
        if (user.hasRole(Role.Names.KAPPER)) {
            kapperInfoService.delete(user);
        } else {
            repository.deleteByUserName(userName);
        }
    }

    @Override
    public void delete(User user) {
        log.debug("delete(user: {})...", user);
        if (user.hasRole(Role.Names.KAPPER)) {
            kapperInfoService.delete(user);
        } else {
            repository.delete(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUserName(String name) {
        log.debug("getByUserName(name: {})...", name);
        User byUserName = repository.getByUserName(name);
//        byUserName.getEvents().iterator();
        return byUserName;
    }

    @Override
    @Transactional(readOnly = true)
    public User getByName(String name) {
        log.debug("getByName(name: {})...", name);
        return repository.getByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(int id) {
        log.debug("getById(id: {})...", id);
        return repository.findById(id)
                .orElse(null);
    }

    @Override
    public User editUser(User user) {
        log.debug("editUser(user: {})...", user);
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        log.debug("getAll()...");
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllByRole(String roleName) {
        log.debug("getAllByRole(roleName: {})...", roleName);
        Role role = rolesService.getByName(roleName);
        return repository.getAllByRoleId(role.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(User user, String roleName) {
        log.debug("hasRole(user: {}, roleName: {})...", user, roleName);
        return user.hasRole(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(User user, int roleId) {
        log.debug("hasRole(user: {}, roleId: {})...", user, roleId);
        return user.hasRole(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(User user, Role role) {
        log.debug("hasRole(user: {}, role: {})...", user, role);
        return user.hasRole(role.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRole(User user) {
        log.debug("getRole(user: {})...", user);
        return user.getRole();
    }

    @Override
    @Transactional(readOnly = true)
    public List<History> getHistory(User user) {
        log.debug("getHistory(user: {})...", user);
        return historyService.getUsersHistory(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Stat getStat(User user) {
        log.debug("getStat(user: {})...", user);
        //TODO
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public KapperInfo getKapperInfo(User user) {
        log.debug("getKapperInfo(user: {})...", user);
        if (user.hasRole(Role.Names.KAPPER)) {
            return user.getKapperInfo();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalInfo getInfo(User user) {
        log.debug("getInfo(user: {})...", user);
        //TODO
        return null;
    }

    // Нельзя метод transfer делать синхронизированным! Он находится в синглтоне и, если его синхронизировать, то это будет бутылочным горлышком.
    // Вместо синхронизации нужно реализовать сервис таким образом, чтобы он не хранил состояние и метод выполнялся под транзакцией.
    @Override
    public void transfer(User user, User kapper, BigDecimal amount){
        log.debug("transfer(user: {}, kapper: {}, amount: {})...", user, kapper, amount);
        Preconditions.checkArgument(user.hasRole(Role.Names.USER), messageTranslator.byCode("user.hasNoPermissionToTransferMoney"), user.getUserName());
        Preconditions.checkArgument(kapper.hasRole(Role.Names.KAPPER), messageTranslator.byCode("user.canTransferOnlyToKapper"));
        Preconditions.checkArgument(user.getBalance().getAmount().compareTo(amount) >= 0, messageTranslator.byCode("user.doesNotNaveEnoughMoney"),
                user.getUserName(), user.getBalance());
        try {
            user.setBalance(user.getBalance().minus(amount));
            if (user.getBalance().getCurrencyUnit().equals(kapper.getBalance().getCurrencyUnit())) {
                kapper.setBalance(kapper.getBalance().plus(amount));
                log.debug("Kapper {} got {} {}", kapper.getUserName(), amount, kapper.getBalance().getCurrencyUnit());
            } else {
                BigDecimal resultAmount = currencyService.exchange(user.getBalance().getCurrencyUnit(), amount, kapper.getBalance().getCurrencyUnit());
                kapper.setBalance(kapper.getBalance().plus(resultAmount));
                log.debug("Kapper {} got {} {}", kapper.getUserName(), resultAmount, kapper.getBalance().getCurrencyUnit());
            }
            editUser(user);
            editUser(kapper);
            log.info("User {} transfered {} {} to kapper {}", user.getUserName(), amount, user.getBalance().getCurrencyUnit(), kapper.getUserName());
        } catch (Exception e) {
            log.error("Couldn't transfer money from {} to {}", user.getUserName(), kapper.getUserName());
            throw new MoneyTransferException(e);
        }
    }

}
