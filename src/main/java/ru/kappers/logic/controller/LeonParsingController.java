package ru.kappers.logic.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
     [
     {"url":"/events/Soccer/281474976710675-Europe-UEFA-Champions-League"},
     {"url":"/events/Soccer/281474976721290-Europe-UEFA-Europa-League-Qualification"},
     {"url":"/events/Soccer/281474976710714-Europe-UEFA-Super-Cup"},
     {"url":"/events/Soccer/281474976710710-Italy-Serie-A"},
     {"url":"/events/Soccer/281474976710821-Russia-Premier-League"},
     {"url":"/events/Soccer/1688849860347640-Russia-FNL"},
     {"url":"/events/Soccer/281474976710696-Germany-Bundesliga"},
     {"url":"/events/Soccer/281474976710709-Spain-Primera-Division"},
     {"url":"/events/Soccer/281474976710712-France-Ligue-1"},
     {"url":"/events/Soccer/281474976710692-England-Premier-League"},
     {"url":"/events/Soccer/281474976710697-England-England-Community-Shield"},
     {"url":"/events/Soccer/281474976710717-Portugal-Portugal-Primeira-Liga"},
     {"url":"/events/Soccer/281474976710818-Portugal-Portugal-League-Cup"},
     {"url":"/events/Soccer/281474976712882-Portugal-Portugal-Super-Cup"},
     {"url":"/events/Soccer/281474976710745-Germany-Germany-DFB-Pokal"},
     {"url":"/events/Soccer/281474976710748-Italy-Italy-Coppa-Italia"},
     {"url":"/events/Soccer/281474976712742-World-International-Champions-Cup"},
     {"url":"/events/Soccer/281474976711338-World-Club-Friendlies"},
     {"url":"/events/Soccer"}
     ]

     * где url это путь к турниру, события из которой хотим сохранить в нашу базу
     */

    @RequestMapping(value = "/oddLeons", method = RequestMethod.POST, headers = "Accept=application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOddLeons(@RequestBody String content) {
        JsonArray asJsonArray = GSON.fromJson(content, JsonArray.class);
        for (int i = 0; i < asJsonArray.size(); i++) {
            JsonElement element = asJsonArray.get(i);
            String url = element.getAsJsonObject().get("url").getAsString();
            List<String> list = leonBetParser.loadEventUrlsOfTournament(url);
            List<OddsLeonDTO> eventsWithOdds = leonBetParser.getEventsWithOdds(list);
            for (OddsLeonDTO dto : eventsWithOdds) {
                try {
                    log.info("oddsLeon.savingOdd", dto.getName());
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
                }
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
