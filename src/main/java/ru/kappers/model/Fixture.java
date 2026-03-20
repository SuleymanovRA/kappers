package ru.kappers.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JPA-сущность Спортивное событие
 */
@Slf4j
@Data
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixtures")
public class Fixture implements Serializable, Comparable<Fixture> {
    @Id
    @Column(name = "fixture_id",nullable = false, insertable = false, updatable = false)
    private Integer id;
    @Column(name="event_timestamp")
    private Long eventTimestamp;
    @Column(name="event_date")
    private LocalDateTime eventDate;
    @Column(name="league_id")
    private Integer leagueId;
    @Column(name="round")
    private String round;
    @Column(name="home_team_id")
    private Integer homeTeamId;
    @Column(name="away_team_id")
    private Integer awayTeamId;
    @Column(name="home_team")
    private String homeTeam;
    @Column(name="away_team")
    private String awayTeam;
    /** Статус */
    @Column(name="status")
    @Convert(converter = StatusConverter.class)
    private Status status;
    /** Сокращенный статус */
    @Column(name="status_short")
    @Convert(converter = ShortStatusConverter.class)
    private ShortStatus statusShort;
    @Column(name="goals_home_team")
    private Integer goalsHomeTeam;
    @Column(name="goals_away_team")
    private Integer goalsAwayTeam;
    @Column(name="halftime_score")
    private String halftimeScore;
    @Column(name="final_score")
    private String finalScore;
    @Column(name="penalty")
    private String penalty;
    @Column(name="elapsed")
    private Integer elapsed;
    @Column(name="first_half_start")
    private Long firstHalfStart;
    @Column(name="second_half_start")
    private Long secondHalfStart;
    @OneToMany(mappedBy = "fixture")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private List<Event> events = new ArrayList<>();

    public String getProperty(String propName) throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Field declaredField = this.getClass().getDeclaredField(propName);
        Class<?> targetType = declaredField.getType();
        Object objectValue = targetType.getDeclaredConstructor().newInstance();
        Object value = declaredField.get(objectValue);
        return String.valueOf(value);
    }

    @Override
    public int compareTo(Fixture o) {
        return eventTimestamp.compareTo(o.eventTimestamp);
    }

    /**
     * Статус спортивного события
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public enum Status {
        /** Спортивное событие не началось */
        NOT_STARTED("Not Started"),
        /** Матч закончен */
        MATCH_FINISHED("Match Finished");

        /** Значение статуса */
        private final String value;

        /**
         * Найти статус по значению
         * @param value значение статуса
         * @return найденный статус или {@literal null}
         */
        @Nullable
        public static Status byValue(String value) {
            for (Status val : Status.values()) {
                if (Objects.equals(value, val.value)) {
                    return val;
                }
            }
            return null;
        }
    }

    /**
     * Сокращенный статус спортивного события
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public enum ShortStatus {
        /** Спортивное событие не началось */
        NOT_STARTED("NS"),
        /** Матч закончен */
        MATCH_FINISHED("FT");

        /** Значение статуса */
        private final String value;

        /**
         * Найти статус по значению
         * @param value значение статуса
         * @return найденный статус или {@literal null}
         */
        @Nullable
        public static ShortStatus byValue(String value) {
            for (ShortStatus val : ShortStatus.values()) {
                if (Objects.equals(value, val.value)) {
                    return val;
                }
            }
            return null;
        }
    }

    @Converter
    public static class StatusConverter implements AttributeConverter<Status, String> {
        @Override
        public String convertToDatabaseColumn(Status attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getValue();
        }

        @Override
        public Status convertToEntityAttribute(String dbData) {
            return Status.byValue(dbData);
        }
    }

    @Converter
    public static class ShortStatusConverter implements AttributeConverter<ShortStatus, String> {
        @Override
        public String convertToDatabaseColumn(ShortStatus attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getValue();
        }

        @Override
        public ShortStatus convertToEntityAttribute(String dbData) {
            return ShortStatus.byValue(dbData);
        }
    }
}