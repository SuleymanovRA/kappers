package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.rapidapi.TeamRapidDTO;

import java.util.List;

import static ru.kappers.assertion.Assertions.assertThat;


class TeamRapidDTOToTeamConverterTest extends UnitTest {
    @InjectMocks
    private TeamRapidDTOToTeamConverter converter;

    @Test
    void convertMustReturnNullIfParameterIsNull() {
        assertThat(converter.convert(null)).isNull();
    }

    @Test
    void convert() {
        for (TeamRapidDTO dto : generatedTeamRapidDTOList()) {
            final var team = converter.convert(dto);
            assertThat(team)
                    .isNotNull()
                    .hasId(dto.getTeam_id())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "teamBridge")
                    .isEqualTo(dto);
        }
    }

    private List<TeamRapidDTO> generatedTeamRapidDTOList() {
        return Instancio.ofList(TeamRapidDTO.class)
                .size(2)
                .create();
    }
}