package ru.kappers.model.utilmodel;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import ru.kappers.model.Fixture;

import java.util.EnumMap;
import java.util.Map;
//Пока что генерируем случайные коэифценты
@Data
@Log4j
public class Odds {
    private Fixture fixture;
    private Map<Outcomes, Double> odds;

    public Odds(Fixture fixture) {
        this.fixture = fixture;
        odds =  new EnumMap<>(Outcomes.class);
        for (Outcomes outome: Outcomes.values()) {
            odds.put(outome, getRandomCoeff());
        }
    }

    private static Double getRandomCoeff(){
        return (Math.random() * 4);
    }
}
