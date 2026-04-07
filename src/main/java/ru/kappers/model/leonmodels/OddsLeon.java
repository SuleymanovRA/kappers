package ru.kappers.model.leonmodels;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Спортивное событие с коэффицентами от букмекеров
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "odds_leon")
public class OddsLeon {
    @Id
    @Column(name = "odd_id", nullable = false, insertable = false, updatable = false)
    private long id;
    @Column(name = "name")
    @Size(max = 255)
    @NotBlank
    private String name;
    @ManyToOne
    @JoinColumn(name = "home_id")
    private CompetitorLeon home;
    @ManyToOne
    @JoinColumn(name = "away_id")
    private CompetitorLeon away;
    @Column(name = "kickoff")
    private LocalDateTime kickoff;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    @ManyToOne
    @JoinColumn(name = "league_id")
    private LeagueLeon league;
    @Column(name = "open")
    private boolean open;
    @Column(name = "url")
    @Size(max = 512)
    private String url;
    @OneToMany(mappedBy = "odd", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<RunnerLeon> runners = new ArrayList<>();
}
