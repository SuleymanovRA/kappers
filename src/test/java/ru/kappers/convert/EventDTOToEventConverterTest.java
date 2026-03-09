package ru.kappers.convert;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.kappers.UnitTest;
import ru.kappers.model.Event;
import ru.kappers.model.Fixture;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.model.utilmodel.Outcomes;
import ru.kappers.service.FixtureService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class EventDTOToEventConverterTest extends UnitTest {
    @InjectMocks
    private EventDTOToEventConverter converter;
    @Mock
    private FixtureService fixtureService;

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convert() {
        for (EventDTO dto : createEventDTOList()) {
            reset(fixtureService);
            final Fixture fixture = mock(Fixture.class);
            when(fixtureService.getById(dto.getF_id())).thenReturn(fixture);

            final Event result = converter.convert(dto);

            assertAll(
                    () -> assertThat(result).isNotNull()
                            .usingRecursiveComparison()
                            .ignoringFields("id", "kapper", "fixture", "isClosed")
                            .isEqualTo(dto),
                    () -> assertThat(result.getFixture()).isEqualTo(fixture)
            );
            verify(fixtureService).getById(dto.getF_id());
        }
    }

    private List<EventDTO> createEventDTOList() {
        return Arrays.asList(
                EventDTO.builder()
                        .f_id(1)
                        .outcome(Outcomes.HOMETEAMWIN)
                        .coefficient(BigDecimal.TEN)
                        .tokens(200)
                        .price(new BigDecimal("35.5"))
                        .build(),
                EventDTO.builder()
                        .f_id(2)
                        .outcome(Outcomes.GUESTTEAMWIN)
                        .coefficient(BigDecimal.ONE)
                        .tokens(300)
                        .price(new BigDecimal("15.1"))
                        .build()
        );
    }
}