package ru.kappers.service.impl;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.kappers.exceptions.MoneyTransferException;
import ru.kappers.model.*;
import ru.kappers.repository.UsersRepository;
import ru.kappers.service.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplUnitTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private RolesService rolesService;
    @Mock
    private KapperInfoService kapperInfoService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private MessageTranslator messageTranslator;
    @Mock
    private HistoryService historyService;


    @Test
    public void deleteByUserNameForNotKapper() {
        final String userName = "Test Name";
        final User user = mock(User.class);
        userService = spy(userService);
        doReturn(user).when(userService).getByUserName(userName);

        userService.deleteByUserName(userName);

        verify(userService).getByUserName(userName);
        verify(usersRepository).deleteByUserName(userName);
        verify(kapperInfoService, never()).delete(user);
    }

    @Test
    public void deleteByUserNameForKapper() {
        final String userName = "Test Name2";
        final User user = mock(User.class);
        when(user.hasRole(Role.Names.KAPPER)).thenReturn(true);
        userService = spy(userService);
        doReturn(user).when(userService).getByUserName(userName);

        userService.deleteByUserName(userName);

        verify(userService).getByUserName(userName);
        verify(usersRepository, never()).deleteByUserName(userName);
        verify(kapperInfoService).delete(user);
    }

    @Test
    public void deleteForNotKapper() {
        final User user = mock(User.class);

        userService.delete(user);

        verify(usersRepository).delete(user);
        verify(kapperInfoService, never()).delete(user);
    }

    @Test
    public void deleteForKapper() {
        final User user = mock(User.class);
        when(user.hasRole(Role.Names.KAPPER)).thenReturn(true);

        userService.delete(user);

        verify(usersRepository, never()).delete(user);
        verify(kapperInfoService).delete(user);
    }

    @Test
    public void getByIdMustReturnNullIfUsersRepositoryNotFoundUser() {
        final int id = 1;

        final User result = userService.getById(id);

        assertThat(result, is(nullValue()));
        verify(usersRepository).findById(id);
    }

    @Test
    public void getByIdMustReturnNotNullIfUsersRepositoryFoundUser() {
        final int id = 1;
        final User user = mock(User.class);
        final Optional<User> opUser = Optional.of(user);
        when(usersRepository.findById(id)).thenReturn(opUser);

        final User result = userService.getById(id);

        assertThat(result, is(user));
        verify(usersRepository).findById(id);
    }

    @Test
    public void getHistory() {
        final User user = mock(User.class);
        final List<History> historyList = Arrays.asList(mock(History.class), mock(History.class));
        when(historyService.getUsersHistory(user)).thenReturn(historyList);

        final List<History> result = userService.getHistory(user);

        assertThat(result, is(historyList));
        verify(historyService).getUsersHistory(user);
    }

    @Test
    public void getStat() {
        final Stat result = userService.getStat(mock(User.class));

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getKapperInfoMustReturnNullIfUserIsNotKapper() {
        final User user = mock(User.class);

        final KapperInfo result = userService.getKapperInfo(user);

        assertThat(result, is(nullValue()));
        verify(user).hasRole(Role.Names.KAPPER);
        verify(user, never()).getKapperInfo();
    }

    @Test
    public void getKapperInfoMustReturnNotNullIfUserIsKapper() {
        final User user = mock(User.class);
        when(user.hasRole(Role.Names.KAPPER)).thenReturn(true);
        final KapperInfo kapperInfo = mock(KapperInfo.class);
        when(user.getKapperInfo()).thenReturn(kapperInfo);

        final KapperInfo result = userService.getKapperInfo(user);

        assertThat(result, is(kapperInfo));
        verify(user).hasRole(Role.Names.KAPPER);
        verify(user).getKapperInfo();
    }

    @Test
    public void getInfo() {
        final PersonalInfo result = userService.getInfo(mock(User.class));

        assertThat(result, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void transferMustThrowExceptionIfTransferNotFromUserRole() {
        final User user = mock(User.class);
        final User kapper = mock(User.class);
        final BigDecimal amount = BigDecimal.ONE;
        when(user.hasRole(Role.Names.USER)).thenReturn(false);

        try {
            userService.transfer(user, kapper, amount);
        } catch (Throwable t) {
            verify(user).hasRole(Role.Names.USER);
            throw t;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void transferMustThrowExceptionIfTransferNotToKapper() {
        final User user = mock(User.class);
        final User kapper = mock(User.class);
        final BigDecimal amount = BigDecimal.ONE;
        when(user.hasRole(Role.Names.USER)).thenReturn(true);
        when(kapper.hasRole(Role.Names.KAPPER)).thenReturn(false);

        try {
            userService.transfer(user, kapper, amount);
        } catch (Throwable t) {
            verify(user).hasRole(Role.Names.USER);
            verify(kapper).hasRole(Role.Names.KAPPER);
            throw t;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void transferMustThrowExceptionIfUserAmountIsLowerThanTransferAmount() {
        final User user = mock(User.class);
        final User kapper = mock(User.class);
        final BigDecimal amount = BigDecimal.ONE;
        when(user.hasRole(Role.Names.USER)).thenReturn(true);
        when(kapper.hasRole(Role.Names.KAPPER)).thenReturn(true);
        when(user.getBalance()).thenReturn(Money.of(CurrencyUnit.USD, BigDecimal.ZERO));

        try {
            userService.transfer(user, kapper, amount);
        } catch (Throwable t) {
            verify(user).hasRole(Role.Names.USER);
            verify(kapper).hasRole(Role.Names.KAPPER);
            verify(user, atLeastOnce()).getBalance();
            throw t;
        }
    }

    @Test(expected = MoneyTransferException.class)
    public void transferMustThrowExceptionIfKapperBalanceIsNull() {
        final User user = mock(User.class);
        final User kapper = mock(User.class);
        final BigDecimal amount = BigDecimal.ONE;
        when(user.hasRole(Role.Names.USER)).thenReturn(true);
        when(kapper.hasRole(Role.Names.KAPPER)).thenReturn(true);
        when(user.getBalance()).thenReturn(Money.of(CurrencyUnit.USD, BigDecimal.TEN));
        userService = spy(userService);

        try {
            userService.transfer(user, kapper, amount);
        } catch (Throwable t) {
            verify(user).hasRole(Role.Names.USER);
            verify(kapper).hasRole(Role.Names.KAPPER);
            verify(user, atLeast(2)).getBalance();
            verify(userService, never()).editUser(any());
            throw t;
        }
    }

    @Test
    public void transferIfCurrencyUnitsIsEquals() {
        final User user = spy(User.builder()
                .balance(Money.of(CurrencyUnit.USD, BigDecimal.TEN))
                .role(Role.builder()
                        .name(Role.Names.USER)
                        .build())
                .build());
        final User kapper = spy(User.builder()
                .balance(Money.of(CurrencyUnit.USD, BigDecimal.ZERO))
                .role(Role.builder()
                        .name(Role.Names.KAPPER)
                        .build())
                .build());
        final BigDecimal amount = BigDecimal.ONE;
        userService = spy(userService);
        doAnswer(it -> it.getArgument(0)).when(userService).editUser(any());

        userService.transfer(user, kapper, amount);

        assertThat(user.getBalance().getAmount(), is(new BigDecimal("9.00")));
        assertThat(kapper.getBalance().getAmount(), is(new BigDecimal("1.00")));
        verify(user).hasRole(Role.Names.USER);
        verify(kapper).hasRole(Role.Names.KAPPER);
        verify(user, atLeast(3)).getBalance();
        verify(kapper, atLeast(2)).getBalance();
        verify(userService).editUser(user);
        verify(userService).editUser(kapper);
        verify(currencyService, never()).exchange(any(CurrencyUnit.class), any(), any(CurrencyUnit.class));
    }

    @Test
    public void transferIfCurrencyUnitsIsNotEquals() {
        final User user = spy(User.builder()
                .balance(Money.of(CurrencyUnit.EUR, BigDecimal.TEN))
                .role(Role.builder()
                        .name(Role.Names.USER)
                        .build())
                .build());
        final User kapper = spy(User.builder()
                .balance(Money.of(CurrencyUnit.USD, BigDecimal.ZERO))
                .role(Role.builder()
                        .name(Role.Names.KAPPER)
                        .build())
                .build());
        final BigDecimal amount = BigDecimal.ONE;
        when(currencyService.exchange(CurrencyUnit.EUR, amount, CurrencyUnit.USD)).thenReturn(new BigDecimal("2.00"));
        userService = spy(userService);
        doAnswer(it -> it.getArgument(0)).when(userService).editUser(any());

        userService.transfer(user, kapper, amount);

        assertThat(user.getBalance().getAmount(), is(new BigDecimal("9.00")));
        assertThat(kapper.getBalance().getAmount(), is(new BigDecimal("2.00")));
        verify(user).hasRole(Role.Names.USER);
        verify(kapper).hasRole(Role.Names.KAPPER);
        verify(user, atLeast(4)).getBalance();
        verify(kapper, atLeast(2)).getBalance();
        verify(userService).editUser(user);
        verify(userService).editUser(kapper);
        verify(currencyService).exchange(CurrencyUnit.EUR, amount, CurrencyUnit.USD);
    }
}