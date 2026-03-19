package ru.kappers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kappers.logic.raitings.IRaiting;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Shoma on 29.09.2018.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kapper_info")
public class KapperInfo implements IRaiting, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private Integer id;

    @Column(name = "tokens")
    private Integer tokens;

    @Column(name = "bets")
    private Integer bets;

    @Column(name = "success_bets")
    private Integer successBets;

    @Column(name = "fail_bets")
    private Integer failBets;

    @Column(name = "blocked_tokens")
    private Integer blockedTokens;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @Override
    public int compareTo(KapperInfo o) {
        if (this.getTokens() < o.getTokens()) return 1;
        else if (this.getTokens() > o.getTokens()) return -1;
        else return 0;
    }
}
