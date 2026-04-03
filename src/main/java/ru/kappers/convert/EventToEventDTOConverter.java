package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kappers.model.Event;
import ru.kappers.model.dto.EventDTO;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class EventToEventDTOConverter extends BaseMapStructConverter<Event, EventDTO> {
    @Mapping(target = "f_id", expression = "java(source.getFixture() != null ? source.getFixture().getId() : EventDTO.EMPTY_FIXTURE_ID)")
    @Override
    public abstract EventDTO convert(Event source);
}
