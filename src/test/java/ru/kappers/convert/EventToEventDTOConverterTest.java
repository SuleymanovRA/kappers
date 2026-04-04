package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.kappers.UnitTest;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

class EventToEventDTOConverterTest extends UnitTest {
    @InjectMocks
    private EventToEventDTOConverterImpl eventToEventDTOConverter;

    @Test
    void convert() {
        var event = Instancio.create(Event.class);
        var eventDTO = eventToEventDTOConverter.convert(event);
        assertThat(eventDTO).usingRecursiveComparison()
                .ignoringFields("f_id")
                .isEqualTo(event);
        assertThat(eventDTO)
                .extracting(EventDTO::getF_id)
                .isEqualTo(event.getFixture().getId());
    }

    @Test
    void convertIfFixtureIsEmpty() {
        var event = Instancio.of(Event.class)
                .ignore(field(Event::getFixture))
                .create();
        var eventDTO = eventToEventDTOConverter.convert(event);
        assertThat(eventDTO).usingRecursiveComparison()
                .ignoringFields("f_id")
                .isEqualTo(event);
        assertThat(eventDTO)
                .extracting(EventDTO::getF_id)
                .isEqualTo(EventDTO.EMPTY_FIXTURE_ID);
    }

    @Test
    void convertMustThrowExceptionIfParameterIsNull() {
        assertThatThrownBy(() -> eventToEventDTOConverter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}