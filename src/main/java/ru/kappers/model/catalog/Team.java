package ru.kappers.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.kappers.model.mapping.TeamBridge;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * JPA-сущность для команды
 */
@Slf4j
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team")
public class Team {

    @Id
    @Column(name = "team_id", nullable = false, insertable = false, updatable = false)
    private Integer id;

    /**
     * название команды
     */
    @Column(name = "name")
    @Size(max = 255)
    @NotBlank
    private String name;

    @Column(name = "code")
    @Size(max = 8)
    private String code;

    @Column(name = "logo")
    @Size(max = 512)
    private String logo;

    /**
     * маппер для связи с сущностью CompetitorLeon
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "rapidTeam")
    private List<TeamBridge> teamBridge;
}
