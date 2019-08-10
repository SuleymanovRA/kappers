package ru.kappers.logic.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.kappers.logic.controller.LeonParsingController;
import ru.kappers.logic.odds.BetParser;
import ru.kappers.model.dto.leon.MarketLeonDTO;
import ru.kappers.model.dto.leon.OddsLeonDTO;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.model.leonmodels.RunnerLeon;
import ru.kappers.service.MessageTranslator;
import ru.kappers.service.OddsLeonService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeonParsingControllerTest {
    @InjectMocks
    private LeonParsingController controller;
    @Mock
    private OddsLeonService oddsLeonService;
    @Mock
    private ConversionService conversionService;
    @Mock
    private MessageTranslator messageTranslator;
    @Mock
    private BetParser<OddsLeonDTO> leonBetParser;


    @Test
    public void getOddLeonsForNewOdd() {
        final String url = "/events/Soccer/281474976710927-Americas-CONCACAF-Gold-Cup";
        final String request = "[{\"url\":\"" + url + "\"}]";
        final List<String> testStringList = Arrays.asList("Test string");
        final OddsLeonDTO oddsLeonDTO = OddsLeonDTO.builder()
                .id(1L)
                .name("Test name")
                .markets(Collections.emptyList())
                .build();
        final OddsLeon oddsLeon = OddsLeon.builder()
                .id(oddsLeonDTO.getId())
                .name(oddsLeonDTO.getName())
                .build();
        final List<OddsLeonDTO> oddsLeonDTOList = Arrays.asList(oddsLeonDTO);
        when(leonBetParser.loadEventUrlsOfTournament(url)).thenReturn(testStringList);
        when(leonBetParser.getEventsWithOdds(testStringList)).thenReturn(oddsLeonDTOList);
        when(conversionService.convert(oddsLeonDTO, OddsLeon.class)).thenReturn(oddsLeon);

        final ResponseEntity<String> result = controller.getOddLeons(request);

        assertThat(result, is(notNullValue()));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        verify(leonBetParser).loadEventUrlsOfTournament(url);
        verify(leonBetParser).getEventsWithOdds(testStringList);
        verify(oddsLeonService).getById(oddsLeonDTO.getId());
        verify(conversionService).convert(oddsLeonDTO, OddsLeon.class);
        verify(oddsLeonService).save(oddsLeon);
        verify(conversionService, never()).convert(any(), eq((Class<List<RunnerLeon>>) (Class<?>) List.class));
    }

    @Test
    public void getOddLeonsForNewOddWithMarkets() {
        final String url = "/events/Soccer/281474976710927-Americas-CONCACAF-Gold-Cup";
        final String request = "[{\"url\":\"" + url + "\"}]";
        final List<String> testStringList = Arrays.asList("Test string");
        final OddsLeonDTO oddsLeonDTO = OddsLeonDTO.builder()
                .id(1L)
                .name("Test name")
                .markets(Arrays.asList(new MarketLeonDTO()))
                .build();
        final OddsLeon oddsLeon = OddsLeon.builder()
                .id(oddsLeonDTO.getId())
                .name(oddsLeonDTO.getName())
                .build();
        final List<OddsLeonDTO> oddsLeonDTOList = Arrays.asList(oddsLeonDTO);
        when(leonBetParser.loadEventUrlsOfTournament(url)).thenReturn(testStringList);
        when(leonBetParser.getEventsWithOdds(testStringList)).thenReturn(oddsLeonDTOList);
        when(conversionService.convert(oddsLeonDTO, OddsLeon.class)).thenReturn(oddsLeon);
        when(conversionService.convert(any(), eq((Class<List<RunnerLeon>>) (Class<?>) List.class))).thenReturn(Arrays.asList(new RunnerLeon()));

        final ResponseEntity<String> result = controller.getOddLeons(request);

        assertThat(result, is(notNullValue()));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        verify(leonBetParser).loadEventUrlsOfTournament(url);
        verify(leonBetParser).getEventsWithOdds(testStringList);
        verify(oddsLeonService).getById(oddsLeonDTO.getId());
        verify(conversionService).convert(oddsLeonDTO, OddsLeon.class);
        verify(oddsLeonService).save(oddsLeon);
        verify(conversionService, atLeastOnce()).convert(any(), eq((Class<List<RunnerLeon>>) (Class<?>) List.class));
    }

    @Test
    public void getOddLeonsForExistsOdd() {
        final String url = "/events/Soccer/281474976710927-Americas-CONCACAF-Gold-Cup";
        final String request = "[{\"url\":\"" + url + "\"}]";
        final List<String> testStringList = Arrays.asList("Test string");
        final OddsLeonDTO oddsLeonDTO = OddsLeonDTO.builder()
                .id(1L)
                .name("Test name")
                .markets(Collections.emptyList())
                .build();
        final OddsLeon oddsLeon = OddsLeon.builder()
                .id(oddsLeonDTO.getId())
                .name(oddsLeonDTO.getName())
                .build();
        final List<OddsLeonDTO> oddsLeonDTOList = Arrays.asList(oddsLeonDTO);
        when(leonBetParser.loadEventUrlsOfTournament(url)).thenReturn(testStringList);
        when(leonBetParser.getEventsWithOdds(testStringList)).thenReturn(oddsLeonDTOList);
        when(oddsLeonService.getById(oddsLeonDTO.getId())).thenReturn(oddsLeon);

        final ResponseEntity<String> result = controller.getOddLeons(request);

        assertThat(result, is(notNullValue()));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        verify(leonBetParser).loadEventUrlsOfTournament(url);
        verify(leonBetParser).getEventsWithOdds(testStringList);
        verify(oddsLeonService).getById(oddsLeonDTO.getId());
        verify(conversionService, never()).convert(oddsLeonDTO, OddsLeon.class);
        verify(oddsLeonService).save(oddsLeon);
        verify(conversionService, never()).convert(any(), eq((Class<List<RunnerLeon>>) (Class<?>) List.class));
    }

    @Test(expected = NullPointerException.class)
    public void getOddLeonsForWrongJSON() {
        final String request = "[{}]";
        controller.getOddLeons(request);
    }

    @Test
    public void getOddLeonsIfNotSaved() {
        final String url = "/events/Soccer/281474976710927-Americas-CONCACAF-Gold-Cup";
        final String request = "[{\"url\":\"" + url + "\"}]";
        final List<String> testStringList = Arrays.asList("Test string");
        final OddsLeonDTO oddsLeonDTO = OddsLeonDTO.builder()
                .id(1L)
                .name("Test name")
                .markets(Collections.emptyList())
                .build();
        final OddsLeon oddsLeon = OddsLeon.builder()
                .id(oddsLeonDTO.getId())
                .name(oddsLeonDTO.getName())
                .build();
        final List<OddsLeonDTO> oddsLeonDTOList = Arrays.asList(oddsLeonDTO);
        when(leonBetParser.loadEventUrlsOfTournament(url)).thenReturn(testStringList);
        when(leonBetParser.getEventsWithOdds(testStringList)).thenReturn(oddsLeonDTOList);
        when(conversionService.convert(oddsLeonDTO, OddsLeon.class)).thenReturn(oddsLeon);
        when(oddsLeonService.save(any())).thenThrow(RuntimeException.class);

        final ResponseEntity<String> result = controller.getOddLeons(request);

        assertThat(result, is(notNullValue()));
        /*если не удалось сохранить, ошибки не должно быть. Просто идем сохраняться дальше*/
       // assertThat(result.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        verify(leonBetParser).loadEventUrlsOfTournament(url);
        verify(leonBetParser).getEventsWithOdds(testStringList);
        verify(oddsLeonService).getById(oddsLeonDTO.getId());
        verify(conversionService).convert(oddsLeonDTO, OddsLeon.class);
        verify(oddsLeonService).save(oddsLeon);
        verify(conversionService, never()).convert(any(), eq((Class<List<RunnerLeon>>) (Class<?>) List.class));
    }
}