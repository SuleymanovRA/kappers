package ru.kappers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Log4j
@Data
@Builder
@Entity
@Table(name = "currency_rate")
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private int id;
    @Column(name="date")
    private Date date;
    @Column(name="charcode")
    private String charCode;
    @Column(name="numcode")
    private String numCode;
    @Column(name="name")
    private String name;
    @Column(name="value")
    private double value;
    @Column(name="nominal")
    private int nominal;
}
