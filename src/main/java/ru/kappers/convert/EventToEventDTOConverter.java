package ru.kappers.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

@Service
public class EventToEventDTOConverter implements Converter<Event, EventDTO> {
    @Override
    public EventDTO convert(Event source) {
        checkArgument(nonNull(source), "source must not null");
        return EventDTO.builder()
                .f_id(getFixtureIdOrEmpty(source))
                .outcome(source.getOutcome())
                .coefficient(source.getCoefficient())
                .tokens(source.getTokens())
                .price(source.getPrice())
                .build();
    }

    private int getFixtureIdOrEmpty(Event source) {
        if (Objects.isNull(source.getFixture())) {
            return EventDTO.EMPTY_FIXTURE_ID;
        }
        return source.getFixture().getId();
    }
}
