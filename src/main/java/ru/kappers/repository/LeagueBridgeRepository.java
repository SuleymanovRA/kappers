package ru.kappers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kappers.model.catalog.League;
import ru.kappers.model.leonmodels.LeagueLeon;
import ru.kappers.model.mapping.LeagueBridge;

public interface LeagueBridgeRepository extends JpaRepository<LeagueBridge, Integer> {
    LeagueBridge getByRapidLeagueId(int rapidId);
    LeagueBridge getByRapidLeague(League rapidLeague);
    LeagueBridge getByLeonLeagueId (long leonId);
    LeagueBridge getByLeonLeague (LeagueLeon leonLeague);
}
