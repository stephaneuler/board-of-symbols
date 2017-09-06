package jserver;

import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * This is the abstract class with the interface to a board. It implements the
 * convenience methods to send commands. A sub class has to implement the
 * abstract method send. The codeExecutorJava performs the following sequence of
 * method calls
 * <ol>
 * <li>setUp - connect the output with the message field</li>
 * <li>send - send all commands</li>
 * <li>setDown - disconnect the output</li>
 * </ol>
 * 
 * @author Euler
 *
 */
public abstract class XSend {
	public static final int NAVY = 128;
	public static final int DARKBLUE = 139;
	public static final int MEDIUMBLUE = 205;
	public static final int BLUE = 255;
	public static final int DARKGREEN = 25600;
	public static final int GREEN = 32768;
	public static final int TEAL = 32896;
	public static final int DARKCYAN = 35723;
	public static final int DEEPSKYBLUE = 49151;
	public static final int DARKTURQUOISE = 52945;
	public static final int MEDIUMSPRINGGREEN = 64154;
	public static final int LIME = 65280;
	public static final int SPRINGGREEN = 65407;
	public static final int AQUA = 65535;
	public static final int CYAN = 65535;
	public static final int MIDNIGHTBLUE = 1644912;
	public static final int DODGERBLUE = 2003199;
	public static final int LIGHTSEAGREEN = 2142890;
	public static final int FORESTGREEN = 2263842;
	public static final int SEAGREEN = 3050327;
	public static final int DARKSLATEGRAY = 3100495;
	public static final int LIMEGREEN = 3329330;
	public static final int MEDIUMSEAGREEN = 3978097;
	public static final int TURQUOISE = 4251856;
	public static final int ROYALBLUE = 4286945;
	public static final int STEELBLUE = 4620980;
	public static final int DARKSLATEBLUE = 4734347;
	public static final int MEDIUMTURQUOISE = 4772300;
	public static final int INDIGO = 4915330;
	public static final int DARKOLIVEGREEN = 5597999;
	public static final int CADETBLUE = 6266528;
	public static final int CORNFLOWERBLUE = 6591981;
	public static final int REBECCAPURPLE = 6697881;
	public static final int MEDIUMAQUAMARINE = 6737322;
	public static final int DIMGRAY = 6908265;
	public static final int SLATEBLUE = 6970061;
	public static final int OLIVEDRAB = 7048739;
	public static final int SLATEGRAY = 7372944;
	public static final int LIGHTSLATEGRAY = 7833753;
	public static final int MEDIUMSLATEBLUE = 8087790;
	public static final int LAWNGREEN = 8190976;
	public static final int CHARTREUSE = 8388352;
	public static final int AQUAMARINE = 8388564;
	public static final int MAROON = 8388608;
	public static final int PURPLE = 8388736;
	public static final int OLIVE = 8421376;
	public static final int GRAY = 8421504;
	public static final int SKYBLUE = 8900331;
	public static final int LIGHTSKYBLUE = 8900346;
	public static final int BLUEVIOLET = 9055202;
	public static final int DARKRED = 9109504;
	public static final int DARKMAGENTA = 9109643;
	public static final int SADDLEBROWN = 9127187;
	public static final int DARKSEAGREEN = 9419919;
	public static final int LIGHTGREEN = 9498256;
	public static final int MEDIUMPURPLE = 9662683;
	public static final int DARKVIOLET = 9699539;
	public static final int PALEGREEN = 10025880;
	public static final int DARKORCHID = 10040012;
	public static final int YELLOWGREEN = 10145074;
	public static final int SIENNA = 10506797;
	public static final int BROWN = 10824234;
	public static final int DARKGRAY = 11119017;
	public static final int LIGHTBLUE = 11393254;
	public static final int GREENYELLOW = 11403055;
	public static final int PALETURQUOISE = 11529966;
	public static final int LIGHTSTEELBLUE = 11584734;
	public static final int POWDERBLUE = 11591910;
	public static final int FIREBRICK = 11674146;
	public static final int DARKGOLDENROD = 12092939;
	public static final int MEDIUMORCHID = 12211667;
	public static final int ROSYBROWN = 12357519;
	public static final int DARKKHAKI = 12433259;
	public static final int SILVER = 12632256;
	public static final int MEDIUMVIOLETRED = 13047173;
	public static final int INDIANRED = 13458524;
	public static final int PERU = 13468991;
	public static final int CHOCOLATE = 13789470;
	public static final int TAN = 13808780;
	public static final int LIGHTGRAY = 13882323;
	public static final int THISTLE = 14204888;
	public static final int ORCHID = 14315734;
	public static final int GOLDENROD = 14329120;
	public static final int PALEVIOLETRED = 14381203;
	public static final int CRIMSON = 14423100;
	public static final int GAINSBORO = 14474460;
	public static final int PLUM = 14524637;
	public static final int BURLYWOOD = 14596231;
	public static final int LIGHTCYAN = 14745599;
	public static final int LAVENDER = 15132410;
	public static final int DARKSALMON = 15308410;
	public static final int VIOLET = 15631086;
	public static final int PALEGOLDENROD = 15657130;
	public static final int LIGHTCORAL = 15761536;
	public static final int KHAKI = 15787660;
	public static final int ALICEBLUE = 15792383;
	public static final int HONEYDEW = 15794160;
	public static final int AZURE = 15794175;
	public static final int SANDYBROWN = 16032864;
	public static final int WHEAT = 16113331;
	public static final int BEIGE = 16119260;
	public static final int WHITESMOKE = 16119285;
	public static final int MINTCREAM = 16121850;
	public static final int GHOSTWHITE = 16316671;
	public static final int SALMON = 16416882;
	public static final int ANTIQUEWHITE = 16444375;
	public static final int LINEN = 16445670;
	public static final int LIGHTGOLDENRODYELLOW = 16448210;
	public static final int OLDLACE = 16643558;
	public static final int RED = 16711680;
	public static final int FUCHSIA = 16711935;
	public static final int MAGENTA = 16711935;
	public static final int DEEPPINK = 16716947;
	public static final int ORANGERED = 16729344;
	public static final int TOMATO = 16737095;
	public static final int HOTPINK = 16738740;
	public static final int CORAL = 16744272;
	public static final int DARKORANGE = 16747520;
	public static final int LIGHTSALMON = 16752762;
	public static final int ORANGE = 16753920;
	public static final int LIGHTPINK = 16758465;
	public static final int PINK = 16761035;
	public static final int GOLD = 16766720;
	public static final int PEACHPUFF = 16767673;
	public static final int NAVAJOWHITE = 16768685;
	public static final int MOCCASIN = 16770229;
	public static final int BISQUE = 16770244;
	public static final int MISTYROSE = 16770273;
	public static final int BLANCHEDALMOND = 16772045;
	public static final int PAPAYAWHIP = 16773077;
	public static final int LAVENDERBLUSH = 16773365;
	public static final int SEASHELL = 16774638;
	public static final int CORNSILK = 16775388;
	public static final int LEMONCHIFFON = 16775885;
	public static final int FLORALWHITE = 16775920;
	public static final int SNOW = 16775930;
	public static final int YELLOW = 16776960;
	public static final int LIGHTYELLOW = 16777184;
	public static final int IVORY = 16777200;
	public static final int WHITE = 16777215;

	Board board = null;
	public String result = "";
	static PrintStream stdout = System.out;

	abstract public void send() throws InterruptedException;

	public void setUp(JTextArea messageField) {
		TOutputStream tos = new TOutputStream(System.out, messageField);
		PrintStream outStream = new PrintStream(tos, true);
		System.setOut(outStream);
	}

	public void setDown() {
		System.setOut(stdout);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}

}
