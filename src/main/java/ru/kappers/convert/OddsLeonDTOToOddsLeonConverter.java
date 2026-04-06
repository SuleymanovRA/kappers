package ru.kappers.convert;

import lombok.Builder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.dto.leon.OddsLeonDTO;
import ru.kappers.model.leonmodels.CompetitorLeon;
import ru.kappers.model.leonmodels.LeagueLeon;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.service.CompetitorLeonService;
import ru.kappers.service.LeagueLeonService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT, injectionStrategy = CONSTRUCTOR)
public abstract class OddsLeonDTOToOddsLeonConverter extends BaseMapStructConverter<OddsLeonDTO, OddsLeon> {
    protected CompetitorLeonService competitorService;
    protected LeagueLeonService leagueService;
    @Lazy
    protected ConversionService conversionService;

    @Builder
    record HomeAndAwayCompetitors(
            CompetitorLeon home,
            CompetitorLeon away
    ) {}

    @Mapping(target = "kickoff", expression = "java(convertToLocalDateTime(source.getKickoff()))")
    @Mapping(target = "lastUpdated", expression = "java(convertToLocalDateTime(source.getLastUpdated()))")
    @Mapping(target = "league", expression = "java(leagueLeon(source))")
    @Mapping(target = "home", ignore = true)
    @Mapping(target = "away", ignore = true)
    @Mapping(target = "runners", ignore = true)
    @Override
    public abstract OddsLeon convert(OddsLeonDTO source);

    @AfterMapping
    protected void afterConvert(OddsLeonDTO source, @MappingTarget OddsLeon oddsLeon) {
        var homeAndAwayCompetitors = homeAndAwayCompetitors(source);
        oddsLeon.setHome(homeAndAwayCompetitors.home);
        oddsLeon.setAway(homeAndAwayCompetitors.away);
    }

    private HomeAndAwayCompetitors homeAndAwayCompetitors(OddsLeonDTO source) {
        CompetitorLeon home = null;
        CompetitorLeon away = null;
        for (CompetitorLeonDTO dto : source.getCompetitors()) {
            CompetitorLeon comp = findCompetitor(dto)
                    .orElse(savedCompetitor(dto));
            if ("HOME".equalsIgnoreCase(dto.getHomeAway())) {
                home = comp;
            } else {
                away = comp;
            }
        }
        return HomeAndAwayCompetitors.builder()
                .home(home)
                .away(away)
                .build();
    }

    private Optional<CompetitorLeon> findCompetitor(CompetitorLeonDTO competitorLeonDTO) {
        return Optional.ofNullable(competitorService.getByName(competitorLeonDTO.getName()));
    }

    private CompetitorLeon savedCompetitor(CompetitorLeonDTO competitorLeonDTO) {
        return competitorService.save(
                conversionService.convert(competitorLeonDTO, CompetitorLeon.class));
    }

    protected LocalDateTime convertToLocalDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli),
                TimeZone.getDefault().toZoneId());
    }

    protected LeagueLeon leagueLeon(OddsLeonDTO source) {
        return findLeague(source)
                .orElse(savedLeague(source));
    }

    private Optional<LeagueLeon> findLeague(OddsLeonDTO source) {
        return Optional.ofNullable(leagueService.getByName(source.getLeague().getName()));
    }

    private LeagueLeon savedLeague(OddsLeonDTO source) {
        return leagueService.save(
                conversionService.convert(source.getLeague(), LeagueLeon.class));
    }
}