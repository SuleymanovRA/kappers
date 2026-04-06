package ru.kappers.convert;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
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

@Service
@Slf4j
public class OddsLeonDTOToOddsLeonConverter implements Converter<OddsLeonDTO, OddsLeon> {
    private final CompetitorLeonService competitorService;
    private final LeagueLeonService leagueService;
    private final ConversionService conversionService;

    @Autowired
    public OddsLeonDTOToOddsLeonConverter(CompetitorLeonService competitorService, LeagueLeonService leagueService,
                                          @Lazy ConversionService conversionService) {
        this.competitorService = competitorService;
        this.leagueService = leagueService;
        this.conversionService = conversionService;
    }

    @Builder
    record HomeAndAwayCompetitors(
            CompetitorLeon home,
            CompetitorLeon away
    ) {}

    @Nullable
    @Override
    public OddsLeon convert(@Nullable OddsLeonDTO source) {
        if (source == null) {
            return null;
        }
        var homeAndAwayCompetitors = homeAndAwayCompetitors(source);
        return OddsLeon.builder()
                .id(source.getId())
                .name(source.getName())
                .kickoff(LocalDateTime.ofInstant(Instant.ofEpochMilli(source.getKickoff()),
                        TimeZone.getDefault().toZoneId()))
                .open(source.isOpen())
                .url(source.getUrl())
                .lastUpdated(LocalDateTime.ofInstant(Instant.ofEpochMilli(source.getLastUpdated()),
                        TimeZone.getDefault().toZoneId()))
                .league(leagueLeon(source))
                .home(homeAndAwayCompetitors.home)
                .away(homeAndAwayCompetitors.away)
                .build();
    }

    protected LeagueLeon leagueLeon(OddsLeonDTO source) {
        return findLeague(source)
                .orElse(savedLeague(source));
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

    private Optional<LeagueLeon> findLeague(OddsLeonDTO source) {
        return Optional.ofNullable(leagueService.getByName(source.getLeague().getName()));
    }

    private LeagueLeon savedLeague(OddsLeonDTO source) {
        return leagueService.save(
                conversionService.convert(source.getLeague(), LeagueLeon.class));
    }
}