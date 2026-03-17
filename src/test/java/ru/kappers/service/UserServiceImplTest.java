package ru.kappers.service;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.RecursiveComparisonAssert;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.kappers.AbstractIntegrationTest;
import ru.kappers.assertion.Assertions;
import ru.kappers.model.KapperInfo;
import ru.kappers.model.Role;
import ru.kappers.model.User;
import ru.kappers.repository.UsersRepository;
import ru.kappers.util.DateTimeUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

@Slf4j
@DatabaseSetup("/data/UserServiceImplTest-users.xml")
public class UserServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final User admin = User.builder()
            .userName("admin")
            .name("админ")
            .password("asasdgfas")
            .dateOfBirth(DateTimeUtil.parseLocalDateTimeFromStartOfDate("1965-08-06+03:00"))
            .dateOfRegistration(LocalDateTime.of(LocalDate.parse("2019-01-20"), LocalTime.MIDNIGHT))
            .lang("RUSSIAN")
            .balance(Money.of(CurrencyUnit.EUR, new BigDecimal("10.00")))
            .build();
    private final User user = User.builder()
            .userName("user")
            .name("юзер")
            .password("assaasas")
            .dateOfBirth(DateTimeUtil.parseLocalDateTimeFromStartOfDate("1965-08-06+03:00"))
            .dateOfRegistration(LocalDateTime.of(LocalDate.parse("2019-01-22"), LocalTime.MIDNIGHT))
            .lang("RUSSIAN")
            .balance(Money.of(CurrencyUnit.USD, new BigDecimal("10.00")))
            .build();
    private final User kapper = User.builder()
            .userName("kapper")
            .name("каппер")
            .password("assaasas")
            .dateOfBirth(DateTimeUtil.parseLocalDateTimeFromStartOfDate("1965-08-06+03:00"))
            .dateOfRegistration(LocalDateTime.of(LocalDate.parse("2019-01-21"), LocalTime.MIDNIGHT))
            .lang("RUSSIAN")
            .balance(Money.of(CurrencyUnit.of("RUB"), new BigDecimal("100.00")))
            .build();

    private final Map<User, String> userRoleNameMap = ImmutableMap.<User, String>builder()
            .put(admin, Role.Names.ADMIN)
            .put(user, Role.Names.USER)
            .put(kapper, Role.Names.KAPPER)
            .build();

    @Before
    public void setUp() {
        updateRolesIfEmpty();
    }

    private void updateRolesIfEmpty() {
        userRoleNameMap.entrySet().stream()
                .filter(it -> it.getKey().getRole() == null)
                .forEach(it -> it.getKey().setRole(rolesService.getByName(it.getValue())));
    }


    @Test
    public void addUsers() {
        deleteFromTables(jdbcTemplate,"users");
        User adminUser = userService.addUser(admin);
        User user = userService.addUser(this.user);
        User kapperUser = userService.addUser(kapper);
        assertWithRecursiveComparison(adminUser)
                .isEqualTo(admin);
        assertWithRecursiveComparison(user)
                .isEqualTo(this.user);
        assertWithRecursiveComparison(kapperUser)
                .isEqualTo(kapper);
        assertThat(user.getKapperInfo()).isNull();
        assertThat(kapperUser.getKapperInfo()).isNotNull();
    }

    private RecursiveComparisonAssert<?> assertWithRecursiveComparison(User actualUser) {
        return assertThat(actualUser).usingRecursiveComparison();
    }

    @Test
    public void deleteUser() {
        final String userName = user.getUserName();
        userService.delete(usersRepository.getByUserName(userName));
        assertThat(usersRepository.getByUserName(userName)).isNull();
    }

    @Test
    public void getByUserName() {
        User kapperUser = userService.getByUserName("kapper");
        log.info(kapperUser.toString());
        Assertions.assertThat(kapperUser).isNotNull()
                .isNotEqualTo(user)
                .isNotEqualTo(admin)
                .hasUserName(kapper.getUserName());
    }

    @Test
    public void getByName() {
        userService.addUser(admin);
        User adminUser = userService.getByName("админ");
        Assertions.assertThat(adminUser).isNotNull()
                .isNotEqualTo(kapper)
                .isNotEqualTo(user)
                .hasUserName(admin.getUserName());
    }

    @Test
    public void editUser() {
        final String adminUserName = "admin";
        User adminUser = userService.getByUserName(adminUserName);
        String email = adminUser.getEmail();
        adminUser.setEmail("qwe1as@asas.ru");
        userService.editUser(adminUser);
        adminUser = userService.getByUserName(adminUserName);
        Assertions.assertThat(adminUser)
                .hasNoEmail(email);
    }

    @Test
    public void getAll() {
        List<String> all = userService.getAll().stream()
                .map(User::getUserName)
                .toList();
        assertThat(all)
                .contains(admin.getUserName(), user.getUserName(), kapper.getUserName());
    }

    @Test
    public void getAllByRole() {
        List<User> admins = userService.getAllByRole(Role.Names.ADMIN);
        assertThat(admins).isNotNull()
                .isNotEmpty()
                .flatExtracting(User::getUserName)
                .contains(admin.getUserName());
    }

    @Test
    public void hasRole() {
        User uUser = userService.getByUserName(user.getUserName());
        User kapperUser = userService.getByUserName(kapper.getUserName());
        assertThat(userService.hasRole(kapperUser, Role.Names.KAPPER)).isTrue();
        assertThat(userService.hasRole(uUser, Role.Names.KAPPER)).isFalse();
        assertThat(userService.hasRole(kapperUser, Role.Names.ADMIN)).isFalse();
        assertThat(userService.hasRole(uUser, rolesService.getRoleIdByName(Role.Names.USER))).isTrue();
        assertThat(userService.hasRole(uUser, rolesService.getByName(Role.Names.USER))).isTrue();
        assertThat(userService.hasRole(uUser, rolesService.getById(2))).isTrue();
        Assertions.assertThat(kapperUser)
                .hasRole(rolesService.getByName(Role.Names.KAPPER));
    }

    @Test
    public void getRole() {
        User kapperUser = userService.getByUserName(kapper.getUserName());
        Assertions.assertThat(kapperUser).isNotNull();
        Role role = userService.getRole(kapperUser);
        Assertions.assertThat(role).isNotNull()
                .hasName(Role.Names.KAPPER);
    }

    @Test
    @Ignore("реализовать тест")
    public void getHistory() {
        //TODO реализовать тест
    }

    @Test
    @Ignore("реализовать тест")
    public void getStat() {
        //TODO реализовать тест
    }

    @Test
    public void getKapperInfo() {
        User kapperUser = userService.getByUserName("kapper");
        kapperUser = userService.addUser(kapperUser);
        KapperInfo kapperInfo = kapperUser.getKapperInfo();
        assertThat(kapperInfo).isNotNull();

        User user = userService.getByUserName("user");
        KapperInfo userInfo = user.getKapperInfo();
        assertThat(userInfo).isNull();
    }

    @Test
    @Ignore("реализовать тест")
    public void getInfo() {
        //TODO реализовать тест
    }


    @Test
    @Ignore("реализовать тест")
    public void transfer() {
        //TODO реализовать тест
    }

    /*
* Ниже фрагмент кода закомментирован для отключения взаимодействия с блокчейном на период разработки.
* */

