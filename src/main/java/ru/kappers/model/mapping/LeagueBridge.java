package ru.kappers.model.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.kappers.model.catalog.League;
import ru.kappers.model.catalog.Team;
import ru.kappers.model.leonmodels.CompetitorLeon;
import ru.kappers.model.leonmodels.LeagueLeon;

import javax.persistence.*;

@Slf4j
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "league_bridge")
public class LeagueBridge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "rapid_league_id", nullable = false)
    private League rapidLeague;

    @OneToOne
    @JoinColumn(name = "leon_league_id", nullable = false)
    private LeagueLeon leonLeague;
}
