package ru.kappers.logic.controller;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.kappers.convert.MarketLeonDTOAndOddsLeonToRunnerLeonListConverter;
import ru.kappers.service.CurrencyService;
import ru.kappers.service.FixtureService;

import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest(CurrencyController.class)
@AutoConfigureTestDatabase(replace = NONE)
@MockBean(FixtureService.class)
@MockBean(MarketLeonDTOAndOddsLeonToRunnerLeonListConverter.class)
public class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CurrencyService currencyService;

    @WithMockUser("kapper1")
    @Test
    public void refreshCurrencyRatesForToday() throws Exception {
        mockMvc.perform(get("/rest/admin/curr/refresh"))
                .andExpect(status().isOk());
        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    public void refreshCurrencyRatesForTodayWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/admin/curr/refresh"))
                .andExpect(status().isUnauthorized());
    }
}