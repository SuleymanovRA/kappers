package ru.kappers.model.comparing;

import lombok.experimental.UtilityClass;
import ru.kappers.model.Fixture;

import java.util.Comparator;

@UtilityClass
public class FixtureComparators {
    public static Comparator<Fixture> byId() {
        return Comparator.comparingInt(Fixture::getId);
    }

    public static Comparator<Fixture> byEventTimestamp() {
        return Comparator.comparing(Fixture::getEventTimestamp);
    }

    public static Comparator<Fixture> common() {
        return Comparator.comparingInt(Fixture::getId)
                .thenComparing(Fixture::getEventTimestamp)
                .thenComparing(Fixture::getEventDate)
                .thenComparing(Fixture::getLeagueId)
                .thenComparing(Fixture::getAwayTeam)
                .thenComparing(Fixture::getAwayTeamId)
                .thenComparing(Fixture::getHomeTeam)
                .thenComparing(Fixture::getHomeTeamId);
    }
}
