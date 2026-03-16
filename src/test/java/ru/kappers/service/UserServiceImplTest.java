package ru.kappers.service;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.kappers.AbstractIntegrationTest;
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
import static org.junit.Assert.*;
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
        assertWithRecursiveComparison(adminUser, admin);
        assertWithRecursiveComparison(user, this.user);
        assertWithRecursiveComparison(kapperUser, kapper);
        assertThat(user.getKapperInfo()).isNull();
        assertThat(kapperUser.getKapperInfo()).isNotNull();
    }

    private void assertWithRecursiveComparison(User actualUser, User expectedUser) {
        assertThat(actualUser)
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
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
        assertThat(kapperUser).isNotNull();
        assertThat(kapperUser.getUserName()).isEqualTo(kapper.getUserName());
        assertThat(kapperUser).isNotEqualTo(user);
        assertNotEquals(kapperUser, admin);
        assertThat(kapperUser).isNotEqualTo(admin);
    }

    @Test
    public void getByName() {
        userService.addUser(admin);
        User user1 = userService.getByName("админ");
        assertNotNull(user1);
        assertNotEquals(user1, kapper);
        assertNotEquals(user1, user);
        assertEquals(user1.getUserName(), admin.getUserName());
        userService.delete(admin);
    }

    @Test
    public void editUser() {
        final String adminUserName = "admin";
        User user1 = userService.getByUserName(adminUserName);
        String curr = user1.getEmail();
        assertNotNull(user1);
        user1.setEmail("qwe1as@asas.ru");
        userService.editUser(user1);
        user1 = userService.getByUserName(adminUserName);
        assertNotEquals(curr, user1.getEmail());
    }

    @Test
    public void getAll() {
        List<String> all = userService.getAll().stream()
                .map(User::getUserName)
                .toList();
        assertTrue(all.contains(admin.getUserName()));
        assertTrue(all.contains(user.getUserName()));
        assertTrue(all.contains(kapper.getUserName()));
    }

    @Test
    public void getAllByRole() {
        List<User> admins = userService.getAllByRole(Role.Names.ADMIN);
        assertNotNull(admins);
        assertNotEquals(0, admins.size());
        assertTrue(admins.stream()
                .map(User::getUserName)
                .toList()
                .contains(admin.getUserName()));
    }

    @Test
    public void hasRole() {
        User userU = userService.getByUserName(user.getUserName());
        User userK = userService.getByUserName(kapper.getUserName());
        assertTrue(userService.hasRole(userK, Role.Names.KAPPER));
        assertFalse(userService.hasRole(userU, Role.Names.KAPPER));
        assertFalse(userService.hasRole(userK, Role.Names.ADMIN));
        assertTrue(userService.hasRole(userU, rolesService.getRoleIdByName(Role.Names.USER)));
        assertTrue(userService.hasRole(userU, rolesService.getByName(Role.Names.USER)));
        assertTrue(userService.hasRole(userU, rolesService.getById(2)));
        assertEquals(userK.getRole(), rolesService.getByName(Role.Names.KAPPER));
    }

    @Test
    public void getRole() {
        User userK = userService.getByUserName(kapper.getUserName());
        assertNotNull(userK);
        Role role = userService.getRole(userK);
        assertNotNull(role);
        assertEquals(role.getName(), Role.Names.KAPPER);
    }

    @Test
    public void getHistory() {
        //TODO реализовать тест
    }

    @Test
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
    public void getInfo() {
        //TODO реализовать тест
    }


    @Test
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