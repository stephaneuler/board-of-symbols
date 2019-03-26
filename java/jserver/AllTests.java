package jserver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BoardTest.class, DiceTest.class, TrainerTest.class, BoardModelTest.class })
public class AllTests {

}
