package ru.kappers.logic.odds;

import java.util.List;

public interface BetParser<T> {

    /**
     * Загрузить список ссылок доступных спортивных событий конкретного турнира
     * @param url ссылка веб страницы турнира, из которого нужно получить список событий
     */
    List<String> loadEventUrlsOfTournament(String url);

    /**
     * Загрузить DTO сущность, полученную из веб страницы конкретного события
     * @param url ссылка веб страницы события, которое нужно парсить
     */
    T loadEventOdds(String url);

    /**
     * Получить список событий конкретного турнира
     * @param urls список ссылок, по которым нужно получить спортивные события
     */
    List<T> getEventsWithOdds(List<String> urls);

}
