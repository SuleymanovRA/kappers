package ru.kappers.logic.controller.web;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.kappers.convert.OddsLeonDTOToOddsLeonConverter;
import ru.kappers.convert.PairOfMarketLeonDTOAndOddsLeonToRunnerLeonListConverter;
import ru.kappers.logic.controller.EventController;
import ru.kappers.model.Event;
import ru.kappers.model.Fixture;
import ru.kappers.model.User;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.service.EventService;
import ru.kappers.service.FixtureService;
import ru.kappers.service.UserService;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest(EventController.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@MockBean(OddsLeonDTOToOddsLeonConverter.class)
@MockBean(PairOfMarketLeonDTOAndOddsLeonToRunnerLeonListConverter.class)
public class EventControllerTest {
    public Gson GSON = new Gson();
    @MockBean
    private FixtureService fixtureService;
    @MockBean
    private UserService userService;
    @MockBean
    private EventService eventService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getFixtureByIdWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/rest/events/" + 101))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("kapper1")
    @Test
    public void getFixtureById() throws Exception {
        Fixture fixture = Instancio.of(Fixture.class)
                .generate(field(Fixture::getId), Generators::intSeq)
                .create();
        when(fixtureService.getById(fixture.getId())).thenReturn(fixture);

        mockMvc.perform(get("/rest/events/" + fixture.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fixture.id").value(fixture.getId()))
                .andExpect(jsonPath("$.fixture.eventTimestamp").value(fixture.getEventTimestamp()));
    }

    @Test
    public void createEventWithoutAuthorization() throws Exception {
        mockMvc.perform(post("/rest/events/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("kapper")
    public void createEvent() throws Exception {
        EventDTO eventDTO = generatedEventDTO();
        User user = generatedUserWithUsername("kapper");
        when(userService.getByUserName(user.getUserName())).thenReturn(user);
        prepareEventCreationByUser();
        prepareFixtureSearch(eventDTO);

        mockMvc.perform(post("/rest/events/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GSON.toJson(eventDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.f_id").value(eventDTO.getF_id()))
                .andExpect(jsonPath("$.outcome").value(eventDTO.getOutcome().name()))
                .andExpect(jsonPath("$.coefficient").value(eventDTO.getCoefficient()))
                .andExpect(jsonPath("$.tokens").value(eventDTO.getTokens()))
                .andExpect(jsonPath("$.price").value(eventDTO.getPrice()));
    }

    private EventDTO generatedEventDTO() {
        return Instancio.of(EventDTO.class)
                .generate(field(EventDTO::getF_id), Generators::intSeq)
                .create();
    }

    private User generatedUserWithUsername(String username) {
        return Instancio.of(User.class)
                .set(field(User::getUserName), username)
                .create();
    }

    private void prepareEventCreationByUser() {
        when(eventService.createEventByUser(any(Event.class), any(User.class)))
                .then(invocation -> {
                    Event event = invocation.getArgument(0);
                    event.setKapper(invocation.getArgument(1));
                    return event;
                });
    }

    private void prepareFixtureSearch(EventDTO eventDTO) {
        when(fixtureService.getById(eventDTO.getF_id()))
                .then(invocation -> generatedFixtureWithId(invocation.getArgument(0)));
    }

    private Fixture generatedFixtureWithId(int id) {
        return Instancio.of(Fixture.class)
                .set(field(Fixture::getId), id)
                .create();
    }
}