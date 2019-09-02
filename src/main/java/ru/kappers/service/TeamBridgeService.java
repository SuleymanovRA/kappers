package ru.kappers.service;

import ru.kappers.model.catalog.Team;
import ru.kappers.model.leonmodels.CompetitorLeon;
import ru.kappers.model.mapping.TeamBridge;

import java.util.List;

public interface TeamBridgeService {
    TeamBridge save (TeamBridge bridge);
    TeamBridge get (TeamBridge bridge);
    TeamBridge getById (Integer bridgeId);
    void delete (TeamBridge bridge);
    TeamBridge update (TeamBridge bridge);
    List<TeamBridge> getAll ();
    TeamBridge getByRapidTeam (Team team);
    TeamBridge getByCompetitorLeon (CompetitorLeon competitor);
    CompetitorLeon getCompetitorByTeamId (int team);
    CompetitorLeon getCompetitorByTeam (Team team);
    Team getTeamByCompetitorLeonId (long competitor);
    Team getTeamByCompetitorLeon (CompetitorLeon competitor);
}
