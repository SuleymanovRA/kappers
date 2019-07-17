package ru.kappers.logic.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.kappers.exceptions.UnirestAPIException;
import ru.kappers.model.catalog.League;
import ru.kappers.model.catalog.Team;
import ru.kappers.service.*;
import ru.kappers.service.parser.RapidAPIParser;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/rest/api/rapid")
public class LeguesAndTeamsController {
    private final LeagueService leagueService;
    private final TeamService teamService;
    private final RapidAPIParser<League> leagueRapidAPIParser;
    private final RapidAPIParser<Team> teamRapidAPIParser;
    private final JsonService jsonService;
    private final MessageTranslator messageTranslator;

    @Autowired
    public LeguesAndTeamsController(LeagueService leagueService, TeamService teamService, RapidAPIParser<League> leagueRapidAPIParser,
                                    RapidAPIParser<Team> teamRapidAPIParser, JsonService jsonService, MessageTranslator messageTranslator) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.leagueRapidAPIParser = leagueRapidAPIParser;
        this.teamRapidAPIParser = teamRapidAPIParser;
        this.jsonService = jsonService;
        this.messageTranslator = messageTranslator;
    }


    @ResponseBody
    @RequestMapping(value = "/league", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<League> getLeaguesList() {
        log.debug("getLeaguesList()...");
        try {
            final List<League> leagues = leagueRapidAPIParser.parseListFromJSON(jsonService.loadLeagues().toString());
            return saveEntities(leagues, leagueService::save);
        } catch (UnirestException e) {
            throw new UnirestAPIException(messageTranslator.byCode("rapidAPI.leagueListLoadingError"), e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/leagues/season/{season}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<League> getLeaguesListBySeason(@PathVariable String season) {
        log.debug("getLeaguesListBySeason(season: {})...", season);
        try {
            final List<League> leagues = leagueRapidAPIParser.parseListFromJSON(jsonService.loadLeaguesOfSeason(season).toString());
            return saveEntities(leagues, leagueService::save);
        } catch (UnirestException e) {
            throw new UnirestAPIException(messageTranslator.byCode("rapidAPI.leagueListLoadingErrorForSeason", season), e);
        }
        //TODO в выдавемом JSON часовой пояс на UTC, а надо на UTC+3
    }

    @ResponseBody
    @RequestMapping(value = "/teams/league/{leagueId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Team> getTeamsOfLeague(@PathVariable Integer leagueId) {
        log.debug("getTeamsOfLeague(leagueId: {})...", leagueId);
        try {
            final List<Team> list = teamRapidAPIParser.parseListFromJSON(jsonService.loadTeamsByLeague(leagueId).toString());
            return saveEntities(list, teamService::save);
        } catch (UnirestException e) {
            throw new UnirestAPIException(messageTranslator.byCode("rapidAPI.teamListLoadingErrorForLeagueWithId", leagueId), e);
        }
    }

    /**
     * Сохранить сущности из указанного списка
     *
     * @param entities     список сохраняемых сущностей
     * @param saveFunction ссылка на метод сохранения
     * @param <T>          обобщенный тип сущности
     * @return список сохраненных сущностей
     */
    public <T> List<T> saveEntities(List<T> entities, Function<T, T> saveFunction) {
        return entities.stream()
                .map(saveFunction::apply)
                .collect(Collectors.toList());
    }
}
