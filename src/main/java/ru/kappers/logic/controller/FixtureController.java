package ru.kappers.logic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.kappers.model.Fixture;
import ru.kappers.model.Fixture.Status;
import ru.kappers.service.FixtureService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/rest/fixture")
@RequiredArgsConstructor
public class FixtureController {
    private final FixtureService fixtureService;

    @ResponseBody
    @GetMapping(value = "/nextweek", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Fixture> getNextWeek() {
        log.debug("getNextWeek()...");
        return fixtureService.getFixturesNextWeek(Status.NOT_STARTED);
    }

    //todo чтобы перейти к полноценному отображению данных с паджинацией надо будет web UI обучить не только отображать список содержимого, но и информацию по страницам и переходам по ним
// пример тестового URL: http://localhost:8080/rest/fixture/nextweek?page=1&size=30&sort=eventDate
/*
    @RequestMapping(value = "/nextweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Fixture> getNextWeek(Pageable pageable) {
        return fixtureService.getFixturesNextWeek(Status.NOT_STARTED, pageable);
    }
*/

    @ResponseBody
    @GetMapping(value = "/lastweek", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Fixture> getLastWeek() {
        log.debug("getLastWeek()...");
        return fixtureService.getFixturesLastWeek(Status.MATCH_FINISHED);
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Fixture getFixtureById(@PathVariable int id) {
        log.debug("getFixtureById(id: {})...", id);
        return fixtureService.getById(id);
    }

    @ResponseBody
    @GetMapping(value = "/today", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Fixture> getFixturesToday() {
        log.debug("getFixturesToday()...");
        return fixtureService.getFixturesToday();
    }

    @ResponseBody
    @GetMapping(value = "/period", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Fixture> getFixturesByPeriod(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        log.debug("getFixturesByPeriod(fromDate: {}, toDate: {})...", fromDate, toDate);
        return fixtureService.getFixturesByPeriod(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
    }
}