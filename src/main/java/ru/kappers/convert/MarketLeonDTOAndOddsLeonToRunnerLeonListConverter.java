package ru.kappers.convert;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.dto.leon.MarketLeonDTO;
import ru.kappers.model.dto.leon.MarketLeonDTOAndOddsLeon;
import ru.kappers.model.dto.leon.RunnerLeonDTO;
import ru.kappers.model.leonmodels.MarketLeon;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.model.leonmodels.RunnerLeon;
import ru.kappers.service.MarketLeonService;
import ru.kappers.service.RunnerLeonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
public class MarketLeonDTOAndOddsLeonToRunnerLeonListConverter implements Converter<MarketLeonDTOAndOddsLeon, List<RunnerLeon>> {
    private final MarketLeonService marketService;
    private final RunnerLeonService runnerService;
    private final ConversionService conversionService;

    @Autowired
    public MarketLeonDTOAndOddsLeonToRunnerLeonListConverter(MarketLeonService marketService,
        RunnerLeonService runnerService, @Lazy ConversionService conversionService) {
        this.marketService = marketService;
        this.runnerService = runnerService;
        this.conversionService = conversionService;
    }

    @Builder
    private record RunnerSearchParameters(
            RunnerLeonDTO runnerDTO,
            MarketLeon market,
            OddsLeon odd) {
    }

    @Override
    public List<RunnerLeon> convert(MarketLeonDTOAndOddsLeon source) {
        checkArgument(nonNull(source), "source must not null");
        final MarketLeonDTO marketDTO = source.marketLeonDTO();
        MarketLeon market = getMarket(marketDTO);
        final List<RunnerLeon> runners = new ArrayList<>(marketDTO.getRunners().size());
        for (RunnerLeonDTO runnerDTO : marketDTO.getRunners()) {
            runners.add(getRunner(RunnerSearchParameters.builder()
                    .runnerDTO(runnerDTO)
                    .market(market)
                    .odd(source.oddsLeon())
                    .build()));
        }
        return runners;
    }

    private void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private MarketLeon getMarket(MarketLeonDTO marketDTO) {
        return findMarket(marketDTO)
                .orElse(savedMarket(marketDTO));
    }

    private Optional<MarketLeon> findMarket(MarketLeonDTO marketDTO) {
        return Optional.ofNullable(marketService.getByName(marketDTO.getName()));
    }

    private MarketLeon savedMarket(MarketLeonDTO marketDTO) {
        return marketService.save(conversionService.convert(marketDTO, MarketLeon.class));
    }

    private RunnerLeon getRunner(RunnerSearchParameters searchParameters) {
        return findRunner(searchParameters)
                .map(runnerLeon -> updatedRunnerLeon(runnerLeon, searchParameters.runnerDTO))
                .orElse(newRunnerLeon(searchParameters));
    }

    private Optional<RunnerLeon> findRunner(RunnerSearchParameters searchParameters) {
        return Optional.ofNullable(
                runnerService.getFirstByMarketAndOddAndName(searchParameters.market.getId(),
                        searchParameters.odd.getId(), searchParameters.runnerDTO.getName()));
    }

    private RunnerLeon updatedRunnerLeon(RunnerLeon runnerLeon, RunnerLeonDTO runnerDTO) {
        return runnerLeon.toBuilder()
                .open(runnerDTO.isOpen())
                .price(runnerDTO.getPrice())
                .build();
    }

    private RunnerLeon newRunnerLeon(RunnerSearchParameters searchParameters) {
        RunnerLeonDTO runnerDTO = searchParameters.runnerDTO;
        return RunnerLeon.builder()
                .name(runnerDTO.getName())
                .open(runnerDTO.isOpen())
                .tags(runnerDTO.getTags() != null ? runnerDTO.getTags().toString() : "")
                .price(runnerDTO.getPrice())
                .market(searchParameters.market)
                .odd(searchParameters.odd)
                .build();
    }
}