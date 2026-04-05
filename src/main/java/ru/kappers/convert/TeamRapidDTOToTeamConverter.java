package ru.kappers.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.catalog.Team;
import ru.kappers.model.dto.rapidapi.TeamRapidDTO;

import javax.annotation.Nullable;

@Service
public class TeamRapidDTOToTeamConverter implements Converter<TeamRapidDTO, Team> {
    @Nullable
    @Override
    public Team convert(@Nullable TeamRapidDTO source) {
        if (source == null) {
            return null;
        }
        return Team.builder()
                .id(source.getTeam_id())
                .name(source.getName())
                .code(source.getCode())
                .logo(source.getLogo())
                .build();
    }
}