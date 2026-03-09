package ru.kappers.convert;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.service.FixtureService;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

/**
 * Конвертер из {@link EventDTO} в {@link Event}
 */
@Service
@RequiredArgsConstructor
public class EventDTOToEventConverter implements Converter<EventDTO, Event> {
    private final FixtureService fixtureService;

    @Nullable
    @Override
    public Event convert(EventDTO source) {
        checkArgument(nonNull(source), "source must not null");
        return Event.builder()
                .fixture(fixtureService.getById(source.getF_id()))
                .outcome(source.getOutcome())
                .coefficient(source.getCoefficient())
                .tokens(source.getTokens())
                .price(source.getPrice())
                .build();
    }
}
