package ru.kappers.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.kappers.model.mapping.LeagueBridge;
import ru.kappers.model.mapping.TeamBridge;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * JPA-сущность для лиги
 */
@Slf4j
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "league")
public class League {
    @Id
    @Column(name = "league_id",nullable = false, insertable = false, updatable = false)
    private Integer id;
    /** название лиги */
    @Column(name="name")
    @Size(max = 255)
    @NotBlank
    private String name;
    @Column(name="country")
    @Size(max = 180)
    private String country;
    @Column(name="season")
    @Size(max = 9)
    private String season;
    @Column(name="season_start")
    private LocalDateTime seasonStart;
    @Column(name="season_end")
    private LocalDateTime seasonEnd;
    @Column(name="logo")
    @Size(max = 512)
    private String logoUrl;

    /**
     * маппер для связи с сущностью LeonLeague
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToOne(mappedBy = "rapidLeague")
    private LeagueBridge leagueBridge;
}
