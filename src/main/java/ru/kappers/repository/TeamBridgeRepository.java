package ru.kappers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kappers.model.catalog.Team;
import ru.kappers.model.leonmodels.CompetitorLeon;
import ru.kappers.model.mapping.TeamBridge;

/**
 * Репозиторий для работы с БД сущности TeamBridge
 */
public interface TeamBridgeRepository extends JpaRepository<TeamBridge, Integer> {
    TeamBridge getByRapidTeam(Team rapid);
    TeamBridge getByRapidTeamId(int rapidId);
    TeamBridge getByLeonCompetitor(CompetitorLeon competitor);
    TeamBridge getByLeonCompetitorId(long competitorId);

}
