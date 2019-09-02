package ru.kappers.service.impl;

import com.google.common.base.Preconditions;
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
        Preconditions.checkNotNull(bridge, "bridge is required not null");
        return null;
    }

    @Override
    public LeagueBridge get(LeagueBridge bridge) {
        log.debug("LeagueBridge get (bridge: {})...", bridge);
        Preconditions.checkNotNull(bridge, "bridge is required not null");
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
        Preconditions.checkNotNull(bridge, "bridge is required not null");
        repository.delete(bridge);
    }

    @Override
    public LeagueBridge update(LeagueBridge bridge) {
        log.debug("LeagueBridge update (bridge: {})...", bridge);
        Preconditions.checkNotNull(bridge, "bridge is required not null");
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

        return null;
    }

    @Override
    public LeagueBridge getByRapidLeague(League league) {
        log.debug("LeagueBridge getByRapidLeague (league: {})...", league);
        Preconditions.checkNotNull(league, "league is required not null");

        return null;
    }

    @Override
    public LeagueBridge getByLeaonLeague(LeagueLeon league) {
        log.debug("LeagueBridge getByLeaonLeague (league: {})...", league);
        Preconditions.checkNotNull(league, "league is required not null");

        return null;
    }

    @Override
    public LeagueLeon getLeagueLeonByRapidLeague(League league) {
        log.debug("LeagueBridge getLeagueLeonByRapidLeague (league: {})...", league);
        Preconditions.checkNotNull(league, "league is required not null");

        return null;
    }

    @Override
    public LeagueLeon getLeagueLeonByRapidLeague(int leagueId) {
        log.debug("LeagueBridge getLeagueLeonByRapidLeague (leagueId: {})...", leagueId);

        return null;
    }

    @Override
    public League getRapidLeagueByLeonLeague(LeagueLeon league) {
        log.debug("LeagueBridge getRapidLeagueByLeonLeague (league: {})...", league);
        Preconditions.checkNotNull(league, "league is required not null");


        return null;
    }

    @Override
    public League getRapidLeonByLeagueLeonId(long leagueLeonId) {
        log.debug("LeagueBridge getRapidLeonByLeagueLeonId (leagueLeonId: {})...", leagueLeonId);

        return null;
    }
}
