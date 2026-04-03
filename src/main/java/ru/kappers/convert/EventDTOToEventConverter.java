package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;
import ru.kappers.service.FixtureService;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

/**
 * Конвертер из {@link EventDTO} в {@link Event}
 */
@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT, injectionStrategy = CONSTRUCTOR)
public abstract class EventDTOToEventConverter extends BaseMapStructConverter<EventDTO, Event> {
    @Autowired
    protected FixtureService fixtureService;

    @Mapping(target = "fixture", expression = "java(fixtureService.getById(source.getF_id()))")
    @Override
    public abstract Event convert(EventDTO source);
}
