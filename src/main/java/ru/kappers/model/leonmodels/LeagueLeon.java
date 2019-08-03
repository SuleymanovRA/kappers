package ru.kappers.model.leonmodels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.kappers.model.mapping.LeagueBridge;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;

@Slf4j
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "league_leon")
public class LeagueLeon {
    @Id
    @Column(name = "league_id", nullable = false, insertable = false, updatable = false)
    private long id;
    @Column(name = "name")
    @Size(max = 255)
    @NotBlank
    private String name;
    @Column(name = "sport")
    @Size(max = 128)
    private String sport;
    @Column(name = "url")
    @Size(max = 512)
    private String url;

    /**
     * маппер для связи с сущностью League
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToOne(mappedBy = "leonLeague")
    private LeagueBridge leagueBridge;

}
