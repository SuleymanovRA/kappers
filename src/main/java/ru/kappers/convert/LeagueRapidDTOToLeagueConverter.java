package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kappers.model.catalog.League;
import ru.kappers.model.dto.rapidapi.LeagueRapidDTO;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class LeagueRapidDTOToLeagueConverter extends BaseMapStructConverter<LeagueRapidDTO, League> {
    @Mapping(source = "league_id", target = "id")
    @Mapping(source = "logo", target = "logoUrl")
    @Mapping(target = "seasonStart", expression = "java(ru.kappers.util.DateTimeUtil.parseLocalDateTimeFromStartOfDate(source.getSeason_start()+\"+03:00\"))")
    @Mapping(target = "seasonEnd", expression = "java(ru.kappers.util.DateTimeUtil.parseLocalDateTimeFromStartOfDate(source.getSeason_end()+\"+03:00\"))")
    @Override
    public abstract League convert(LeagueRapidDTO source);
}
