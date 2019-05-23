package ru.kappers.model.leonmodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "market_leon")
public class MarketLeon {
    @Id
    @Column(name = "market_id", nullable = false, insertable = false, updatable = false)
    private long id;
    @Column(name = "name")
    @Size(max = 255)
    @NotBlank
    private String name;
    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL)
    private List<RunnerLeon> runners;
    @Column(name = "open")
    private boolean open;
    @Column(name = "family")
    @Size(max = 255)
    private String family;
    @ManyToOne
    @JoinColumn(name = "odd_id", nullable = false)
    private  OddsLeon odd;
}
