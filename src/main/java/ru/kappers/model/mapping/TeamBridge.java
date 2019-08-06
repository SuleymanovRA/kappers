package ru.kappers.model.mapping;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.kappers.model.catalog.Team;
import ru.kappers.model.leonmodels.CompetitorLeon;

import javax.persistence.*;

/**
 * JPA сущность для маппинга команд с Rapid и Leon
 * @author Ashamaz Shomakhov
 */

@Slf4j
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_bridge")
public class TeamBridge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team rapidTeam;

    @OneToOne
    @JoinColumn(name = "competitor_id", nullable = false)
    private CompetitorLeon leonCompetitor;
}
