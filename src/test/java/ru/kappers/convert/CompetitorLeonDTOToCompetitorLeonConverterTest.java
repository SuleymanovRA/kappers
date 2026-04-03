package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.leonmodels.CompetitorLeon;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CompetitorLeonDTOToCompetitorLeonConverterTest extends UnitTest {
    @InjectMocks
    private CompetitorLeonDTOToCompetitorLeonConverterImpl converter;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (CompetitorLeonDTO dto : generatedCompetitorLeonDTOList()) {
            final CompetitorLeon result = converter.convert(dto);
            assertThat(result).isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("home_odds", "away_odds", "teamBridge")
                    .isEqualTo(dto);
        }
    }

    private List<CompetitorLeonDTO> generatedCompetitorLeonDTOList() {
        return Instancio.ofList(CompetitorLeonDTO.class)
                .size(2)
                .create();
    }

    @Test
    void convertNullable() {
        for (CompetitorLeonDTO dto : generatedCompetitorLeonDTOList()) {
            final CompetitorLeon result = converter.convertNullable(dto);
            assertThat(result).isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("home_odds", "away_odds", "teamBridge")
                    .isEqualTo(dto);
        }
    }

    @Test
    void convertNullableMustReturnNullIfParameterIsNull() {
        assertThat(converter.convertNullable(null)).isNull();
    }
}