package ru.kappers.convert;

import org.mapstruct.BeforeMapping;
import org.springframework.core.convert.converter.Converter;

import static java.util.Objects.isNull;

public abstract class BaseMapStructConverter<S, T> implements Converter<S, T> {
    @BeforeMapping
    protected void beforeConvert(S source) {
        if (isNull(source)) {
          throw new IllegalArgumentException("source must not null");
        }
    }
}
