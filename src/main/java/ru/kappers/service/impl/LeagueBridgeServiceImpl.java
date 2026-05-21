package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.model.catalog.League;
import ru.kappers.model.leonmodels.LeagueLeon;
import ru.kappers.model.mapping.LeagueBridge;
import ru.kappers.model.mapping.TeamBridge;
import ru.kappers.repository.LeagueBridgeRepository;
import ru.kappers.service.LeagueBridgeService;
import ru.kappers.service.LeagueLeonService;
import ru.kappers.service.LeagueService;

import java.util.List;

@Service
@Slf4j
@Transactional
public class LeagueBridgeServiceImpl implements LeagueBridgeService {
    private static final String BRIDGE_IS_REQUIRED_NOT_NULL = "bridge is required not null";
    private static final String LEAGUE_IS_REQUIRED_NOT_NULL = "league is required not null";
    private final LeagueBridgeRepository repository;
    private final LeagueLeonService leonService;
    private final LeagueService rapidLeonService;

    @Autowired
    public LeagueBridgeServiceImpl(LeagueBridgeRepository repository, LeagueLeonService leonService, LeagueService rapidLeonService) {
        this.repository = repository;
        this.leonService = leonService;
        this.rapidLeonService = rapidLeonService;
    }

    @Override
    public LeagueBridge save(LeagueBridge bridge) {
        log.debug("LeagueBridge save (bridge: {})...", bridge);
        checkNotNull(bridge, BRIDGE_IS_REQUIRED_NOT_NULL);
        return null;
    }

    private <T> void checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    @Override
    public LeagueBridge get(LeagueBridge bridge) {
        log.debug("LeagueBridge get (bridge: {})...", bridge);
        checkNotNull(bridge, BRIDGE_IS_REQUIRED_NOT_NULL);
        return getById(bridge.getId());
    }

    @Override
    public LeagueBridge getById(Integer id) {
        log.debug("LeagueBridge getById (id: {})...", id);
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(LeagueBridge bridge) {
        log.debug("LeagueBridge delete (bridge: {})...", bridge);
        checkNotNull(bridge, BRIDGE_IS_REQUIRED_NOT_NULL);
        repository.delete(bridge);
    }

    @Override
    public LeagueBridge update(LeagueBridge bridge) {
        log.debug("LeagueBridge update (bridge: {})...", bridge);
        checkNotNull(bridge, BRIDGE_IS_REQUIRED_NOT_NULL);
        LeagueBridge byId = null;
        if (bridge.getId() != null) {
            byId = getById(bridge.getId());
        }
        if (byId == null) {
            return save(bridge);
        } else {
            byId.setLeonLeague(bridge.getLeonLeague());
            byId.setRapidLeague(bridge.getRapidLeague());
            return save(byId);
        }
    }

    @Override
    public List<LeagueBridge> getAll() {
        log.debug("LeagueBridge getAll ()...");
        //todo
        return List.of();
    }

    @Override
    public LeagueBridge getByRapidLeague(League league) {
        log.debug("LeagueBridge getByRapidLeague (league: {})...", league);
        checkNotNull(league, LEAGUE_IS_REQUIRED_NOT_NULL);
        //todo
        return null;
    }

    @Override
    public LeagueBridge getByLeaonLeague(LeagueLeon league) {
        log.debug("LeagueBridge getByLeaonLeague (league: {})...", league);
        checkNotNull(league, LEAGUE_IS_REQUIRED_NOT_NULL);
        //todo
        return null;
    }

    @Override
    public LeagueLeon getLeagueLeonByRapidLeague(League league) {
        log.debug("LeagueBridge getLeagueLeonByRapidLeague (league: {})...", league);
        checkNotNull(league, LEAGUE_IS_REQUIRED_NOT_NULL);
        //todo
        return null;
    }

    @Override
    public LeagueLeon getLeagueLeonByRapidLeague(int leagueId) {
        log.debug("LeagueBridge getLeagueLeonByRapidLeague (leagueId: {})...", leagueId);
        //todo
        return null;
    }

    @Override
    public League getRapidLeagueByLeonLeague(LeagueLeon league) {
        log.debug("LeagueBridge getRapidLeagueByLeonLeague (league: {})...", league);
        checkNotNull(league, LEAGUE_IS_REQUIRED_NOT_NULL);
        //todo
        return null;
    }

    @Override
    public League getRapidLeonByLeagueLeonId(long leagueLeonId) {
        log.debug("LeagueBridge getRapidLeonByLeagueLeonId (leagueLeonId: {})...", leagueLeonId);
        //todo
        return null;
    }
}
