package ru.kappers.logic.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kappers.logic.odds.BetParser;
import ru.kappers.model.dto.leon.MarketLeonDTO;
import ru.kappers.model.dto.leon.OddsLeonDTO;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.model.leonmodels.RunnerLeon;
import ru.kappers.service.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/rest/leon")
public class LeonParsingController {
    private static final Gson GSON = new Gson();

    private final OddsLeonService oddsLeonService;
    private final ConversionService conversionService;
    private final MessageTranslator messageTranslator;
    private final BetParser<OddsLeonDTO> leonBetParser;

    @Autowired
    public LeonParsingController(OddsLeonService oddsLeonService, ConversionService conversionService, MessageTranslator messageTranslator, BetParser<OddsLeonDTO> leonBetParser) {
        this.oddsLeonService = oddsLeonService;
        this.conversionService = conversionService;
        this.messageTranslator = messageTranslator;
        this.leonBetParser = leonBetParser;
    }

    /**
     * Пост запрос с телом
     * {
     * "url":"/events/Soccer/281474976710874-Americas-Copa-America"
     * }
     * где url это путь к турниру, события из которой хотим сохранить в нашу базу
     */

    @RequestMapping(value = "/oddLeons", method = RequestMethod.POST, headers = "Accept=application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOddLeons(@RequestBody String content) {
        JsonObject jObject = GSON.fromJson(content, JsonElement.class).getAsJsonObject();
        String url = jObject.get("url").getAsString();
        List<String> list = leonBetParser.loadEventUrlsOfTournament(url);
        List<OddsLeonDTO> eventsWithOdds = leonBetParser.getEventsWithOdds(list);
        for (OddsLeonDTO dto : eventsWithOdds) {
            try {
                OddsLeon odd = oddsLeonService.getById(dto.getId());
                if (odd == null) {
                    odd = conversionService.convert(dto, OddsLeon.class);
                }
                List<RunnerLeon> runners = runnerLeonConverter(dto.getMarkets(), odd);
                if (odd != null) {
                    final OddsLeon o = odd;
                    runners.forEach(s -> s.setOdd(o));
                    odd.setRunners(runners);
                    oddsLeonService.save(odd);
                }
            } catch (Exception e) {
                String msg = messageTranslator.byCode("oddsLeon.withIdAndNameAreNotSaved", dto.getId(), dto.getName());
                log.error(msg, e);
                return ResponseEntity.unprocessableEntity().body(msg);
            }
        }
        return ResponseEntity.ok(messageTranslator.byCode("oddsLeon.areSaved"));
    }

    /**
     * Из MarketLeonDTO, который мы получили с сайта Леон, вытаскиваем всех раннеров. Сами маркеты сохраняем отдельной сущностью, они могут повторяться
     */
    private List<RunnerLeon> runnerLeonConverter(List<MarketLeonDTO> markets, OddsLeon odd) {
        final List<RunnerLeon> runners = new ArrayList<>(markets.size());
        for (MarketLeonDTO market : markets) {
            Pair<MarketLeonDTO, OddsLeon> pair = Pair.of(market, odd);
            runners.addAll(conversionService.convert(pair, (Class<List<RunnerLeon>>) (Class<?>) List.class));
        }
        return runners;
    }
}
