package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kappers.model.Fixture;
import ru.kappers.model.dto.rapidapi.FixtureRapidDTO;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

/**
 * Конвертер из {@link FixtureRapidDTO} в {@link Fixture}
 */
@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class FixtureRapidDTOToFixtureConverter extends BaseMapStructConverter<FixtureRapidDTO, Fixture> {
    @Mapping(source = "fixture_id", target = "id")
    @Mapping(source = "event_timestamp", target = "eventTimestamp")
    @Mapping(target = "eventDate", expression = "java(ru.kappers.util.DateTimeUtil.parseLocalDateTimeFromZonedDateTime(source.getEvent_date()))")
    @Mapping(source = "league_id", target = "leagueId")
    @Mapping(source = "homeTeam_id", target = "homeTeamId")
    @Mapping(source = "awayTeam_id", target = "awayTeamId")
    @Mapping(target = "status", expression = "java(Fixture.Status.byValue(source.getStatus()))")
    @Mapping(target = "statusShort", expression = "java(Fixture.ShortStatus.byValue(source.getStatusShort()))")
    @Mapping(source = "halftime_score", target = "halftimeScore")
    @Mapping(source = "final_score", target = "finalScore")
    @Mapping(target = "events", ignore = true)
    @Override
    public abstract Fixture convert(FixtureRapidDTO source);
}
