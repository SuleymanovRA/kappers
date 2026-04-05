package ru.kappers.convert;

import org.mapstruct.Mapper;
import ru.kappers.model.dto.leon.MarketLeonDTO;
import ru.kappers.model.leonmodels.MarketLeon;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring", nullValueMappingStrategy = RETURN_DEFAULT)
public abstract class MarketLeonDTOToMarketLeonConverter extends BaseMapStructConverter <MarketLeonDTO, MarketLeon> {
}