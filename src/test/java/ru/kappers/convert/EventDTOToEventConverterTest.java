package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.kappers.UnitTest;
import ru.kappers.model.Event;
import ru.kappers.model.Fixture;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.service.FixtureService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
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
        for (EventDTO dto : generateEventDTOList()) {
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

    private List<EventDTO> generateEventDTOList() {
        return Instancio.ofList(EventDTO.class)
                .size(2)
                .generate(field(EventDTO::getF_id), gen -> gen.intSeq().start(1))
                .create();
    }
}