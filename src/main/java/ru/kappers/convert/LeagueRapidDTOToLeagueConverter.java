package ru.kappers.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.catalog.League;
import ru.kappers.model.dto.rapidapi.LeagueRapidDTO;
import ru.kappers.util.DateTimeUtil;

import javax.annotation.Nullable;

@Service
public class LeagueRapidDTOToLeagueConverter implements Converter<LeagueRapidDTO, League> {
    @Nullable
    @Override
    public League convert(@Nullable LeagueRapidDTO source) {
        if (source == null) {
            return null;
        }

        return League.builder()
                .id(source.getLeague_id())
                .country(source.getCountry())
                .name(source.getName())
                .logoUrl(source.getLogo())
                .season(source.getSeason())
                .seasonStart(DateTimeUtil.parseLocalDateTimeFromStartOfDate(source.getSeason_start()+"+03:00"))
                .seasonEnd(DateTimeUtil.parseLocalDateTimeFromStartOfDate(source.getSeason_end()+"+03:00"))
                .build();
    }
}
