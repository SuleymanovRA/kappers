package ru.kappers.convert;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.leonmodels.CompetitorLeon;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

@Mapper(componentModel = "spring")
public abstract class CompetitorLeonDTOToCompetitorLeonConverter implements Converter<CompetitorLeonDTO, CompetitorLeon> {
    @Override
    public CompetitorLeon convert(CompetitorLeonDTO source) {
        checkArgument(nonNull(source), "source must not null");
        return convertNullable(source);
    }

    @Nullable
    public abstract CompetitorLeon convertNullable(@Nullable CompetitorLeonDTO source);
}
