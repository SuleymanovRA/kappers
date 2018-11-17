package ru.kappers.model;


import lombok.*;
import lombok.extern.log4j.Log4j;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;

@Log4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fixtures")
public class Fixture implements Serializable, Comparable {

//    @Id
////    @GeneratedValue(strategy = GenerationType.AUTO)
////    @Column(name = "id", nullable = false, insertable = false, updatable = false)
////    @EqualsAndHashCode.Exclude
////    private int Id;
    @Id
    @Column(name = "fixture_id",nullable = false, insertable = false, updatable = false)
    Integer fixture_id;
    @Column(name="event_timestamp")
    Long event_timestamp;
    @Column(name="event_date")
    Timestamp event_date;
    @Column(name="league_id")
    Integer league_id;
    @Column(name="round")
    String round;
    @Column(name="homeTeam_id")
    Integer homeTeam_id;
    @Column(name="awayTeam_id")
    Integer awayTeam_id;
    @Column(name="homeTeam")
    String homeTeam;
    @Column(name="awayTeam")
    String awayTeam;
    @Column(name="status")
    String status;
    @Column(name="statusShort")
    String statusShort;
    @Column(name="goalsHomeTeam")
    Integer goalsHomeTeam;
    @Column(name="goalsAwayTeam")
    Integer goalsAwayTeam;
    @Column(name="halftime_score")
    String halftime_score;
    @Column(name="final_score")
    String final_score;
    @Column(name="penalty")
    String penalty;
    @Column(name="elapsed")
    Integer elapsed;
    @Column(name="firstHalfStart")
    Long firstHalfStart;
    @Column(name="secondHalfStart")
    Long secondHalfStart;

    public String getProperty(String propName) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field declaredField = this.getClass().getDeclaredField(propName);
        Class<?> targetType = declaredField.getType();
        Object objectValue = targetType.newInstance();
        Object value = declaredField.get(objectValue);
        return String.valueOf(value);
    }
   @Override
    public int compareTo(Object o) {
        return (getEvent_timestamp()).compareTo(((Fixture) o).getEvent_timestamp());
    }
}