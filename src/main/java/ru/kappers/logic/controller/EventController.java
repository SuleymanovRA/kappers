package ru.kappers.logic.controller;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kappers.model.Event;
import ru.kappers.model.Fixture;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.model.utilmodel.Odds;
import ru.kappers.service.EventService;
import ru.kappers.service.FixtureService;
import ru.kappers.service.UserService;

@Slf4j
@RestController
@RequestMapping(value = "/rest/events")
@RequiredArgsConstructor
public class EventController {
    private final FixtureService fixtureService;
    private final EventService eventService;
    private final UserService userService;
    private final ConversionService conversionService;
    public static final Gson GSON = new Gson();

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Odds getFixtureById(@PathVariable int id) {
        log.debug("getFixtureById(id: {})...", id);
        Fixture fixture = fixtureService.getById(id);
        return new Odds(fixture);
    }
    /**
     * Создать {@link Event} от имени текущего пользователя
     * Пример JSON для создания евента:
     *
     * {
     * 	"outcome":"GUESTTEAMWIN", //в этом случае ставка на гостевую команду
     * 	"coefficient":"1.35", //кэф пока берем от балды
     * 	"tokens":"50", //сколько токенов ставим
     * 	"price":"500",  //какую цену назначаем за открыте евента юзерами
     * 	"f_id":"37743" //айди фиксчи
     * }
     *
     * */
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, headers = "Accept=application/json",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDTO createEvent(@RequestBody String content) {
        log.debug("createEvent(content: {})...", content);
        var eventDTO = GSON.fromJson(content, EventDTO.class);
        var event = conversionService.convert(eventDTO, Event.class);
        var user = userService.getByUserName(getCurrentAuthentication().getName());
        return conversionService.convert(
                eventService.createEventByUser(event, user),
                EventDTO.class);
    }

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
