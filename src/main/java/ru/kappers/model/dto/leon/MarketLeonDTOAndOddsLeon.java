package ru.kappers.model.dto.leon;

import lombok.Builder;
import lombok.Value;
import ru.kappers.model.leonmodels.OddsLeon;

@Builder
@Value
public class MarketLeonDTOAndOddsLeon {
    private MarketLeonDTO marketLeonDTO;
    private OddsLeon oddsLeon;
}
