package ru.kappers.convert;


import lombok.Builder;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.convert.ConversionService;
import ru.kappers.UnitTest;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.dto.leon.LeagueLeonDTO;
import ru.kappers.model.dto.leon.OddsLeonDTO;
import ru.kappers.model.leonmodels.CompetitorLeon;
import ru.kappers.model.leonmodels.LeagueLeon;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.service.CompetitorLeonService;
import ru.kappers.service.LeagueLeonService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OddsLeonDTOToOddsLeonConverterTest extends UnitTest {
    @InjectMocks
    private OddsLeonDTOToOddsLeonConverter converter;
    @Mock
    private CompetitorLeonService competitorService;
    @Mock
    private LeagueLeonService leagueService;
    @Mock
    private ConversionService conversionService;

    @Test
    void convertMustReturnNullIfParameterIsNull() {
        assertThat(converter.convert(null)).isNull();
    }

    @Builder
    record TestConvertionData(
            LeagueLeon leagueLeon,
            CompetitorLeon competitorLeon,
            CompetitorLeon competitorLeon2
    ) {}

    @Test
    void convert() {
        final TestConvertionData testData = generatedTestConvertionData();
        for (OddsLeonDTO oddsLeonDTO : generatedOddsLeonDTOList(testData)) {
            reset(competitorService, leagueService, conversionService);
            prepareCompetitorSaving();
            prepareLeagueSaving();
            prepareCompetitorLeonConversion(testData.competitorLeon, testData.competitorLeon2);
            prepareLeagueLeonConversion(testData.leagueLeon);

            final OddsLeon oddsLeon = converter.convert(oddsLeonDTO);
            assertOddsLeonByFieldNames(oddsLeon, oddsLeonDTO);
            assertOddsLeonSpecificFields(oddsLeon, oddsLeonDTO, testData);
            assertRunnersIsNotNull(oddsLeon);
        }
    }

    private TestConvertionData generatedTestConvertionData() {
        return TestConvertionData.builder()
                .leagueLeon(Instancio.create(LeagueLeon.class))
                .competitorLeon(Instancio.create(CompetitorLeon.class))
                .competitorLeon2(Instancio.create(CompetitorLeon.class))
                .build();
    }

    private List<OddsLeonDTO> generatedOddsLeonDTOList(TestConvertionData testData) {
        final var leagueLeonDTO = LeagueLeonDTO.builder().
                name(testData.leagueLeon.getName())
                .build();
        final List<CompetitorLeonDTO> competitorLeonDTOList = generatedCompetitorLeonDTOList(testData.competitorLeon, testData.competitorLeon2);
        return Instancio.ofList(OddsLeonDTO.class)
                .size(2)
                .set(field(OddsLeonDTO::getCompetitors), competitorLeonDTOList)
                .set(field(OddsLeonDTO::getLeague), leagueLeonDTO)
                .set(field(OddsLeonDTO::isOpen), true)
                .create();
    }

    private List<CompetitorLeonDTO> generatedCompetitorLeonDTOList(CompetitorLeon competitorLeon, CompetitorLeon competitorLeon2) {
        return Arrays.asList(
                CompetitorLeonDTO.builder()
                        .name(competitorLeon.getName())
                        .homeAway("HOME")
                        .build(),
                CompetitorLeonDTO.builder()
                        .name(competitorLeon2.getName())
                        .homeAway("AWAY")
                        .build()
        );
    }

    private void prepareCompetitorSaving() {
        when(competitorService.save(any())).thenAnswer(it -> it.getArgument(0));
    }

    private void prepareLeagueSaving() {
        when(leagueService.save(any())).thenAnswer(it -> it.getArgument(0));
    }

    private void prepareCompetitorLeonConversion(CompetitorLeon competitorLeon, CompetitorLeon competitorLeon2) {
        when(conversionService.convert(any(CompetitorLeonDTO.class), eq(CompetitorLeon.class))).thenAnswer(it -> {
            CompetitorLeonDTO dtoInner = it.getArgument(0);
            return dtoInner.getName().equals(competitorLeon.getName()) ? competitorLeon : competitorLeon2;
        });
    }

    private void prepareLeagueLeonConversion(LeagueLeon leagueLeon) {
        when(conversionService.convert(any(LeagueLeonDTO.class), eq(LeagueLeon.class))).thenReturn(leagueLeon);
    }

    private void assertOddsLeonByFieldNames(OddsLeon oddsLeon, OddsLeonDTO oddsLeonDTO) {
        assertThat(oddsLeon)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("kickoff", "lastUpdated", "league", "home", "away", "runners")
                .isEqualTo(oddsLeonDTO);
    }

    private void assertOddsLeonSpecificFields(OddsLeon oddsLeon, OddsLeonDTO oddsLeonDTO, TestConvertionData testData) {
        assertThat(oddsLeon)
                .extracting(
                        OddsLeon::getKickoff,
                        OddsLeon::getLastUpdated,
                        OddsLeon::getLeague,
                        OddsLeon::getHome,
                        OddsLeon::getAway
                ).containsExactly(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(oddsLeonDTO.getKickoff()), TimeZone.getDefault().toZoneId()),
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(oddsLeonDTO.getLastUpdated()), TimeZone.getDefault().toZoneId()),
                        testData.leagueLeon,
                        testData.competitorLeon,
                        testData.competitorLeon2
                );
    }

    private void assertRunnersIsNotNull(OddsLeon oddsLeon) {
        assertThat(oddsLeon)
                .extracting(OddsLeon::getRunners)
                .isNotNull();
    }
}