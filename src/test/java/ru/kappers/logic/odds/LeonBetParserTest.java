package ru.kappers.logic.odds;

import org.assertj.core.data.Index;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import ru.kappers.AbstractDatabaseTest;
import ru.kappers.model.dto.leon.CompetitorLeonDTO;
import ru.kappers.model.dto.leon.LeagueLeonDTO;
import ru.kappers.model.dto.leon.OddsLeonDTO;
import ru.kappers.model.dto.leon.SportLeonDTO;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {LeonBetParserTest.Configuration.class})
public class LeonBetParserTest extends AbstractDatabaseTest {
    @Autowired
    private BetParser<OddsLeonDTO> parser;

    @Test
    public void loadEventUrlsOfTournament() {
        List<String> list = parser.loadEventUrlsOfTournament("/events/Soccer/281474976710675-Europe-UEFA-Champions-League.htm");
        assertThat(list)
                .hasSize(1)
                .contains("/events/Soccer/281474976710675-Europe-UEFA-Champions-League/281474982732163-Tottenham-Hotspur-Liverpool",
                        Index.atIndex(0));
    }

    @Test
    public void getEventsWithOdds() {
        List<String> list = parser.loadEventUrlsOfTournament("/events/Soccer/281474976710675-Europe-UEFA-Champions-League.htm");
        List<OddsLeonDTO> eventsWithOdds = parser.getEventsWithOdds(list);

        assertThat(eventsWithOdds).isNotEmpty();
        assertOddsLeonDTO(eventsWithOdds.get(0));
    }

    private void assertOddsLeonDTO(OddsLeonDTO oddsLeonDTO) {
        assertThat(oddsLeonDTO)
                .usingRecursiveComparison()
                .ignoringFields("competitors", "league", "markets")
                .isEqualTo(OddsLeonDTO.builder()
                        .id(281474982732163L)
                        .name("Тоттенхэм Хотспур - Ливерпуль")
                        .kickoff(1559415600000L)
                        .lastUpdated(1558185003370L)
                        .open(true)
                        .marketsCount(46)
                        .url("/events/Soccer/281474976710675-Europe-UEFA-Champions-League/281474982732163-Tottenham-Hotspur-Liverpool")
                        .build());
        assertThat(oddsLeonDTO.getMarkets()).as("markets")
                        .hasSize(oddsLeonDTO.getMarketsCount());
        assertCompetitors(oddsLeonDTO.getCompetitors());
        assertLeague(oddsLeonDTO.getLeague());
    }

    private void assertCompetitors(List<CompetitorLeonDTO> competitorLeonDTOS) {
        assertThat(competitorLeonDTOS).as("competitors")
                .isNotNull()
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(
                        CompetitorLeonDTO.builder()
                                .id(281474976833057L)
                                .name("Тоттенхэм Хотспур")
                                .homeAway("HOME")
                                .type("TEAM")
                                .logo("https://cdn.leon.ru/SC/leonru/config_logos/tottenham-hotspur-1.png")
                                .build(),
                        CompetitorLeonDTO.builder()
                                .id(281474976720725L)
                                .name("Ливерпуль")
                                .homeAway("AWAY")
                                .type("TEAM")
                                .logo("https://cdn.leon.ru/SC/leonru/config_logos/liverpool_resize.png")
                                .build()
                );
    }

    private void assertLeague(LeagueLeonDTO leagueLeonDTO) {
        assertThat(leagueLeonDTO).as("league")
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("sport")
                .isEqualTo(LeagueLeonDTO.builder()
                        .id(281474976710675L)
                        .name("Европа - Лига Чемпионов")
                        .url("/events/Soccer/281474976710675-Europe-UEFA-Champions-League")
                        .build());
        assertSportLeonDTO(leagueLeonDTO.getSport());
    }

    private void assertSportLeonDTO(SportLeonDTO sportLeonDTO) {
        assertThat(sportLeonDTO).as("sport")
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("betline")
                .isEqualTo(SportLeonDTO.builder()
                        .id(17592186044417L)
                        .name("Футбол")
                        .weight(100)
                        .family("Soccer")
                        .build());
        assertThat(sportLeonDTO.getBetline()).as("betline")
                .isNotNull();
    }

    @Test
    public void loadEventOdds() {
        final var oddsLeonDTO = parser.loadEventOdds("/events/Soccer/281474976710675-Europe-UEFA-Champions-League/281474982732162-Ajax-Tottenham-Hotspur");
        assertThat(oddsLeonDTO)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("competitors", "league", "markets")
                .isEqualTo(OddsLeonDTO.builder()
                        .id(1143492107996767L)
                        .name("Реал Вальядолид - Валенсия")
                        .kickoff(1558188900000L)
                        .lastUpdated(1558201200964L)
                        .open(false)
                        .marketsCount(0)
                        .url("/events/Soccer/1143492092890276-Spain-LaLiga/1143492107996767-Real-Valladolid-Valencia-CF")
                        .build());
        assertThat(oddsLeonDTO.getCompetitors()).as("competitors")
                .isNotNull();
        assertThat(oddsLeonDTO.getLeague()).as("league")
                .isNotNull();
        assertThat(oddsLeonDTO.getMarkets()).as("markets")
                .isNull();
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public BetParser<OddsLeonDTO> leonBetParser() throws FileNotFoundException, MalformedURLException {
            return new LeonBetParser(ResourceUtils.getFile("classpath:data")
                    .toURI().toURL().toString());
        }
    }
}