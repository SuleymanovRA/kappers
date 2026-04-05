package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.rapidapi.TeamRapidDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.kappers.assertion.Assertions.assertThat;


class TeamRapidDTOToTeamConverterTest extends UnitTest {
    @InjectMocks
    private TeamRapidDTOToTeamConverterImpl converter;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (TeamRapidDTO teamRapidDTO : generatedTeamRapidDTOList()) {
            final var team = converter.convert(teamRapidDTO);
            assertThat(team)
                    .isNotNull()
                    .hasId(teamRapidDTO.getTeam_id())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "teamBridge")
                    .isEqualTo(teamRapidDTO);
        }
    }

    private List<TeamRapidDTO> generatedTeamRapidDTOList() {
        return Instancio.ofList(TeamRapidDTO.class)
                .size(2)
                .create();
    }
}