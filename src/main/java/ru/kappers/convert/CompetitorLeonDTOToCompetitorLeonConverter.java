package ru.kappers.convert;

import org.mapstruct.Mapper;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.leonmodels.CompetitorLeon;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class CompetitorLeonDTOToCompetitorLeonConverter extends BaseMapStructConverter<CompetitorLeonDTO, CompetitorLeon> {
}
