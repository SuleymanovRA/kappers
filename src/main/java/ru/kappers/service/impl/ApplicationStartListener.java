package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Service;
import ru.kappers.config.KappersProperties;
import ru.kappers.service.CurrencyService;
import ru.kappers.service.MessageTranslator;

/**
 * Слушатель старта приложения (срабатывает после завершения подъема Spring контекста)
 */
@Slf4j
@Service
public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {
    private final CurrencyService currencyService;
    private final KappersProperties kappersProperties;
    private final MessageTranslator translator;
    private final SchedulingTaskExecutor taskExecutor;

    @Autowired
    public ApplicationStartListener(CurrencyService currencyService, KappersProperties kappersProperties,
                                    MessageTranslator translator, SchedulingTaskExecutor taskExecutor) {
        this.currencyService = currencyService;
        this.kappersProperties = kappersProperties;
        this.translator = translator;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("onApplicationEvent(event: {})...", event);
        log.info(translator.byCode("kappersMessage.springContextRefreshed"));
        log.info(translator.byCode("kappersMessage.KappersPropertiesForLog"), kappersProperties);
        taskExecutor.execute(() -> {
            try {
                currencyService.tryRefreshCurrencyRatesForToday();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
