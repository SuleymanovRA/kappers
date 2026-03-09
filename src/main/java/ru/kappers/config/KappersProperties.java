package ru.kappers.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.RoundingMode;
import java.util.Locale;

@ConfigurationProperties(prefix = "kappers")
@NoArgsConstructor
@Getter
@Setter
public class KappersProperties {
    /** Cron-выражение - каждый день в 00:00 */
    public static final String CRON_EVERY_DAY_AT_00_00 = "0 0 0 * * ?";
    /** Locale для русского языка в Российской Федерации */
    public static final Locale RUSSIAN_LOCALE = new Locale("ru", "RU");
    /** Имя параметра для Locale в запросе по умолчанию */
    public static final String REQUEST_LOCALE_PARAMETER_NAME_DEFAULT = "lang";

    /** Курсы валют */
    private CurrencyRates currencyRates = new CurrencyRates();
    /** Размер пула планировщика заданий */
    private int taskSchedulerPoolSize = 10;
    /** Locale по умолчанию */
    private Locale defaultLocale = RUSSIAN_LOCALE;
    /** Имя параметра для Locale в запросе */
    private String requestLocaleParameterName = REQUEST_LOCALE_PARAMETER_NAME_DEFAULT;
    /** Режим округления для {@link java.math.BigDecimal} */
    private RoundingMode bigDecimalRoundingMode = RoundingMode.HALF_UP;
    /** Код валюты рубля (вообще то их 2: RUB и RUR, RUB появился в 1998 году) */
    private String rubCurrencyCode = "RUB";
    /** Адрес сайта букмекера ООО "Леон" */
    private String leonBetURL = "https://www.leon.ru";

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CurrencyRates {
        /** cron-выражение для планирования обновления курсов валют, по умолчанию {@value #CRON_EVERY_DAY_AT_00_00} */
        private String refreshCron = CRON_EVERY_DAY_AT_00_00;
        /** включено обновление курсов валют по расписанию cron-выражения, по умолчанию true */
        private boolean refreshCronEnabled = true;
    }
}
