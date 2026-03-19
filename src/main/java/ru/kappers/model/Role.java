package ru.kappers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA-сущность для роли
 */
@Slf4j
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = true, updatable = false)
    private int id;
    @Column(name = "name", nullable = false, unique=true)
    private String name;
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @OneToMany(mappedBy = "role")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    /**
     * Класс констант имен ролей. Список всех имен хранится в поле {@link Names#LIST}
     */
    public static final class Names {
        /** Администратор */
        public static final String ADMIN = "ROLE_ADMIN";
        /** Пользователь */
        public static final String USER = "ROLE_USER";
        /** Kapper (спортивный аналитик) */
        public static final String KAPPER = "ROLE_KAPPER";
        /** Анонимный */
        public static final String ANONYMOUS = "ROLE_ANONYMOUS";

        /** Список всех имен ролей */
        public static final List<String> LIST = ImmutableList.of(ADMIN, USER, KAPPER, ANONYMOUS);
    }
}
