package ru.kappers.convert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import ru.kappers.model.dto.leon.MarketLeonDTO;
import ru.kappers.model.dto.leon.RunnerLeonDTO;
import ru.kappers.model.leonmodels.MarketLeon;
import ru.kappers.model.leonmodels.RunnerLeon;
import ru.kappers.service.MarketLeonService;
import ru.kappers.service.RunnerLeonService;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MarketLeonDTOToRunnerLeonListConverter implements Converter<MarketLeonDTO, List<RunnerLeon>> {
    private final MarketLeonService marketService;
    private final RunnerLeonService runnerService;
    private final ConversionService conversionService;

    @Autowired
    public MarketLeonDTOToRunnerLeonListConverter(MarketLeonService marketService, RunnerLeonService runnerService, @Lazy ConversionService conversionService) {
        this.marketService = marketService;
        this.runnerService = runnerService;
        this.conversionService = conversionService;
    }

    @Override
    public List<RunnerLeon> convert(@Nullable MarketLeonDTO source) {
        if (source == null) {
            return new ArrayList<>();
        }
        final MarketLeonDTO marketDTO = source;
        final List<RunnerLeon> runners = new ArrayList<>(marketDTO.getRunners().size());

        MarketLeon market = marketService.getByName(marketDTO.getName());
        if (market == null) {
            market = marketService.save(conversionService.convert(marketDTO, MarketLeon.class));
        }

        for (RunnerLeonDTO runnerDTO : marketDTO.getRunners()) {
            runners.add(getRunner(runnerDTO, market));
        }
        return runners;
    }

    private RunnerLeon getRunner(RunnerLeonDTO runnerDTO, MarketLeon market) {
        RunnerLeon byId = runnerService.getById(runnerDTO.getId());
        if (byId == null) {
            return RunnerLeon.builder()
                    .id(runnerDTO.getId())
                    .name(runnerDTO.getName())
                    .price(runnerDTO.getPrice())
                    .open(runnerDTO.isOpen())
                    .market(market)
                    .tags(runnerDTO.getTags() != null ? runnerDTO.getTags().toString() : "")
                    .build();
        } else{
            byId.setPrice(runnerDTO.getPrice());
            byId.setOpen(runnerDTO.isOpen());
            return byId;
        }

    }
}