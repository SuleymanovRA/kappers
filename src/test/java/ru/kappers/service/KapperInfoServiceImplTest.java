package ru.kappers.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kappers.KappersApplication;
import ru.kappers.exceptions.UserNotHaveKapperRoleException;
import ru.kappers.model.KapperInfo;
import ru.kappers.model.Role;
import ru.kappers.model.User;
import ru.kappers.util.DateTimeUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

@Slf4j
@ActiveProfiles("test")
@ContextConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KappersApplication.class})
@TestExecutionListeners({DbUnitTestExecutionListener.class})
public class KapperInfoServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    private User user = User.builder()
            .userName("user1")
            .name("юзер")
            .password("assaasas")
            .dateOfBirth(DateTimeUtil.parseLocalDateTimeFromStartOfDate("1965-08-06+03:00"))
            .dateOfRegistration(LocalDateTime.of(LocalDate.parse("2019-01-20"), LocalTime.MIDNIGHT))
            .lang("RUSSIAN")
            .balance(Money.of(CurrencyUnit.EUR, new BigDecimal("10.00")))
            .build();
    private User kapper = User.builder()
            .userName("kapper1")
            .name("каппер")
            .password("assaasas")
            .dateOfBirth(DateTimeUtil.parseLocalDateTimeFromStartOfDate("1965-08-06+03:00"))
            .dateOfRegistration(LocalDateTime.of(LocalDate.parse("2019-01-20"), LocalTime.MIDNIGHT))
            .lang("RUSSIAN")
            .balance(Money.of(CurrencyUnit.USD, new BigDecimal("100.00")))
            .build();


    @Autowired
    private KapperInfoService kapperInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private RolesService rolesService;

    @Before
    public void setUp() {
        deleteFromTables("users");
        kapper.setRole(rolesService.getByName(Role.Names.KAPPER));
    }

    @Test
    public void initKapper() {
        userService.addUser(kapper);
        User kapper1 = userService.getByUserName(kapper.getUserName());
        assertNotNull(kapper1);
        KapperInfo kapperInfo = kapper1.getKapperInfo();
        assertNotNull(kapperInfo);
        assertEquals((int)kapperInfo.getTokens(), 500);
        assertEquals((int)kapperInfo.getBets(), 0);
        assertEquals((int)kapperInfo.getBlockedTokens(), 0);
        assertEquals((int)kapperInfo.getSuccessBets(), 0);
        assertEquals(kapperInfo.getUser(), kapper1);
    }

    @Test(expected = UserNotHaveKapperRoleException.class)
    public void initKapperMustThrowExceptionForUserWithoutKapperRole() {
        userService.addUser(user);
        User user1 = userService.getByUserName(user.getUserName());
        assertNotNull(user1);
        kapperInfoService.initKapper(user1);
    }

    @Test
    public void delete() {
        userService.addUser(kapper);
        User kapper1 = userService.getByUserName(kapper.getUserName());
        assertNotNull(kapper1);
        KapperInfo kapperInfo = kapper1.getKapperInfo();
        assertNotNull(kapperInfo);
        kapperInfoService.delete(kapper1);
        KapperInfo byUser = kapperInfoService.getByUser(kapper1);
        assertNull(byUser);
        kapper1 = userService.getByUserName(kapper.getUserName());
        assertNull(kapper1);
    }

    @Test
    public void getByUser() {
        kapper = userService.addUser(kapper);
        user = userService.addUser(user);

        assertNotNull(userService.getByUserName(kapper.getUserName()));
        assertNotNull(kapperInfoService.getByUser(kapper));
        assertNull(kapperInfoService.getByUser(user));
    }

    @Test
    public void editKapper() {
        kapper = userService.addUser(kapper);
        KapperInfo kapperInfo = kapperInfoService.getByUser(kapper);
        assertEquals((int)kapperInfo.getTokens(), 500);
        kapperInfo.setBlockedTokens(25);
        kapperInfo.setTokens(kapperInfo.getTokens()-25);
        kapperInfo = kapperInfoService.editKapper(kapperInfo);
        assertEquals((int)kapperInfo.getTokens(), 475);
        assertEquals((int)kapperInfo.getBlockedTokens(), 25);
    }
}