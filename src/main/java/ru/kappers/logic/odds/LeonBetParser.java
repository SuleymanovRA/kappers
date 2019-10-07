package ru.kappers.logic.odds;

import com.google.gson.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kappers.exceptions.BetParserException;
import ru.kappers.model.dto.leon.OddsLeonDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Парсер сайта ООО "Леон"
 */
@Slf4j
@Service
public class LeonBetParser implements BetParser<OddsLeonDTO> {

    @Getter
    /** ссылка на сайт ООО "Леон" */
    private final String leonAddress; //TODO после заведения справочника, вынести в системный параметр
    private final Gson GSON = new Gson();
    private final JsonParser JSON_PARSER = new JsonParser();

    public LeonBetParser() {
        this("${kappers.leon-bet-url}");
    }

    /**
     * Создать парсер сайта ООО "Леон"
     *
     * @param leonAddress ссылка на сайт ООО "Леон"
     */
    @Autowired
    public LeonBetParser(@Value("${kappers.leon-bet-url}") String leonAddress) {
        this.leonAddress = leonAddress;
    }

    @NotNull
    public List<String> loadEventUrlsOfTournament(@NotNull String url) {
        log.debug("loadEventUrlsOfTournament(url: {})...", url);
        List<String> urlsOfEvents = new ArrayList<>();
        JsonArray arr = getArrayOfEventsByURL(url);
        for (int i = 0; i < arr.size(); i++) {
            JsonElement el = arr.get(i);
            String eventLink = el.getAsJsonObject().get("url").getAsString();
            urlsOfEvents.add(eventLink);
        }
        return urlsOfEvents;
    }

    @NotNull
    public List<OddsLeonDTO> getEventsWithOdds(@NotNull List<String> urls) {
        log.debug("getEventsWithOdds(urls: {})...", urls);
        //TODO сохранение в БД (не здесь, а во внешнем коде чтобы соблюдать принципы SOLID)
        return urls.stream()
                .map(this::loadEventOdds)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public OddsLeonDTO loadEventOdds(@NotNull String url) {
        log.debug("loadEventOdds(url: {})...", url);
        JsonArray arr = getArrayOfEventsByURL(url);
        JsonObject object = arr.get(0).getAsJsonObject();
        return GSON.fromJson(object, OddsLeonDTO.class);
    }


    /**
     * Метод возвращает массив объектов JSON, полученных в результате парсинга веб страницы указанного урла
     *
     * @param url - линк веб страницы, которую нужно распарсить
     */
    private JsonArray getArrayOfEventsByURL(String url) {
        try {
            url = URLEncoder.encode(url, StandardCharsets.UTF_8.toString()).replace("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URL address = null;
        try {
            address = new URL(leonAddress + url);
        } catch (MalformedURLException e) {
            String msg = "Не удалось сформировать URL";
            log.error(msg, e);
            throw new BetParserException(msg, e);
        }
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                address.openConnection().getInputStream(), StandardCharsets.UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException e) {
            String msg = "Ошибка ввода/вывода во время загрузки данных со страницы " + url
                    + ". Возможно передан некорректный URL";
            log.error(msg, e);
            throw new BetParserException(msg, e);
        }
        StringBuilder object = new StringBuilder();
        String obj = "";
        String sbStr = sb.toString();
        int idx = sbStr.indexOf("initialEvents:");
        if (idx > -1) {
            object.append(sbStr.substring(idx));
            obj = object.substring(0, object.indexOf("</script>"))
                    .replace("initialEvents", "{\"initialEvents\"")
                    .substring(0, object.indexOf("refreshTimeout"))
                    .trim();
            obj = obj.substring(0, obj.length() - 1).concat("}");
        }
        log.debug("obj: {}", obj);
        JsonObject jsonObject = (JsonObject) JSON_PARSER.parse(obj);
        return (JsonArray) jsonObject.get("initialEvents")
                .getAsJsonObject()
                .get("events");
    }

}
