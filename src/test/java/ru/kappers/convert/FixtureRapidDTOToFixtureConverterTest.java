package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.Fixture;
import ru.kappers.model.dto.rapidapi.FixtureRapidDTO;
import ru.kappers.util.DateTimeUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

class FixtureRapidDTOToFixtureConverterTest extends UnitTest {
    @InjectMocks
    private FixtureRapidDTOToFixtureConverterImpl converter;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (FixtureRapidDTO fixtureRapidDTO : generatedFixtureRapidDTOList()) {
            final Fixture fixture = converter.convert(fixtureRapidDTO);
            assertThat(fixture)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("id", "eventTimestamp", "eventDate", "leagueId", "homeTeamId",
                            "awayTeamId", "status", "statusShort", "halftimeScore", "finalScore", "events")
                    .isEqualTo(fixtureRapidDTO);
            assertNotByFieldNames(fixture, fixtureRapidDTO);
        }
    }

    private List<FixtureRapidDTO> generatedFixtureRapidDTOList() {
        return Instancio.ofList(FixtureRapidDTO.class)
                .size(2)
                .set(field(FixtureRapidDTO::getEvent_date), "2020-12-02T10:15:30+01:00")
                .create();
    }

    private void assertNotByFieldNames(Fixture fixture, FixtureRapidDTO fixtureRapidDTO) {
        assertThat(fixture)
                .extracting(
                        Fixture::getId,
                        Fixture::getEventTimestamp,
                        Fixture::getEventDate,
                        Fixture::getLeagueId,
                        Fixture::getHomeTeamId,
                        Fixture::getAwayTeamId,
                        Fixture::getStatus,
                        Fixture::getStatusShort,
                        Fixture::getHalftimeScore,
                        Fixture::getFinalScore
                ).containsExactly(
                        fixtureRapidDTO.getFixture_id(),
                        fixtureRapidDTO.getEvent_timestamp(),
                        DateTimeUtil.parseLocalDateTimeFromZonedDateTime(fixtureRapidDTO.getEvent_date()),
                        fixtureRapidDTO.getLeague_id(),
                        fixtureRapidDTO.getHomeTeam_id(),
                        fixtureRapidDTO.getAwayTeam_id(),
                        Fixture.Status.byValue(fixtureRapidDTO.getStatus()),
                        Fixture.ShortStatus.byValue(fixtureRapidDTO.getStatusShort()),
                        fixtureRapidDTO.getHalftime_score(),
                        fixtureRapidDTO.getFinal_score()
                );
    }
}