package ru.kappers.convert;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class EventToEventDTOConverterTest {
    private EventToEventDTOConverter eventToEventDTOConverter = new EventToEventDTOConverter();

    @Test
    void convert() {
        var event = Instancio.create(Event.class);
        var eventDTO = eventToEventDTOConverter.convert(event);
        Assertions.assertThat(eventDTO).usingRecursiveComparison()
                .ignoringFields("f_id")
                .isEqualTo(event);
        Assertions.assertThat(eventDTO)
                .extracting(EventDTO::getF_id)
                .isEqualTo(event.getFixture().getId());
    }

    @Test
    void convertIfFixtureIsEmpty() {
        var event = Instancio.of(Event.class)
                .ignore(field(Event::getFixture))
                .create();
        var eventDTO = eventToEventDTOConverter.convert(event);
        Assertions.assertThat(eventDTO).usingRecursiveComparison()
                .ignoringFields("f_id")
                .isEqualTo(event);
        Assertions.assertThat(eventDTO)
                .extracting(EventDTO::getF_id)
                .isEqualTo(EventDTO.EMPTY_FIXTURE_ID);
    }
}