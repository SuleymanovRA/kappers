package ru.kappers.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.SchedulingTaskExecutor;
import ru.kappers.config.KappersProperties;
import ru.kappers.service.CurrencyService;
import ru.kappers.service.MessageTranslator;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationStartListenerTest {
    @InjectMocks
    private ApplicationStartListener applicationStartListener;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private KappersProperties kappersProperties;
    @Mock
    private MessageTranslator translator;
    @Mock
    private SchedulingTaskExecutor taskExecutor;

    @Test
    public void onApplicationEvent() {
        doAnswer(it -> {
            Runnable task = it.getArgument(0);
            task.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        applicationStartListener.onApplicationEvent(mock(ContextRefreshedEvent.class));

        verify(taskExecutor).execute(any(Runnable.class));
        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }
}