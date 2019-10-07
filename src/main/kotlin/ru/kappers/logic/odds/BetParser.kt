package ru.kappers.logic.odds

interface BetParser<T> {

    /**
     * Загрузить список ссылок доступных спортивных событий конкретного турнира
     * @param url ссылка веб страницы турнира, из которого нужно получить список событий
     */
    fun loadEventUrlsOfTournament(url: String): MutableList<String>

    /**
     * Загрузить DTO сущность, полученную из веб страницы конкретного события
     * @param url ссылка веб страницы события, которое нужно парсить
     */
    fun loadEventOdds(url: String): T

    /**
     * Получить список событий конкретного турнира
     * @param urls список ссылок, по которым нужно получить спортивные события
     */
    fun getEventsWithOdds(urls: List<String>): List<T>

}
