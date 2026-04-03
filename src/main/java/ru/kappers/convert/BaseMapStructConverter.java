package ru.kappers.convert;

import org.mapstruct.BeforeMapping;
import org.springframework.core.convert.converter.Converter;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

public abstract class BaseMapStructConverter<S, T> implements Converter<S, T> {
    @BeforeMapping
    protected void beforeConvert(S source) {
        checkArgument(nonNull(source), "source must not null");
    }
}
