package ru.kappers.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.kappers.AbstractDatabaseTest;
import ru.kappers.assertion.Assertions;
import ru.kappers.model.Fixture;
import ru.kappers.model.Fixture.ShortStatus;
import ru.kappers.model.Fixture.Status;
import ru.kappers.repository.FixtureRepository;
import ru.kappers.service.impl.FixtureServiceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

@Slf4j
@ContextConfiguration(classes = {FixtureServiceImplTest.Configuration.class})
public class FixtureServiceImplTest extends AbstractDatabaseTest {
    @Autowired
    private FixtureService service;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final LocalDateTime now = LocalDateTime.now();
    private Fixture yesterday = Fixture.builder()
            .id(110)
            .eventDate(now.minusDays(1))
            .eventTimestamp(Timestamp.valueOf(now.minusDays(1)).getTime())
            .awayTeam("Paris Saint Germain")
            .homeTeam("Lyon")
            .leagueId(4)
            .status(Status.MATCH_FINISHED)
            .statusShort(ShortStatus.MATCH_FINISHED)
            .build();
    private final Fixture today = Fixture.builder()
            .id(111)
            .eventDate(now)
            .eventTimestamp(Timestamp.valueOf(now).getTime())
            .awayTeam("Real Madrid")
            .homeTeam("FC Barselona")
            .leagueId(87)
            .status(Status.NOT_STARTED)
            .statusShort(ShortStatus.NOT_STARTED)
            .build();
    private final Fixture todayBegin = today.toBuilder()
            .id(today.getId() + 10)
            .eventDate(LocalDateTime.of(now.toLocalDate(), LocalTime.MIN))
            .eventTimestamp(Timestamp.valueOf(LocalDateTime.of(now.toLocalDate(), LocalTime.MIN)).getTime())
            .status(Status.MATCH_FINISHED)
            .statusShort(ShortStatus.MATCH_FINISHED)
            .build();
    private final Fixture todayEnd = today.toBuilder()
            .id(today.getId() + 11)
            .eventDate(LocalDateTime.of(now.toLocalDate(), LocalTime.MAX))
            .eventTimestamp(Timestamp.valueOf(LocalDateTime.of(now.toLocalDate(), LocalTime.MAX)).getTime())
            .build();
    private Fixture tomorrow = Fixture.builder()
            .id(112)
            .eventDate(now.plusDays(1))
            .eventTimestamp(Timestamp.valueOf(now.plusDays(1)).getTime())
            .awayTeam("Manchester United")
            .homeTeam("FC Liverpool")
            .leagueId(2)
            .status(Status.NOT_STARTED)
            .statusShort(ShortStatus.NOT_STARTED)
            .build();
    private Fixture nextWeek = Fixture.builder()
            .id(113)
            .eventDate(now.plusDays(7))
            .eventTimestamp(Timestamp.valueOf(now.plusDays(7)).getTime())
            .awayTeam("Real Madrid")
            .homeTeam("FC Barselona")
            .leagueId(87)
            .status(Status.NOT_STARTED)
            .statusShort(ShortStatus.NOT_STARTED)
            .build();
    private Fixture lastWeek = Fixture.builder()
            .id(114)
            .eventDate(now.minusDays(7))
            .eventTimestamp(Timestamp.valueOf(now.minusDays(7)).getTime())
            .awayTeam("Paris Saint Germain")
            .homeTeam("Lyon")
            .leagueId(4)
            .status(Status.MATCH_FINISHED)
            .statusShort(ShortStatus.MATCH_FINISHED)
            .build();

    private void clearTable() {
        deleteFromTables(jdbcTemplate, "fixtures");
    }

    @Before
    public void setUp() {
        log.debug("today = " + today);
        log.debug("tomorrow = " + tomorrow);
        log.debug("yesterday = " + yesterday);
    }

    @Test
    public void getById() {
        savedFixture(Fixture.builder()
                .id(54552)
                .eventDate(LocalDateTime.now())
                .build());
        Fixture fixture = service.getById(54552);
        Assertions.assertThat(fixture).isNotNull();
    }

    @Test
    public void addRecord() {
        Fixture fixture = savedFixture(tomorrow);
        Assertions.assertThat(fixture).isNotNull()
                .hasId(tomorrow.getId());
    }

    @Test
    public void deleteRecord() {
        Fixture todayFixture = savedFixture(today);
        Assertions.assertThat(todayFixture).isNotNull();
        Fixture fixture = service.getById(todayFixture.getId());
        Assertions.assertThat(fixture).isEqualTo(todayFixture);
        service.deleteRecord(fixture);
        Assertions.assertThat(service.getById(todayFixture.getId()))
                .isNull();
    }

    private Fixture savedFixture(Fixture fixture) {
        return service.addRecord(fixture);
    }

