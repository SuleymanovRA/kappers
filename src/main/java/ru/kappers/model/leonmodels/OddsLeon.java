package ru.kappers.model.leonmodels;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "odds_leon")
public class OddsLeon {
    @Id
    @Column(name = "odd_id", nullable = false, insertable = true, updatable = false)
    private long id;
    @Column(name = "name")
    @Size(max = 255)
    @NotBlank
    private String name;
    @ManyToOne
    @JoinColumn(name = "home_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CompetitorLeon home;
    @ManyToOne
    @JoinColumn(name = "away_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CompetitorLeon away;
    @Column(name = "kickoff")
    private Timestamp kickoff;
    @Column(name = "last_updated")
    private Timestamp lastUpdated;
    @ManyToOne
    @JoinColumn(name = "league_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private LeagueLeon league;
    @Column(name = "open")
    private boolean open;
    @Column(name = "url")
    @Size(max = 512)
    private String url;
    @OneToMany(mappedBy = "odd", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.REMOVE})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<RunnerLeon> runners;
}
