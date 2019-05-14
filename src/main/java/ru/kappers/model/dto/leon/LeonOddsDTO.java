package ru.kappers.model.dto.leon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO сущность спортивных событий с коэфицентами от букмекеров
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeonOddsDTO {
    private long id;
    private String name;
    /** участники соревнования */
    private List<CompetitorDTO> competitors;
    private long kickoff;
    /** это дата/время последнего изменения? */
    private long lastUpdated;
    /** лига */
    private LeagueDTO league;
    private boolean open;
    private int marketsCount;
    private String url;
    private List<MarketDTO> markets;

}
