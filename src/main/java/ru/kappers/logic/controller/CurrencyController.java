package ru.kappers.logic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kappers.service.CurrencyService;

/**
 * Контроллер валют
 */
@Slf4j
@RestController
@RequestMapping(value = "/rest/admin/curr")
public class CurrencyController {
    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping(value = "/refresh")
    public void refreshCurrencyRatesForToday() {
        currencyService.tryRefreshCurrencyRatesForToday();
    }
}
