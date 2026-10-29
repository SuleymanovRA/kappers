package ru.kappers.logic.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.MvcResult;
import ru.kappers.convert.MarketLeonDTOAndOddsLeonToRunnerLeonListConverter;
import ru.kappers.convert.gson.TestGsonBuilder;
import ru.kappers.model.Fixture;
import ru.kappers.model.comparing.FixtureComparators;
import ru.kappers.service.FixtureService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest(FixtureController.class)
@AutoConfigureTestDatabase(replace = NONE)
@MockBean(MarketLeonDTOAndOddsLeonToRunnerLeonListConverter.class)
class FixtureControllerTest {
    private static final Gson GSON = TestGsonBuilder.defaultGsonBuilder()
            .create();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FixtureService fixtureService;

    @Test
    void getNextWeekWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/fixture/nextweek"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("kapper1")
    @Test
    void getNextWeek() throws Exception {
        var fixtureList = generatedFixtureList();
        when(fixtureService.getFixturesNextWeek(Fixture.Status.NOT_STARTED)).thenReturn(fixtureList);

        var mvcResult = mockMvc.perform(get("/rest/fixture/nextweek"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(responseContentToFixtureList(mvcResult))
                .usingElementComparator(FixtureComparators.common())
                .isEqualTo(fixtureList);
    }

    private List<Fixture> generatedFixtureList() {
        return Instancio.ofList(Fixture.class)
                .size(2)
                .create();
    }

    private List<Fixture> responseContentToFixtureList(MvcResult mvcResult) throws UnsupportedEncodingException {
        return GSON.fromJson(mvcResult.getResponse().getContentAsString(), new TypeToken<>() {});
    }

    @Test
    void getLastWeekWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/fixture/lastweek"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("kapper1")
    @Test
    void getLastWeek() throws Exception {
        var fixtureList = generatedFixtureList();
        when(fixtureService.getFixturesLastWeek(Fixture.Status.MATCH_FINISHED)).thenReturn(fixtureList);

        var mvcResult = mockMvc.perform(get("/rest/fixture/lastweek"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(responseContentToFixtureList(mvcResult))
                .usingElementComparator(FixtureComparators.common())
                .isEqualTo(fixtureList);
    }

    @Test
    void getFixturesTodayWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/fixture/today"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("kapper1")
    @Test
    void getFixturesToday() throws Exception {
        var fixtureList = generatedFixtureList();
        when(fixtureService.getFixturesToday()).thenReturn(fixtureList);

        var mvcResult = mockMvc.perform(get("/rest/fixture/today"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(responseContentToFixtureList(mvcResult))
                .usingElementComparator(FixtureComparators.common())
                .isEqualTo(fixtureList);
    }

    @Test
    void getFixtureByIdWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/fixture/1"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("kapper1")
    @Test
    void getFixtureById() throws Exception {
        var fixture = Instancio.create(Fixture.class);
        when(fixtureService.getById(fixture.getId())).thenReturn(fixture);

        var mvcResult = mockMvc.perform(get("/rest/fixture/%d".formatted(fixture.getId())))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(responseContentToFixture(mvcResult))
                .usingComparator(FixtureComparators.common())
                .isEqualTo(fixture);
    }

    private Fixture responseContentToFixture(MvcResult mvcResult) throws UnsupportedEncodingException {
        return GSON.fromJson(mvcResult.getResponse().getContentAsString(), new TypeToken<>() {
        });
    }

    @Test
    void getFixturesByPeriodWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/fixture/period"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("kapper1")
    @Test
    void getFixturesByPeriod() throws Exception {
        var fixtureList = generatedFixtureList();
        when(fixtureService.getFixturesByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(fixtureList);

        var todayAsString = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        var mvcResult = mockMvc.perform(get("/rest/fixture/period")
                        .param("from", todayAsString)
                        .param("to", todayAsString))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(responseContentToFixtureList(mvcResult))
                .usingElementComparator(FixtureComparators.common())
                .isEqualTo(fixtureList);
    }
}