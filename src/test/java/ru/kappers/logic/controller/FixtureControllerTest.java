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
import ru.kappers.convert.MarketLeonDTOAndOddsLeonToRunnerLeonListConverter;
import ru.kappers.model.Fixture;
import ru.kappers.service.FixtureService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private static Gson GSON = new Gson();
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
        var resultFixtureList = GSON.fromJson(mvcResult.getResponse().getContentAsString(), new TypeToken<List<Fixture>>(){});
        assertThat(resultFixtureList)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(fixtureList);
    }

    private List<Fixture> generatedFixtureList() {
        return Instancio.ofList(Fixture.class)
                .size(2)
                .create();
    }
}