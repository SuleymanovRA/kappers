package ru.kappers.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import lombok.extern.log4j.Log4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kappers.KappersApplication;
import ru.kappers.model.Fixture;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

@Log4j
@ActiveProfiles("test")
@ContextConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KappersApplication.class})
@TestExecutionListeners({DbUnitTestExecutionListener.class})
public class FixtureServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private FixtureService service;
    private Timestamp nowTstmp = new Timestamp(System.currentTimeMillis());
    private Fixture today = Fixture.builder()
            .id(111)
            .eventDate(nowTstmp)
            .eventTimestamp(nowTstmp.getTime())
            .awayTeam("Real Madrid")
            .homeTeam("FC Barselona")
            .leagueId(87)
            .status("Not Started")
            .build();

    private Fixture tomorrow = Fixture.builder()
            .id(112)
            .eventDate(new Timestamp(nowTstmp.getTime() + 24 * 3600 * 1000))
            .eventTimestamp(nowTstmp.getTime() + 24 * 3600 * 1000)
            .awayTeam("Manchester United")
            .homeTeam("FC Liverpool")
            .leagueId(2)
            .status("Not Started")
            .build();

    private Fixture yesterday = Fixture.builder()
            .id(110)
            .eventDate(new Timestamp(nowTstmp.getTime() - 24 * 3600 * 1000))
            .eventTimestamp(nowTstmp.getTime() - 24 * 3600 * 1000)
            .awayTeam("Paris Saint Germain")
            .homeTeam("Lyon")
            .leagueId(4)
            .status("Match Finished")
            .build();

    @Before
    public void setUp() {
        System.out.println(today);
        System.out.println(tomorrow);
        System.out.println(yesterday);
    }

    @Test
    public void getById() {
        Fixture fixture = Fixture.builder()
                .id(54552)
                .eventDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        service.addRecord(fixture);
        Fixture byId = service.getById(54552);
        assertNotNull(byId);
    }


    @Test
    public void addRecord() {
        Fixture record = service.addRecord(tomorrow);
        assertNotNull(record);
        assertEquals((long)record.getId(), (long)112);
        service.deleteRecord(record);
    }

    @Test
    public void deleteRecord() {
        Fixture record = service.addRecord(today);
        Assert.assertNotNull(record);
        Fixture byId = service.getById(111);
        assertEquals(byId, record);
        service.deleteRecord(today);
        byId = service.getById(111);
        assertNull(byId);
    }

    @Test
    public void deleteRecordByFixtureId() {
        Fixture record = service.addRecord(today);
        assertNotNull(record);
        Fixture byId = service.getById(111);
        assertEquals(byId, record);
        service.deleteRecordById(111);
        byId = service.getById(111);
        assertNull(byId);
    }

    @Test
    public void updateFixture() {
    }

    @Test
    public void getAll() {
    }

    @Test
    public void getFixturesByPeriod() {
    }

    @Test
    public void getFixturesToday() {
    }

    @Test
    public void getFixturesToday1() {
    }

    @Test
    public void getFixturesLastWeek() {
    }

    @Test
    public void getFixturesLastWeekFiltered() {
    }

    @Test
    public void getFixturesNextWeek() {
    }

    @Test
    public void getFixturesNextWeekFiltered() {
    }
}