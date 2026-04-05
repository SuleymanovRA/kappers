package ru.kappers.convert;


import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.leon.MarketLeonDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class MarketLeonDTOToMarketLeonConverterTest extends UnitTest {
    @InjectMocks
    private MarketLeonDTOToMarketLeonConverterImpl converter;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (MarketLeonDTO marketLeonDTO : generatedMarketLeonDTOList()) {
            final var marketLeon = converter.convert(marketLeonDTO);
            assertThat(marketLeon)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(marketLeonDTO);
        }
    }

    private List<MarketLeonDTO> generatedMarketLeonDTOList() {
        return Instancio.ofList(MarketLeonDTO.class)
                .size(2)
                .create();
    }
}