package ru.kappers.service;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.kappers.AbstractDatabaseTest;
import ru.kappers.assertion.Assertions;
import ru.kappers.config.KappersProperties;
import ru.kappers.exceptions.UserNotHaveKapperRoleException;
import ru.kappers.model.KapperInfo;
import ru.kappers.model.Role;
import ru.kappers.model.User;
import ru.kappers.repository.*;
import ru.kappers.service.impl.*;
import ru.kappers.service.parser.CBRFDailyCurrencyRatesParser;
import ru.kappers.util.DateTimeUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

@ContextConfiguration(classes = {KapperInfoServiceImplTest.Configuration.class})
@ExtendWith(MockitoExtension.class)
public class KapperInfoServiceImplTest extends AbstractDatabaseTest {
    private User user = defaultUserBuilder()
            .build();

    private User.UserBuilder defaultUserBuilder() {
        return User.builder()
                .userName("user1")
                .name("юзер")
                .password("assaasas")
                .dateOfBirth(DateTimeUtil.parseLocalDateTimeFromStartOfDate("1965-08-06+03:00"))
                .dateOfRegistration(LocalDateTime.of(LocalDate.parse("2019-01-20"), LocalTime.MIDNIGHT))
                .lang("RUSSIAN")
                .balance(Money.of(CurrencyUnit.EUR, new BigDecimal("10.00")));
    }

    private User kapper = defaultUserBuilder()
            .userName("kapper1")
            .name("каппер")
            .balance(Money.of(CurrencyUnit.USD, new BigDecimal("100.00")))
            .build();

    @Autowired
    private KapperInfoService kapperInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        deleteFromTables(jdbcTemplate, "users");
        updateKapperRole();
    }

    private void updateKapperRole() {
        kapper.setRole(rolesService.getByName(Role.Names.KAPPER));
    }

    @Test
    public void initKapper() {
        userService.addUser(kapper);
        User kapperUser = userService.getByUserName(kapper.getUserName());
        Assertions.assertThat(kapperUser).isNotNull()
                .kapperInfoAssert()
                .isNotNull()
                .hasTokens(500)
                .hasBets(0)
                .hasBlockedTokens(0)
                .hasSuccessBets(0)
                .hasUser(kapperUser);
    }

    @Test(expected = UserNotHaveKapperRoleException.class)
    public void initKapperMustThrowExceptionForUserWithoutKapperRole() {
        userService.addUser(user);
        User user1 = userService.getByUserName(user.getUserName());
        Assertions.assertThat(user1).isNotNull();
        kapperInfoService.initKapper(user1);
    }

    @Test
    public void delete() {
        userService.addUser(kapper);
        User kapperUser = userService.getByUserName(kapper.getUserName());
        Assertions.assertThat(kapperUser).isNotNull()
                .kapperInfoAssert()
                .isNotNull();
        kapperInfoService.delete(kapperUser);
        KapperInfo kapperInfo = kapperInfoService.getByUser(kapperUser);
        Assertions.assertThat(kapperInfo).isNull();
        kapperUser = userService.getByUserName(kapper.getUserName());
        Assertions.assertThat(kapperUser).isNull();
    }

    @Test
    public void getByUser() {
        kapper = userService.addUser(kapper);
        user = userService.addUser(user);

        Assertions.assertThat(userService.getByUserName(kapper.getUserName())).isNotNull();
        Assertions.assertThat(kapperInfoService.getByUser(kapper)).isNotNull();
        Assertions.assertThat(kapperInfoService.getByUser(user)).isNull();
    }

    @Test
    public void editKapper() {
        kapper = userService.addUser(kapper);
        KapperInfo kapperInfo = kapperInfoService.getByUser(kapper);
        Assertions.assertThat(kapperInfo).hasTokens(500);
        kapperInfo = kapperInfoService.editKapper(kapperInfo.toBuilder()
                .blockedTokens(25)
                .tokens(kapperInfo.getTokens() - 25)
                .build());
        Assertions.assertThat(kapperInfo)
                .hasTokens(475)
                .hasBlockedTokens(25);
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public MessageTranslator messageTranslator() {
            return Mockito.mock(MessageTranslator.class);
        }

        @Bean
        public KapperInfoService kapperInfoService(KapperInfoRepository kapperInfoRepository,
                                                   MessageTranslator messageTranslator) {
            return new KapperInfoServiceImpl(kapperInfoRepository, messageTranslator);
        }

        @Bean
        public RolesService rolesService(RolesRepository repository) {
            return new RolesServiceImpl(repository);
        }

        @Bean
        public CurrencyRateService currencyRateService(CurrRateRepository currRateRepository,
                                                       KappersProperties kappersProperties) {
            return new CurrencyRateServiceImpl(currRateRepository, kappersProperties);
        }

        @Bean
        public CBRFDailyCurrencyRatesParser cbrfDailyCurrencyRatesParser() {
            return Mockito.mock(CBRFDailyCurrencyRatesParser.class);
        }

        @Bean
        public CurrencyService currencyService(CurrencyRateService currencyRateService,
                CBRFDailyCurrencyRatesParser cbrfDailyCurrencyRatesParser, KappersProperties kappersProperties,
                MessageTranslator messageTranslator) {
            return new CurrencyServiceImpl(currencyRateService, cbrfDailyCurrencyRatesParser, kappersProperties,
                    messageTranslator);
        }

        @Bean
        public HistoryService historyService(HistoryRepository historyRepository) {
            return new HistoryServiceImpl(historyRepository);
        }

        @Bean
        public UserService userService(UsersRepository usersRepository, RolesService rolesService,
                                       KapperInfoService kapperInfoService, CurrencyService currencyService,
                                       MessageTranslator messageTranslator, HistoryService historyService) {
            return new UserServiceImpl(usersRepository, rolesService, kapperInfoService, currencyService,
                    messageTranslator, historyService);
        }
    }
}