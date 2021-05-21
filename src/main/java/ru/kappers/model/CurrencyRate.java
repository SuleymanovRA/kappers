package ru.kappers.model;

import lombok.Builder;
import lombok.Generated;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@Entity
@Table(name = "currency_rate")
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

    @Generated
    public CurrencyRate() {
    }

    @Generated
    public CurrencyRate(int id, LocalDate date, String charCode, String numCode, String name, BigDecimal value, int nominal) {
        this.id = id;
        this.date = date;
        this.charCode = charCode;
        this.numCode = numCode;
        this.name = name;
        this.value = value;
        this.nominal = nominal;
    }

    @Generated
    public int getId() {
        return this.id;
    }

    @Generated
    public LocalDate getDate() {
        return this.date;
    }

    @Generated
    public String getCharCode() {
        return this.charCode;
    }

    @Generated
    public String getNumCode() {
        return this.numCode;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public BigDecimal getValue() {
        return this.value;
    }

    @Generated
    public int getNominal() {
        return this.nominal;
    }

    @Generated
    public void setId(int id) {
        this.id = id;
    }

    @Generated
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Generated
    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    @Generated
    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Generated
    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRate that = (CurrencyRate) o;
        return id == that.id
                && nominal == that.nominal
                && Objects.equals(date, that.date)
                && Objects.equals(charCode, that.charCode)
                && Objects.equals(numCode, that.numCode)
                && Objects.equals(name, that.name)
                && Objects.equals(value, that.value);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(id, date, charCode, numCode, name, value, nominal);
    }

    @Generated
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CurrencyRate{");
        sb.append("id=").append(id);
        sb.append(", date=").append(date);
        sb.append(", charCode='").append(charCode).append('\'');
        sb.append(", numCode='").append(numCode).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", value=").append(value);
        sb.append(", nominal=").append(nominal);
        sb.append('}');
        return sb.toString();
    }
}
