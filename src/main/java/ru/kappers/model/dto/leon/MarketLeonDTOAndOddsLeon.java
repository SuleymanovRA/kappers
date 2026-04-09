package ru.kappers.model.dto.leon;

import lombok.Builder;
import ru.kappers.model.leonmodels.OddsLeon;

@Builder
public record MarketLeonDTOAndOddsLeon(
    MarketLeonDTO marketLeonDTO,
    OddsLeon oddsLeon
) {}
