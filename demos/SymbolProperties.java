package tests;

import java.awt.Font;

import jserver.Board;
import jserver.ColorNames;
import jserver.Symbol;
import jserver.XSendAdapter;
import plotter.DataObject;
import plotter.TextObject;


// Demo: set text properties of a symbol
// SE, July 2024

public class SymbolProperties {
	XSendAdapter xs = new XSendAdapter();
	Board board = xs.getBoard();

	public static void main(String[] args) {
		SymbolProperties symboProperties = new SymbolProperties();
		symboProperties.standardSymbol(1, 2);
		symboProperties.getInfo(1, 2);
		symboProperties.specialSymbol(2, 2);
		symboProperties.getInfo(2, 2);

	}

	private void specialSymbol(int x, int y) {
		xs.form2(x, y, "s");
		xs.farbe2(x, y, XSendAdapter.YELLOW);
		xs.text2(x, y, "Mod");

		Symbol symbol = board.getSymbol(x, y);
		TextObject textObject = symbol.getTextObject();
		textObject.setFont(new Font("Bauhaus 93", Font.ITALIC, 33));
		
		xs.statusText("Modify text properties of a symbol");

	}

	private void standardSymbol(int x, int y) {
		xs.groesse(5, 5);
		xs.farbe2(x, y, XSendAdapter.LIGHTBLUE);
		xs.form2(x, y, "s");
		xs.text2(x, y, "Hallo");
	}

	private void getInfo(int x, int y) {
		Symbol symbol = board.getSymbol(x, y);
		System.out.println("************** Symbol (" + x + "," + y + ")" + "**************");
		System.out.println(symbol);
		System.out.println("Form: " + symbol.getType());
		System.out.println("Farbe: " + ColorNames.getTextFull(symbol.getFarbe()));
		System.out.println("Hintergrund: " + ColorNames.getTextFull(symbol.getHintergrund()));
		System.out.println("Groesse: " + symbol.getSize());
		System.out.println("Rotate: " + symbol.getRotate());
		System.out.println("Text: " + symbol.getText());
		System.out.println("Textfarbe: " + ColorNames.getTextFull(symbol.getTextFarbe()));

		System.out.println("************** TextObjekt **************");
		TextObject tO = symbol.getTextObject();
		System.out.println("Font: " + tO.getFont());
		System.out.println("Stroke: " + tO.getStroke());

		System.out.println("************** DataObjekt **************");
		DataObject dO = board.getPlotter().getDataObject(symbol.getKey());
		System.out.println("DataObject: " + dO);
	}

}
