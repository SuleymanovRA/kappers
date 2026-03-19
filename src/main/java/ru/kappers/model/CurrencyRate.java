package ru.kappers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "currency_rate")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private int id;
    @Column(name="date", columnDefinition = "DATE")
    private LocalDate date;
    @Column(name="charcode")
    private String charCode;
    @Column(name="numcode")
    private String numCode;
    @Column(name="name")
    private String name;
    @Column(name="value")
    private BigDecimal value;
    @Column(name="nominal")
    private int nominal;
}
