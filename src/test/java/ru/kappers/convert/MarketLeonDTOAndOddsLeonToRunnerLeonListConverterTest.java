package ru.kappers.convert;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.convert.ConversionService;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.leon.MarketLeonDTO;
import ru.kappers.model.dto.leon.MarketLeonDTOAndOddsLeon;
import ru.kappers.model.leonmodels.MarketLeon;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.model.leonmodels.RunnerLeon;
import ru.kappers.service.MarketLeonService;
import ru.kappers.service.RunnerLeonService;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


class MarketLeonDTOAndOddsLeonToRunnerLeonListConverterTest extends UnitTest {
    @InjectMocks
    private MarketLeonDTOAndOddsLeonToRunnerLeonListConverter converter;
    @Mock
    private MarketLeonService marketService;
    @Mock
    private ConversionService conversionService;
    @Mock
    private RunnerLeonService runnerService;

    private final Comparator<Double> DOUBLE_COMPARATOR = (d1, d2) -> {
        double precision = 0.01;
        return Math.abs(d1 - d2) <= precision ? 0 : 1;
    };
    private MarketLeonDTOToMarketLeonConverter marketLeonDTOToMarketLeonConverter = Mappers.getMapper(MarketLeonDTOToMarketLeonConverter.class);

    @Test
    void convertMustReturnImptyListIfParameterIsNull() {
        assertThat(converter.convert(null))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void convert() {
        prepareMarketLeonDTOConvertion();
        prepareMarketLeonSaving();

        var marketLeonDTOAndOddsLeon = generatedMarketLeonDTOAndOddsLeon();
        var runnerLeonList = converter.convert(marketLeonDTOAndOddsLeon);
        assertRunnersByFieldNames(runnerLeonList, marketLeonDTOAndOddsLeon);
        assertRunnersMarket(runnerLeonList, marketLeonDTOAndOddsLeon.getMarketLeonDTO());
        assertRunnersOdd(runnerLeonList, marketLeonDTOAndOddsLeon.getOddsLeon());
    }

    private void prepareMarketLeonDTOConvertion() {
        when(conversionService.convert(any(MarketLeonDTO.class), eq(MarketLeon.class)))
                .then(invocation -> marketLeonDTOToMarketLeonConverter.convert(invocation.getArgument(0)));
    }

    private void prepareMarketLeonSaving() {
        when(marketService.save(any(MarketLeon.class))).then(invocation -> invocation.getArgument(0));
    }

    private MarketLeonDTOAndOddsLeon generatedMarketLeonDTOAndOddsLeon() {
        return Instancio.of(MarketLeonDTOAndOddsLeon.class)
                .set(field(MarketLeonDTOAndOddsLeon::getMarketLeonDTO), generatedMarketLeonDTO())
                .create();
    }

    private MarketLeonDTO generatedMarketLeonDTO() {
        return Instancio.of(MarketLeonDTO.class)
                .generate(field(MarketLeonDTO::getRunners), gen -> gen.collection().size(2))
                .create();
    }

    private void assertRunnersByFieldNames(List<RunnerLeon> runnerLeonList, MarketLeonDTOAndOddsLeon marketLeonDTOAndOddsLeon) {
        var runnerLeonDTOList = marketLeonDTOAndOddsLeon.getMarketLeonDTO().getRunners();
        assertThat(runnerLeonList)
                .isNotNull()
                .hasSize(runnerLeonDTOList.size())
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithType(DOUBLE_COMPARATOR, Double.class)
                .usingComparatorForElementFieldsWithNames(Comparator.comparing(Object::toString), "tags")
                .usingElementComparatorIgnoringFields("id", "market", "odd")
                .isEqualTo(runnerLeonDTOList);
    }

    private void assertRunnersMarket(List<RunnerLeon> runnerLeonList, MarketLeonDTO marketLeonDTO) {
        assertThat(runnerLeonList)
                .extracting(RunnerLeon::getMarket)
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(marketLeonDTOToMarketLeonConverter.convert(marketLeonDTO));
    }

    private void assertRunnersOdd(List<RunnerLeon> runnerLeonList, OddsLeon oddsLeon) {
        assertThat(runnerLeonList)
                .extracting(RunnerLeon::getOdd)
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(oddsLeon);
    }
}