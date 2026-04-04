package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kappers.model.dto.leon.LeagueLeonDTO;
import ru.kappers.model.leonmodels.LeagueLeon;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class LeagueLeonDTOToLeagueLeonConverter extends BaseMapStructConverter<LeagueLeonDTO, LeagueLeon> {
    @Mapping(source = "sport.name", target = "sport")
    @Override
    public abstract LeagueLeon convert(LeagueLeonDTO source);
}
