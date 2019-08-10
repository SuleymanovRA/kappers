package ru.kappers.logic.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.kappers.model.Event;
import ru.kappers.model.Fixture;
import ru.kappers.service.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/rest/api/fixtures")
public class GetFixturesByAPIController {
    //Этот контроллер доступен для вызова только пользователям с ролью ROLE_ADMIN
    private final FixtureService service;
    private final EventService eventService;
    private final JsonService jsonService;
    private final MessageTranslator messageTranslator;

    @Autowired
    public GetFixturesByAPIController(FixtureService service,
                                      EventService eventService,
                                      JsonService jsonService,
                                      MessageTranslator messageTranslator) {
        this.service = service;
        this.eventService = eventService;
        this.jsonService = jsonService;
        this.messageTranslator = messageTranslator;
    }

    /**
     * Метод предназначен для обновления списка спортивных событий
     * - две недели назад от сегодняшнего дня, и две недели вперед - предстоящие
     * Внимание! На период разработки выставлено 5 дней до и 5 дней после. Можно менять в своих целях. Перед деплоем нужно выставить 7 в цикле
     */
    @ResponseBody
    @RequestMapping(value = "/twoweeks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFixturesLastWeek() {
        log.debug("getFixturesLastWeek()");
        for (long i = -5; i < 5; i++) {
            try {
                JSONObject jsonObject = jsonService.loadFixturesByDate(LocalDate.now().plusDays(i));
                Map<Integer, Fixture> fixturesFromJson = jsonService.getFixturesFromJson(jsonObject.toString());
                for (Map.Entry<Integer, Fixture> entry : fixturesFromJson.entrySet()) {
                    Fixture value = entry.getValue();
                    value.setId(entry.getKey());
                    Event event = eventService.getById(entry.getKey());
                    service.addRecord(value);
                    if (event!=null){
                        completeEventData(event, value);
                    }
                }
            } catch (UnirestException e) {
                log.error(messageTranslator.byCode("rapidAPI.twoWeeks.errorOnLoading"), e);
                return ResponseEntity.unprocessableEntity().body(messageTranslator.byCode("rapidAPI.twoWeeks.errorOnLoading"));
            } catch (Exception e){
                log.error(messageTranslator.byCode("rapidAPI.unexpectedError"), e);
                return ResponseEntity.unprocessableEntity().body(messageTranslator.byCode("rapidAPI.unexpectedError"));
            }
        }

        return ResponseEntity.ok(messageTranslator.byCode("rapidAPI.twoWeeks.loaded"));
    }

    private void completeEventData(Event event, Fixture value) {
        //TODO сравнить результат, закрыть евент

    }

    /**
     * Выгрузка всех событий без каких либо фильтров из БД
     */

    @ResponseBody
    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public List<Fixture> getAllFixtures() {
        return service.getAll();
    }


}
