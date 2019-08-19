package ru.kappers.service;

import ru.kappers.model.catalog.League;
import ru.kappers.model.leonmodels.LeagueLeon;
import ru.kappers.model.mapping.LeagueBridge;

import java.util.List;

public interface LeagueBridgeService {
    LeagueBridge save (LeagueBridge bridge);
    LeagueBridge get (LeagueBridge bridge);
    LeagueBridge getById (Integer id);
    void delete (LeagueBridge bridge);
    LeagueBridge update (LeagueBridge bridge);
    List <LeagueBridge> getAll();
    LeagueBridge getByRapidLeague(League league);
    LeagueBridge getByLeaonLeague (LeagueLeon league);
    LeagueLeon getLeagueLeonByRapidLeague(League league);
    LeagueLeon getLeagueLeonByRapidLeague (int leagueId);
    League getRapidLeagueByLeonLeague (LeagueLeon league);
    League getRapidLeonByLeagueLeonId (long leagueLeonId);
}
