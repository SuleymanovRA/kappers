package ru.kappers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.NaturalId;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.money.Money;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private int id;

    @NaturalId
    @Column(name = "user_name")
    //  @NotEmpty(message = "*Please provide user name")
    private String userName;

    @Column(name = "password")
    @ToString.Exclude
    //  @JsonIgnore
    //   @Length(min = 5, message = "*Your password must have at least 5 characters")
    //  @NotEmpty(message = "*Please provide your password")
    private String password;

    @Column(name = "name")
    private String name;
    @Column(name = "email")
//    @Email(message = "*Please provide a valid Email")
    //   @NotEmpty(message = "*Please provide an email")
    private String email;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "date_of_registration")
    private LocalDateTime dateOfRegistration;

    @Column(name = "isblocked", nullable = false)
    private boolean isblocked;

    @Column(name = "lang")
    private String lang;

    /**
     * Роль
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "kapper")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private List<Event> events = new ArrayList<>();

    /** Баланс с валютой */
    @Columns(columns = { @Column(name = "currency"), @Column(name = "balance") })
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmountAndCurrency")
    private Money balance;

    //
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(name = "stat", joinColumns = @JoinColumn(name = "user_id"))
//    private List<Stat> stat;


//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinTable(name = "personal_info", joinColumns = @JoinColumn(name = "user_id"))
//    private PersonalInfo personalInfo;

//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinTable(name = "history", joinColumns = @JoinColumn(name = "player_id"))
//    private Set<History> entity;

    public boolean hasRole(int roleId) {
        return role.getId() == roleId;
    }

    public boolean hasNoRole(String roleName) {
        return !hasRole(roleName);
    }

    public boolean hasRole(String roleName) {
        return Objects.equals(role.getName(), roleName);
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne (mappedBy = "user")
    private KapperInfo kapperInfo;
}