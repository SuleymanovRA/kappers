package ru.kappers;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import ru.kappers.logic.controller.web.EventControllerTest;
import ru.kappers.logic.odds.LeonBetParserTest;
import ru.kappers.service.*;

import static org.junit.Assert.assertTrue;

@RunWith(Suite.class)
@SpringBootTest(classes = { KappersApplication.class})
@ActiveProfiles("test")
@TestExecutionListeners({DbUnitTestExecutionListener.class})
@Suite.SuiteClasses({
		KapperInfoServiceImplTest.class,
		FixtureServiceImplTest.class,
		EventControllerTest.class,
		LeonBetParserTest.class
})
public class KappersApplicationTests {
	@Test
	public void contextLoads() {
		assertTrue(true);
	}
}
