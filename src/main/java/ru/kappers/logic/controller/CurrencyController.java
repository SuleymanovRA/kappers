package ru.kappers.logic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kappers.service.CurrencyService;

/**
 * Контроллер валют
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rest/admin/curr")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping(value = "/refresh")
    public void refreshCurrencyRatesForToday() {
        log.debug("refreshCurrencyRatesForToday()...");
        currencyService.tryRefreshCurrencyRatesForToday();
    }
}
