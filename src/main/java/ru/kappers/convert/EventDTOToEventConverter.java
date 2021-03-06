package ru.kappers.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.service.FixtureService;

import javax.annotation.Nullable;

/**
 * Конвертер из {@link EventDTO} в {@link Event}
 */
@Service
public class EventDTOToEventConverter implements Converter<EventDTO, Event> {

    private final FixtureService fixtureService;

    @Autowired
    public EventDTOToEventConverter(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @Nullable
    @Override
    public Event convert(@Nullable EventDTO source) {
        if (source == null) {
            return null;
        }
        final Event event = Event.builder()
                .fixture(fixtureService.getById(source.getF_id()))
                .outcome(source.getOutcome())
                .coefficient(source.getCoefficient())
                .tokens(source.getTokens())
                .price(source.getPrice())
                .build();
        return event;
    }
}
