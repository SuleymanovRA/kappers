package ru.kappers.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.RoundingMode;
import java.util.Locale;

@ConfigurationProperties(prefix = "kappers")
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

    public KappersProperties() {
    }

    public CurrencyRates getCurrencyRates() {
        return this.currencyRates;
    }

    public int getTaskSchedulerPoolSize() {
        return this.taskSchedulerPoolSize;
    }

    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public String getRequestLocaleParameterName() {
        return this.requestLocaleParameterName;
    }

    public RoundingMode getBigDecimalRoundingMode() {
        return this.bigDecimalRoundingMode;
    }

    public String getRubCurrencyCode() {
        return this.rubCurrencyCode;
    }

    public String getLeonBetURL() {
        return this.leonBetURL;
    }

    public void setCurrencyRates(CurrencyRates currencyRates) {
        this.currencyRates = currencyRates;
    }

    public void setTaskSchedulerPoolSize(int taskSchedulerPoolSize) {
        this.taskSchedulerPoolSize = taskSchedulerPoolSize;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setRequestLocaleParameterName(String requestLocaleParameterName) {
        this.requestLocaleParameterName = requestLocaleParameterName;
    }

    public void setBigDecimalRoundingMode(RoundingMode bigDecimalRoundingMode) {
        this.bigDecimalRoundingMode = bigDecimalRoundingMode;
    }

    public void setRubCurrencyCode(String rubCurrencyCode) {
        this.rubCurrencyCode = rubCurrencyCode;
    }

    public void setLeonBetURL(String leonBetURL) {
        this.leonBetURL = leonBetURL;
    }

    public static class CurrencyRates {
        /** cron-выражение для планирования обновления курсов валют, по умолчанию "0 0 0 * * ?" (каждый день в 00:00) */
        private String refreshCron = CRON_EVERY_DAY_AT_00_00;
        /** включено обновление курсов валют по расписанию cron-выражения, по умолчанию true */
        private boolean refreshCronEnabled = true;

        public CurrencyRates() {
        }

        /** cron-выражение для планирования обновления курсов валют, по умолчанию {@value #CRON_EVERY_DAY_AT_00_00} */
        public void setRefreshCron(String refreshCron) {
            this.refreshCron = refreshCron;
        }

        public String getRefreshCron() {
            return this.refreshCron;
        }

        public boolean isRefreshCronEnabled() {
            return this.refreshCronEnabled;
        }

        public void setRefreshCronEnabled(boolean refreshCronEnabled) {
            this.refreshCronEnabled = refreshCronEnabled;
        }
    }
}
