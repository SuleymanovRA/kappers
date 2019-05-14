package ru.kappers.logic.odds;

import org.junit.Test;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.dto.leon.LeagueLeonDTO;
import ru.kappers.model.dto.leon.OddsLeonDTO;
import ru.kappers.model.dto.leon.SportLeonDTO;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class LeonBetParserTest {

    private BetParser<OddsLeonDTO> parser = new LeonBetParser();

    @Test
    public void loadEventUrlsOfTournament() {
        List<String> list = parser.loadEventUrlsOfTournament("/events/Soccer/281474976710675-Europe-UEFA-Champions-League");
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is("/events/Soccer/281474976710675-Europe-UEFA-Champions-League/281474982732163-Tottenham-Hotspur-Liverpool"));
    }

    @Test
    public void getEventsWithOdds() {
        List<String> list = parser.loadEventUrlsOfTournament("/events/Soccer/281474976710675-Europe-UEFA-Champions-League");
        List<OddsLeonDTO> eventsWithOdds = parser.getEventsWithOdds(list);

        assertThat(eventsWithOdds.isEmpty(), is(false));
        // check LeonOddsDTO
        final OddsLeonDTO oddsDTO = eventsWithOdds.get(0);
        final LeagueLeonDTO leagueDTO = oddsDTO.getLeague();
        final List<CompetitorLeonDTO> competitors = oddsDTO.getCompetitors();
        assertThat(oddsDTO.getId(), is(281474982732163L));
        assertThat(oddsDTO.getName(), is("Тоттенхэм Хотспур - Ливерпуль"));
        assertThat(competitors, is(notNullValue()));
        assertThat(oddsDTO.getKickoff(), is(1559415600000L));
//        assertThat(oddsDTO.getLastUpdated(), is(1557833759654L));
        assertThat(leagueDTO, is(notNullValue()));
        assertThat(oddsDTO.isOpen(), is(true));
        assertThat(oddsDTO.getMarketsCount(), is(50));
        assertThat(oddsDTO.getUrl(), is("/events/Soccer/281474976710675-Europe-UEFA-Champions-League/281474982732163-Tottenham-Hotspur-Liverpool"));
        assertThat(oddsDTO.getMarkets().size(), is(oddsDTO.getMarketsCount()));
        // check List<CompetitorDTO>
        assertThat(competitors.size(), is(2));
        CompetitorLeonDTO tottenhamDTO = competitors.get(0);
        CompetitorLeonDTO liverpoolDTO = competitors.get(1);
        if (tottenhamDTO.getId() != 281474976833057L) {
            CompetitorLeonDTO tmp = tottenhamDTO;
            tottenhamDTO = liverpoolDTO;
            liverpoolDTO = tmp;
        }
        // check tottenhamDTO
        assertThat(tottenhamDTO.getId(), is(281474976833057L));
        assertThat(tottenhamDTO.getName(), is("Тоттенхэм Хотспур"));
        assertThat(tottenhamDTO.getHomeAway(), is("HOME"));
        assertThat(tottenhamDTO.getType(), is("TEAM"));
        assertThat(tottenhamDTO.getLogo(), is("https://cdn.leon.ru/SC/leonru/config_logos/tottenham-hotspur-1.png"));
        // check liverpoolDTO
        assertThat(liverpoolDTO.getId(), is(281474976720725L));
        assertThat(liverpoolDTO.getName(), is("Ливерпуль"));
        assertThat(liverpoolDTO.getHomeAway(), is("AWAY"));
        assertThat(liverpoolDTO.getType(), is("TEAM"));
        assertThat(liverpoolDTO.getLogo(), is("https://cdn.leon.ru/SC/leonru/config_logos/liverpool_resize.png"));
        // check LeagueDTO
        final SportLeonDTO sportDTO = leagueDTO.getSport();
        assertThat(leagueDTO.getId(), is(281474976710675L));
        assertThat(leagueDTO.getName(), is("Европа - Лига Чемпионов"));
        assertThat(sportDTO, is(notNullValue()));
        assertThat(leagueDTO.getUrl(), is("/events/Soccer/281474976710675-Europe-UEFA-Champions-League"));
        // check SportDTO
        assertThat(sportDTO.getId(), is(17592186044417L));
        assertThat(sportDTO.getName(), is("Футбол"));
        assertThat(sportDTO.getBetline(), is(notNullValue()));
        assertThat(sportDTO.getWeight(), is(100));
        assertThat(sportDTO.getFamily(), is("Soccer"));
    }

    @Test
    public void loadEventOdds() {
        final OddsLeonDTO oddsDTO = parser.loadEventOdds("/events/Soccer/281474976710675-Europe-UEFA-Champions-League/281474982732162-Ajax-Tottenham-Hotspur");

        assertThat(oddsDTO, is(notNullValue()));
        assertThat(oddsDTO.getId(), is(1143492108116379L));
        assertThat(oddsDTO.getName(), is("ФК Арарат Ереван - FC Lori"));
        assertThat(oddsDTO.getCompetitors(), is(notNullValue()));
        assertThat(oddsDTO.getKickoff(), is(1557835200000L));
        assertThat(oddsDTO.getLeague(), is(notNullValue()));
        assertThat(oddsDTO.isOpen(), is(true));
//        assertThat(oddsDTO.getMarketsCount(), is(61));
        assertThat(oddsDTO.getUrl(), is("/events/Soccer/1143492095850626-Armenia-Premier-League/1143492108116379-Ararat-Yerevan-FC-FC-Lori"));
        assertThat(oddsDTO.getMarkets(), is(notNullValue()));
    }
}