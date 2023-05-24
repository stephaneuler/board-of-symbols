package chessv3;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;


@RunWith(JUnitPlatform.class)
@SelectClasses({ PieceTest.class, KnightTest.class, ChessBoardTest.class })
class AllTest {

}
