package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.leon.LeagueLeonDTO;
import ru.kappers.model.leonmodels.LeagueLeon;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LeagueLeonDTOToLeagueLeonConverterTest extends UnitTest {
    @InjectMocks
    private LeagueLeonDTOToLeagueLeonConverter converter;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (LeagueLeonDTO leagueLeonDTO : generatedLeagueLeonDTOList()) {
            final LeagueLeon result = converter.convert(leagueLeonDTO);
            assertThat(result)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("sport", "leagueBridge")
                    .isEqualTo(leagueLeonDTO);
            assertThat(result)
                    .extracting(LeagueLeon::getSport)
                    .isEqualTo(leagueLeonDTO.getSport().getName());
        }
    }

    private List<LeagueLeonDTO> generatedLeagueLeonDTOList() {
        return Instancio.ofList(LeagueLeonDTO.class)
                .size(2)
                .create();
    }
}