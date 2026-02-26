package ru.kappers.logic.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.kappers.service.CurrencyService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyControllerTest {
    @InjectMocks
    private CurrencyController currencyController;
    @Mock
    private CurrencyService currencyService;

    @Test
    public void refreshCurrencyRatesForToday() {
        currencyController.refreshCurrencyRatesForToday();

        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }
}