package ru.kappers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kappers.model.CurrencyRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CurrRateRepository extends JpaRepository<CurrencyRate, Integer> {
   boolean existsCurrencyRateByDateAndCharCode(LocalDate date, String currLiteral);
   Optional<CurrencyRate> getCurrencyRateByDateAndCharCode(LocalDate date, String currLiteral);
   void deleteAll();
   List<CurrencyRate> getAllByDate(LocalDate date);


}
