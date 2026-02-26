package ru.kappers;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import ru.kappers.convert.*;
import ru.kappers.exceptions.*;
import ru.kappers.logic.contract.ContractTest;
import ru.kappers.logic.controller.CurrencyControllerTest;
import ru.kappers.logic.controller.LeguesAndTeamsControllerTest;
import ru.kappers.logic.controller.UserControllerTest;
import ru.kappers.logic.controller.web.EventControllerTest;
import ru.kappers.logic.odds.LeonBetParserTest;
import ru.kappers.service.*;
import ru.kappers.service.impl.*;
import ru.kappers.service.parser.CBRFDailyCurrencyRatesParserTest;
import ru.kappers.util.DateTimeUtilTest;

import static org.junit.Assert.assertTrue;

@RunWith(Suite.class)
@SpringBootTest(classes = { KappersApplication.class})
@ActiveProfiles("test")
@TestExecutionListeners({DbUnitTestExecutionListener.class})
@Suite.SuiteClasses({
		RoleServiceImplTest.class,
		DateTimeUtilTest.class,
		UserServiceImplTest.class,
		KapperInfoServiceImplTest.class,
		HistoryServiceImplTest.class,
		FixtureServiceImplTest.class,
        CurrencyRateServiceImplTest.class,
		EventControllerTest.class,
		CurrencyControllerTest.class,
		ApplicationStartListenerTest.class,
		CBRFDailyCurrencyRatesParserTest.class,
		CurrencyServiceImplTest.class,
		LeonBetParserTest.class,
		CompetitorLeonServiceImplTest.class,
		LeagueLeonServiceImplTest.class,
		MarketLeonServiceImplTest.class,
		OddsLeonServiceImplTest.class,
		RunnerLeonServiceImplTest.class,
		LeguesAndTeamsControllerTest.class,
		UserControllerTest.class,
		ContractTest.class,
		BetParserExceptionTest.class,
		CurrencyRateGettingExceptionTest.class,
		MoneyTransferExceptionTest.class,
		UnirestAPIExceptionTest.class,
		UserNotHaveKapperRoleExceptionTest.class,
		CompetitorLeonDTOToCompetitorLeonConverterTest.class,
		EventDTOToEventConverterTest.class,
		FixtureRapidDTOToFixtureConverterTest.class,
		LeagueLeonDTOToLeagueLeonConverterTest.class,
		LeagueRapidDTOLeagueConverterTest.class,
		MarketLeonDTOToMarketLeonConverterTest.class,
		PairOfMarketLeonDTOAndOddsLeonToRunnerLeonListConverterTest.class,
		OddsLeonDTOToOddsLeonConverterTest.class,
		TeamRapidDTOTeamConverterTest.class

})
public class KappersApplicationTests {

	@Test
	public void contextLoads() {
		assertTrue(true);
	}

}