//    @Test
//    public void DCoreExample() {
//        final Logger slf4jLog = LoggerFactory.getLogger("SDK_SAMPLE");
///*      from DCore Kotlin SDK
//        final String accountName = "u961279ec8b7ae7bd62f304f7c1c3d345";
//        final String accountPrivateKey = "5Jd7zdvxXYNdUfnEXt5XokrE3zwJSs734yQ36a1YaqioRTGGLtn";
//        final String webSocketUrl = "wss://stagesocket.decentgo.com:8090";
//        final String httpUrl = "https://stagesocket.decentgo.com:8090";
//*/
//        final String accountID = "1.2.371";
//        final String accountName = "dw-kapper";
//        final String accountPrivateKey = "5Kim38ED5obSB9urFWgR4HXbQCbcPwcLUGG1xxaxPgYDBB9N7zX";
//        final String webSocketUrl = "wss://hackathon2.decent.ch:8090";
//        final String httpUrl = "https://hackathon2.decent.ch:8090";
//
////        setup dcore api
//        OkHttpClient client = TrustAllCerts.wrap(new OkHttpClient().newBuilder()).build();
//        DCoreApi api = DCoreSdk.create(client, webSocketUrl, httpUrl, slf4jLog);
//
////        create user credentials
//        Credentials credentials = api.getAccountApi()
//                .createCredentials(accountName, accountPrivateKey)
//                .blockingGet();
//
////        balance
//        Pair<Asset, AssetAmount> balance = api.getBalanceApi()
//                .getBalanceWithAsset(credentials.getAccount(), "DCT")
//                .blockingGet();
//        log.info("BALANCE: {}" + balance.getFirst().format(balance.getSecond().getAmount(), 2));
//
//        String uri = "https://8sfaasfghfghfhertsdfsdffsha34uuuuuu.com";
//        String synopsis = "{\"content_type_id\": \"0\", \"title\": \"Hello world\", \"description\": \"\"}";
//        String hashCode = "5432106789543210678954321067890123456781"; // must 40 chars
//        LocalDateTime experation = LocalDateTime.parse("2018-12-08T13:31:15");
//        ContentSubmitOperation contentSubmitOperation = new ContentSubmitOperation(1, ChainObject.parse(accountID),
//                new ArrayList<>(), uri, 0, Arrays.asList(new RegionalPrice(new AssetAmount(0), Regions.ALL.getId())),
//                hashCode, new ArrayList<>(), new ArrayList<>(),
//                experation, new AssetAmount(0), synopsis, null, BaseOperation.FEE_UNSET
//                );
//        log.info("submit operation :" + contentSubmitOperation);
//        TransactionConfirmation confirmation = api.getBroadcastApi().broadcastWithCallback(accountPrivateKey, contentSubmitOperation)
//                .blockingGet();
//        log.info("TRANSACTION: " + confirmation);
//
//
////        transfer
///*
//        AssetAmount amount = balance.getFirst().amount(0.12345);
//        confirmation = api.getOperationsHelper()
//                .transfer(credentials, "u3a7b78084e7d3956442d5a4d439dad51", amount, "hello memo")
//                .blockingGet();
//        log.info("TRANSACTION: " + confirmation);
//*/
//
////        verify
////        ProcessedTransaction trx = api.getTransactionApi().getTransaction(confirmation)
////                .blockingGet();
////        log.info("TRANSACTION EXIST: " + trx);
//
////        history
//        List<OperationHistory> history = api.getHistoryApi().getAccountHistory(credentials.getAccount())
//                .blockingGet();
//        log.info("HISTORY: {}" + history);
//    }
}