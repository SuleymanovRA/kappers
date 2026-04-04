package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.catalog.League;
import ru.kappers.model.dto.rapidapi.LeagueRapidDTO;
import ru.kappers.util.DateTimeUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

class LeagueRapidDTOToLeagueConverterTest extends UnitTest {
    @InjectMocks
    private LeagueRapidDTOToLeagueConverterImpl converter;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (LeagueRapidDTO dto : generatedLeagueRapidDTOList()) {
            final League league = converter.convert(dto);
            assertThat(league)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("id", "logoUrl", "seasonStart", "seasonEnd", "leagueBridge")
                    .isEqualTo(dto);
            assertLeagueNotByFieldName(league, dto);
        }
    }

    private List<LeagueRapidDTO> generatedLeagueRapidDTOList() {
        return Instancio.ofList(LeagueRapidDTO.class)
                .size(2)
                .set(field(LeagueRapidDTO::getSeason_start), "2017-08-05")
                .set(field(LeagueRapidDTO::getSeason_end), "2018-05-05")
                .create();
    }

    private void assertLeagueNotByFieldName(League league, LeagueRapidDTO leagueRapidDTO) {
        assertThat(league)
                .extracting(
                        League::getId,
                        League::getLogoUrl,
                        League::getSeasonStart,
                        League::getSeasonEnd
                ).containsExactly(
                        leagueRapidDTO.getLeague_id(),
                        leagueRapidDTO.getLogo(),
                        DateTimeUtil.parseLocalDateTimeFromStartOfDate(leagueRapidDTO.getSeason_start()+"+03:00"),
                        DateTimeUtil.parseLocalDateTimeFromStartOfDate(leagueRapidDTO.getSeason_end()+"+03:00")
                );
    }
}