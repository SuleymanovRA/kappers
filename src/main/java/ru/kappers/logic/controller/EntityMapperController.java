package ru.kappers.logic.controller;

import com.google.gson.Gson;
import com.ibm.icu.text.StringTransform;
import com.ibm.icu.text.Transliterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.kappers.model.Fixture;
import ru.kappers.model.catalog.Team;
import ru.kappers.model.dto.TeamBridgeDTO;
import ru.kappers.model.leonmodels.CompetitorLeon;
import ru.kappers.model.mapping.TeamBridge;
import ru.kappers.service.CompetitorLeonService;
import ru.kappers.service.FixtureService;
import ru.kappers.service.TeamBridgeService;
import ru.kappers.service.TeamService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер предназначен для маппинга сущностей Команд: Team и CompetitorLeon
 * @author Ashamaz Shomakhov
 */

@Slf4j
@RestController
@RequestMapping(value = "/rest/mapper")
public class EntityMapperController {
    private static final String CYRILLIC_TO_LATIN = "Latin-Russian/BGN";
    private static final Gson GSON = new Gson();

    private final TeamService teamService;
    private final CompetitorLeonService competitorService;
    private final FixtureService fixtureService;
    private final TeamBridgeService teamBridgeService;
    private final StringTransform toLatinTranslit = Transliterator.getInstance(CYRILLIC_TO_LATIN);


    @Autowired
    public EntityMapperController(TeamService teamService, CompetitorLeonService competitorService, FixtureService fixtureService, TeamBridgeService teamBridgeService) {
        this.teamService = teamService;
        this.competitorService = competitorService;
        this.fixtureService = fixtureService;
        this.teamBridgeService = teamBridgeService;
    }

    /**
     * getUnmappedRapidTeams возвращает не смапленные сущности Team из базы данных
     */

    @ResponseBody
    @RequestMapping(value = "/teams/unmapped/rapidteams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Team> getUnmappedRapidTeams() {
        log.debug("getUnmappedRapidTeams()...");
        List<Integer> mappedIds = teamBridgeService.getAll().stream()
                .map(s -> s.getRapidTeam().getId())
                .collect(Collectors.toList());
        return teamService.getAllByIdIsNotIn(mappedIds);
    }

    /**
     * getUnmappedLeonCompetitors возвращает не смапленные сущности CompetitorLeon из базы данных
     */

    @ResponseBody
    @RequestMapping(value = "/teams/unmapped/competitors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CompetitorLeon> getUnmappedLeonCompetitors() {
        log.debug("getUnmappedLeonCompetitors()...");
        List<Long> mappedIds = teamBridgeService.getAll().stream()
                .map(s -> s.getLeonCompetitor().getId())
                .collect(Collectors.toList());
        return competitorService.getAllByIdIsNotIn(mappedIds);
    }

    /**
     * completeRapidTeamsFromExistFixtures получает все имеющиеся Fixture и сохраняет в сущности Team
     * все команды, участвующие в этих событиях
     */
    @ResponseBody
    @RequestMapping(value = "/teams/getTeamsFromFixtures", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Team> completeRapidTeamsFromExistFixtures() {
        log.debug("completeRapidTeamsFromExistFixtures()...");
        Set<Team> teamsFromFixtures = new HashSet<>();
        List<Fixture> fixtureList = fixtureService.getAll();
        for (Fixture f : fixtureList) {
            Team byId = teamService.getById(f.getAwayTeamId());
            if (byId == null) {
                teamsFromFixtures.add(teamService.save(Team.builder()
                        .id(f.getAwayTeamId())
                        .name(f.getAwayTeam())
                        .build()));
            }
            byId = teamService.getById(f.getHomeTeamId());
            if (byId == null) {
                teamsFromFixtures.add(teamService.save(Team.builder()
                        .id(f.getHomeTeamId())
                        .name(f.getHomeTeam())
                        .build()));
            }
        }
        return teamsFromFixtures;
    }

    /**
     * tryMappingRapidTeamAndLeonCompetitor - метод получает все сущности Team и CompetitorLeon
     * и пытается их смапить по названию через транслитерацию
     */
    @ResponseBody
    @RequestMapping(value = "/teams/map", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Team, CompetitorLeon> tryMappingRapidTeamAndLeonCompetitor() {
        log.debug("tryMappingRapidTeamAndLeonCompetitor()...");
        final Set<String> rapidTeams = teamService.getAll().stream()
                .map(Team::getName)
                .collect(Collectors.toSet());
        log.debug("tryMappingRapidTeamAndLeonCompetitor(): получено {} сущностей Team", rapidTeams.size());
        final List<String> leonComps = competitorService.getAll().stream()
                .map(CompetitorLeon::getName)
                .collect(Collectors.toList());
        log.debug("tryMappingRapidTeamAndLeonCompetitor(): получено {} сущностей CompetitorLeon", leonComps.size());
        final Map<Team, CompetitorLeon> result = new HashMap<>(rapidTeams.size());
        TeamBridge teamBridge;
        for (String st : rapidTeams) {
            if (leonComps.contains(st)) {
                teamBridge = saveTeamBridge(teamService.getByName(st), competitorService.getByName(st));
                result.put(teamBridge.getRapidTeam(), teamBridge.getLeonCompetitor());
            } else {
                String res = toLatinTranslit.transform(st);
                if (leonComps.contains(res)) {
                    teamBridge = saveTeamBridge(teamService.getByName(st), competitorService.getByName(res));
                    result.put(teamBridge.getRapidTeam(), teamBridge.getLeonCompetitor());
                }
            }
        }
        return result;
    }

    /**
     * saveMappedTeams - сохраняет в БД смапленную сущность, полученную через POST запрос с фронта
     * Пример JSON который надо получить
     * {
     * "rapidTeam":"1088",
     * "leonCompetitor":"281474976793815"
     * }
     */
    @RequestMapping(value = "/teams/save", method = RequestMethod.POST, headers = "Accept=application/json",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TeamBridge saveMappedTeams(@RequestBody String content) {
        log.debug("saveMappedTeams(content: {})...", content);
        TeamBridgeDTO bridge = GSON.fromJson(content, TeamBridgeDTO.class);
        Team team = teamService.getById(Integer.parseInt(bridge.getRapidTeam()));
        CompetitorLeon competitorLeon = competitorService.getById(Long.parseLong(bridge.getLeonCompetitor()));
        return saveTeamBridge(team, competitorLeon);
    }

    private TeamBridge saveTeamBridge(Team team, CompetitorLeon competitor) {
        return teamBridgeService.save(TeamBridge.builder()
                .rapidTeam(team)
                .leonCompetitor(competitor)
                .build());
    }


}