    @Test
    public void deleteRecordByFixtureId() {
        Fixture todayFixture = savedFixture(today);
        Assertions.assertThat(todayFixture).isNotNull();
        Fixture fixture = service.getById(todayFixture.getId());
        Assertions.assertThat(fixture).isEqualTo(todayFixture);
        service.deleteRecordById(fixture.getId());
        Assertions.assertThat(service.getById(todayFixture.getId()))
                .isNull();
    }

    @Test
    public void updateFixture() {
        var tomorrowFixture = savedFixture(tomorrow);
        Fixture storedFixture = service.getById(tomorrowFixture.getId());
        service.updateFixture(storedFixture.toBuilder()
                .status(Status.MATCH_FINISHED)
                .build());
        storedFixture = service.getById(tomorrowFixture.getId());
        Assertions.assertThat(storedFixture).hasStatus(Status.MATCH_FINISHED);
    }

    @Test
    public void getAll() {
        clearTable();
        final List<Fixture> fixtureList = Stream.of(today, tomorrow, yesterday)
                .map(service::addRecord)
                .toList();
        List<Fixture> all = service.getAll();
        org.assertj.core.api.Assertions.assertThat(all).isNotNull()
                .hasSize(fixtureList.size())
                .containsAll(fixtureList);
    }

    @Test
    public void getAllForPageableOf2ItemsOnFirstPageAndSortASCByEventDate() {
        clearTable();
        Stream.of(today, tomorrow, yesterday)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "eventDate"));
        final Page<Fixture> all = service.getAll(pageable);

        org.assertj.core.api.Assertions.assertThat(all).isNotNull();
        org.assertj.core.api.Assertions.assertThat(all.getContent())
                .hasSize(pageable.getPageSize())
                .containsAll(List.of(yesterday, today))
                .doesNotContain(tomorrow);
    }

    @Test
    public void getFixturesByPeriod() {
        clearTable();
        Stream.of(today, tomorrow, yesterday)
                .forEach(service::addRecord);
        final List<Fixture> fixtures = service.getFixturesByPeriod(now.minusHours(8), now.plusHours(8));

        org.assertj.core.api.Assertions.assertThat(fixtures)
                .contains(today)
                .doesNotContain(tomorrow, yesterday);
    }

    @Test
    public void getFixturesByPeriodForPageableOf4ItemsOnFirstPageAndSortAscByEventDate() {
        clearTable();
        List.of(todayBegin, today, todayEnd, tomorrow, yesterday)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.ASC, "eventDate"));
        final Page<Fixture> todaysFixtures = service.getFixturesByPeriod(todayBegin.getEventDate(), todayEnd.getEventDate(), pageable);

        org.assertj.core.api.Assertions.assertThat(todaysFixtures.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(todayBegin, today, todayEnd)
                .doesNotContain(tomorrow, yesterday);
    }

    private Comparator<Fixture> FixtureByIdComparator() {
        return Comparator.comparingInt(Fixture::getId);
    }

    @Test
    public void getFixturesToday() {
        clearTable();
        List.of(today, todayBegin, todayEnd, tomorrow, yesterday)
                .forEach(service::addRecord);
        List<Fixture> fixturesToday = service.getFixturesToday();
        org.assertj.core.api.Assertions.assertThat(fixturesToday)
                .usingElementComparator(FixtureByIdComparator())
                .contains(today, todayBegin, todayEnd)
                .doesNotContain(tomorrow, yesterday);
    }

    @Test
    public void getFixturesTodayForPageableOf2ItemsOnFirstPageAndSortDescByEventDate() {
        clearTable();
        List.of(today, todayBegin, todayEnd, tomorrow, yesterday)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "eventDate"));
        final Page<Fixture> fixturesToday = service.getFixturesToday(pageable);
        org.assertj.core.api.Assertions.assertThat(fixturesToday.getContent())
                .hasSize(pageable.getPageSize())
                .usingElementComparator(FixtureByIdComparator())
                .contains(today, todayEnd)
                .doesNotContain(todayBegin, yesterday, tomorrow);
    }

    @Test
    public void getFixturesTodayFiltered() {
        clearTable();
        List.of(today, todayBegin, todayEnd, tomorrow, yesterday)
                .forEach(service::addRecord);

        final List<Fixture> fixturesToday = service.getFixturesToday(Status.NOT_STARTED);
        org.assertj.core.api.Assertions.assertThat(fixturesToday)
                .usingElementComparator(FixtureByIdComparator())
                .contains(today, todayEnd)
                .doesNotContain(todayBegin, tomorrow, yesterday);

        final List<Fixture> fixturesToday2 = service.getFixturesToday(Status.MATCH_FINISHED);
        org.assertj.core.api.Assertions.assertThat(fixturesToday2)
                .usingElementComparator(FixtureByIdComparator())
                .contains(todayBegin)
                .doesNotContain(today, todayEnd, tomorrow, yesterday);
    }

    @Test
    public void getFixturesTodayFilteredForPageableOf2ItemsOnFirstPageAndSortAscByEventDate() {
        clearTable();
        List.of(today, todayBegin, todayEnd, tomorrow, yesterday)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "eventDate"));

        final Page<Fixture> fixturesToday = service.getFixturesToday(Status.NOT_STARTED, pageable);
        org.assertj.core.api.Assertions.assertThat(fixturesToday.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(today, todayEnd)
                .doesNotContain(todayBegin, tomorrow, yesterday);

        final Page<Fixture> fixturesToday2 = service.getFixturesToday(Status.MATCH_FINISHED, pageable);
        org.assertj.core.api.Assertions.assertThat(fixturesToday2.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(todayBegin)
                .doesNotContain(today, todayEnd, tomorrow, yesterday);
    }

    @Test
    public void getFixturesLastWeek() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        List<Fixture> fixturesLastWeek = service.getFixturesLastWeek();
        org.assertj.core.api.Assertions.assertThat(fixturesLastWeek)
                .usingElementComparator(FixtureByIdComparator())
                .contains(lastWeek, yesterday, todayBegin, today, todayEnd)
                .doesNotContain(tomorrow, nextWeek);
    }

    @Test
    public void getFixturesLastWeekForPageableOf4ItemsOnFirstPageAndSortAscByEventDate() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.ASC, "eventDate"));
        Page<Fixture> fixturesLastWeek = service.getFixturesLastWeek(pageable);
        org.assertj.core.api.Assertions.assertThat(fixturesLastWeek.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(lastWeek, yesterday, todayBegin, today)
                .doesNotContain(todayEnd, tomorrow, nextWeek);
    }

    @Test
    public void getFixturesLastWeekFiltered() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        List<Fixture> fixturesLastWeek = service.getFixturesLastWeek(Status.MATCH_FINISHED);
        org.assertj.core.api.Assertions.assertThat(fixturesLastWeek)
                .usingElementComparator(FixtureByIdComparator())
                .contains(lastWeek, yesterday, todayBegin)
                .doesNotContain(today, todayEnd, tomorrow, nextWeek);
    }

    @Test
    public void getFixturesLastWeekFilteredForPageableOf2ItemsOnFirstPageAndSortAscByEventDate() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "eventDate"));
        Page<Fixture> fixturesLastWeek = service.getFixturesLastWeek(Status.MATCH_FINISHED, pageable);

        org.assertj.core.api.Assertions.assertThat(fixturesLastWeek.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(lastWeek, yesterday)
                .doesNotContain(todayBegin, today, todayEnd, tomorrow, nextWeek);
    }

    @Test
    public void getFixturesNextWeek() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        List<Fixture> fixturesNextWeek = service.getFixturesNextWeek();
        org.assertj.core.api.Assertions.assertThat(fixturesNextWeek)
                .usingElementComparator(FixtureByIdComparator())
                .contains(todayBegin, today, todayEnd, tomorrow, nextWeek)
                .doesNotContain(lastWeek, yesterday);
    }

    @Test
    public void getFixturesNextWeekForPageableOf2ItemsOnSecondPageAndSortAscByEventDate() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "eventDate"));
        Page<Fixture> fixturesNextWeek = service.getFixturesNextWeek(pageable);
        org.assertj.core.api.Assertions.assertThat(fixturesNextWeek.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(todayEnd, tomorrow)
                .doesNotContain(lastWeek, yesterday, todayBegin, today, nextWeek);
    }

    @Test
    public void getFixturesNextWeekFiltered() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        List<Fixture> fixturesNextWeek = service.getFixturesNextWeek(Status.NOT_STARTED);
        org.assertj.core.api.Assertions.assertThat(fixturesNextWeek)
                .usingElementComparator(FixtureByIdComparator())
                .contains(today, todayEnd, tomorrow, nextWeek)
                .doesNotContain(lastWeek, yesterday, todayBegin);
    }

    @Test
    public void getFixturesNextWeekFilteredForPageableOf2ItemsOnSecondPageAndSortAscByEventDate() {
        clearTable();
        List.of(lastWeek, yesterday, todayBegin, today, todayEnd, tomorrow, nextWeek)
                .forEach(service::addRecord);
        final PageRequest pageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "eventDate"));
        Page<Fixture> fixturesNextWeek = service.getFixturesNextWeek(Status.NOT_STARTED, pageable);

        org.assertj.core.api.Assertions.assertThat(fixturesNextWeek.getContent())
                .usingElementComparator(FixtureByIdComparator())
                .contains(tomorrow, nextWeek)
                .doesNotContain(lastWeek, yesterday, todayBegin, today, todayEnd);
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public FixtureService fixtureService(FixtureRepository fixtureRepository) {
            return new FixtureServiceImpl(fixtureRepository);
        }
    }
}