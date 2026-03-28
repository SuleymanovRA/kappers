package ru.kappers.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kappers.model.utilmodel.Outcomes;

import java.math.BigDecimal;

/**
 * DTO для Заведенного события
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    /** Fixture ID (или {@linkplain #EMPTY_FIXTURE_ID}, если пустой ID) */
    private int f_id;
    /** возможный исход */
    private Outcomes outcome;
    /** коэффициент */
    private BigDecimal coefficient;
    /** сколько токенов ставим */
    private int tokens;
    /** какую цену назначаем за открытие евента пользователями */
    private BigDecimal price;

    public static final int EMPTY_FIXTURE_ID = -1;
}
