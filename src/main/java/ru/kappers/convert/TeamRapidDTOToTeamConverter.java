package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kappers.model.catalog.Team;
import ru.kappers.model.dto.rapidapi.TeamRapidDTO;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class TeamRapidDTOToTeamConverter extends BaseMapStructConverter<TeamRapidDTO, Team> {
    @Mapping(source = "team_id", target = "id")
    @Override
    public abstract Team convert(TeamRapidDTO source);
}