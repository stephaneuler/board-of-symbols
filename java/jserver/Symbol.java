package jserver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import plotter.DataObject;
import plotter.ImageObject;
import plotter.LineStyle;
import plotter.Plotter;
import plotter.TextObject;

public class Symbol {
	private static int alpha = 150;
	private static double barWidth = 0.4;
	private static double blockHeigth = 0.4;
	private static boolean numbering = false;
	private static String fontFamilie = "Arial";
	private static Font font = new Font(fontFamilie, Font.PLAIN, 12);
	private static Board board;
	private static boolean linearNumbering = false;
	private static Color BoSColor = Color.LIGHT_GRAY;
	private static Color numberTextColor = Color.BLUE;

	Position pos;
	private double size;
	private double fullSize;
	int index = -1;
	// boolean aktiv;
	private String key = null;
	SymbolType type = SymbolType.CIRCLE;
	protected Color farbe = BoSColor;
	protected Color textFarbe = Color.BLACK;
	protected Color hintergrund;;
	private Stroke stroke = new BasicStroke(5.f);
	private DataObject mitHintergrund = null;
	private String text = null;
	TextObject textObject = null;
	private List<String> secondaryKeys = new ArrayList<String>();
	private boolean useAlphaWithText;
	private ImageObject io;

	public Symbol(Position pos, double size) {
		super();
		this.pos = pos;
		this.size = size;
		fullSize = size;
	}

	public static Color getBoSColor() {
		return BoSColor;
	}

	public static void setBoSColor(Color boSColor) {
		BoSColor = boSColor;
	}

	public Position getPos() {
		return pos;
	}

	public static Board getBoard() {
		return board;
	}

	public static void setBoard(Board board) {
		Symbol.board = board;
	}

	public static boolean isLinearNumbering() {
		return linearNumbering;
	}

	public static void setLinearNumbering(boolean linearNumbering) {
		Symbol.linearNumbering = linearNumbering;
	}

	public static void setFontType(String name) {
		fontFamilie = name;
		font = new Font(fontFamilie, Font.PLAIN, font.getSize());
	}

	public static void setFontSize(int s) {
		font = new Font(fontFamilie, Font.PLAIN, s);
	}

	public static int getFontSize() {
		return font.getSize();
	}

	public static Font getFont() {
		return font;
	}

	public static void setFont(Font font) {
		Symbol.font = font;
	}

	public static double getBarWidth() {
		return barWidth;
	}

	public static void setBarWidth(double barWidth) {
		Symbol.barWidth = barWidth;
	}

	public static boolean isNumbering() {
		return numbering;
	}

	public static void setNumbering(boolean numbering) {
		Symbol.numbering = numbering;
	}

	public static void setNumberTextColor(Color color) {
		numberTextColor = color;
	}
	public static Color getNumberTextColor() {
		return numberTextColor;
	}
	
	public String getText() {
		return text;
	}

	public boolean hasText() {
		return text != null;
	}


	public void setText(String text) {
		this.text = text;
	}

	public Color getTextFarbe() {
		return textFarbe;
	}

	public void setTextFarbe(Color textFarbe) {
		this.textFarbe = textFarbe;
	}

	public static void toggleNumbering() {
		numbering = !numbering;
	}

	public static int getAlpha() {
		return alpha;
	}

	public static void setAlpha(int alpha) {
		Symbol.alpha = alpha;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the farbe
	 */
	public Color getFarbe() {
		return farbe;
	}

	/**
	 * @param farbe the farbe to set
	 */
	public void setFarbe(Color farbe) {
		this.farbe = farbe;

	}

	public Color getHintergrund() {
		return hintergrund;
	}

	public void setHintergrund(Color hintergrund) {
		// System.out.println("Hintergrund: " + hintergrund);
		this.hintergrund = hintergrund;
	}

	public void clearHintergrund() {
		hintergrund = null;
		if (mitHintergrund != null) {
			mitHintergrund.setCorners(null);
			mitHintergrund = null;
		}
	}

	public SymbolType getType() {
		return type;
	}

	public void setType(SymbolType type) {
		if (type == SymbolType.RANDOM) {
			this.type = SymbolType.getRandom();
		} else {
			this.type = type;
		}
	}

	/**
	 * @return the aktiv
	 */
	// public boolean isAktiv() {
	// return aktiv;
	// }
	//
	// /**
	// * @param aktiv
	// * the aktiv to set
	// */
	// public void setAktiv(boolean aktiv) {
	// this.aktiv = aktiv;
	// }

	public void setImage(String imageFileName, Plotter plotter) {
		plotter.removeImageObject(io);
		if (imageFileName.equals("-")) {
			return;
		}
		io = plotter.setImage(imageFileName, pos.x, pos.y);
		if (io == null) {
			JOptionPane.showMessageDialog(null, "Datei " + imageFileName + " nicht gefunden", "Bilddatei",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		io.setWorldWidth(2 * size);
		io.setWorldHeight(2 * size);
	}

	public void zeichnen(Plotter plotter) {
		if (getKey() == null) {
			setKey(plotter.nextDataSet());
			plotter.setDataLineStyle(getKey(), LineStyle.FILL);
		}
		// if (aktiv)
		// plotter.setDataColor(key, farbe.brighter());
		// else {
		// plotter.setDataColor(key, farbe.darker());
		// }
		plotter.setDataColor(getKey(), farbe);
		for (String k : secondaryKeys) {
			plotter.removeDataObject(k);
		}
		secondaryKeys.clear();

		draw(plotter);

	}

	public void reset() {
		farbe = BoSColor;
		hintergrund = null;
		clearHintergrund();
	}

	protected void draw(Plotter plotter) {
		plotter.setDataLineStyle(key, LineStyle.FILL);
		plotter.removeCirlce( key );

		if (hintergrund != null) {
			DataObject d = plotter.getDataSet(getKey());
			d.setCorners(new plotter.Point(pos.x - fullSize, pos.y + fullSize),
					new plotter.Point(pos.x + fullSize, pos.y - fullSize));
			d.setBackGroundColor(hintergrund);
			mitHintergrund = d;
		}

		if (numbering) {
			plotter.setDataColor(getKey(), new Color(farbe.getRed(), farbe.getGreen(), farbe.getBlue(), alpha));
			String numText = "";
			if (linearNumbering) {
				numText += index;
			} else {
				numText += index % board.getColumns() + "," + index / board.getColumns();
			}
			TextObject to = plotter.setText(numText, pos.x, pos.y);
			to.setColor( numberTextColor );
		} else {
			plotter.setDataColor(getKey(), farbe);
		}

		if (text != null) {
			if (useAlphaWithText && text.length() != 0) {
				plotter.setDataColor(getKey(), new Color(farbe.getRed(), farbe.getGreen(), farbe.getBlue(), alpha));
			}
			// System.out.println("Text:" + text );
			// if (textObject == null) {
			while (plotter.removeText(pos.x, pos.y))
				;
			textObject = plotter.setText(text, pos.x, pos.y);
			textObject.setColor(textFarbe);
			textObject.setFont(font);
			// } else {
			// textObject.setText(text);
			// }
		}

		if (type == SymbolType.CIRCLE) {
//			for (double t = 0; t < 2 * Math.PI; t += 0.03) {
//				double x = pos.x + size * Math.cos(t);
//				double y = pos.y + size * Math.sin(t);
//				plotter.add(getKey(), x, y);
//			}
			plotter.addCircle( key, pos.x, pos.y, size );

		} else if (Dice.isDiceType(type)) {
			Dice.draw(getKey(), secondaryKeys, Dice.getValue(type), plotter, pos, size);

		} else if (type == SymbolType.SQUARE) {
			plotter.add(getKey(), pos.x - size, pos.y - size);
			plotter.addD(getKey(), 2 * size, 0);
			plotter.addD(getKey(), 0, 2 * size);
			plotter.addD(getKey(), -2 * size, 0);
			plotter.addD(getKey(), 0, -2 * size);

		} else if (type == SymbolType.DIAMOND) {
			plotter.add(getKey(), pos.x - size, pos.y);
			plotter.addD(getKey(), size, -size);
			plotter.addD(getKey(), size, +size);
			plotter.addD(getKey(), -size, size);
			plotter.addD(getKey(), -size, -size);

		} else if (type == SymbolType.VLINE) {
			plotter.add(getKey(), pos.x, pos.y - size);
			plotter.addD(getKey(), 0, 2 * size);
			plotter.setDataLineStyle(getKey(), LineStyle.LINE);
			plotter.setDataStroke(getKey(), stroke);

		} else if (type == SymbolType.HLINE) {
			plotter.add(getKey(), pos.x - size, pos.y);
			plotter.addD(getKey(), 2 * size, 0);
			plotter.setDataLineStyle(getKey(), LineStyle.LINE);
			plotter.setDataStroke(getKey(), stroke);

		} else if (type == SymbolType.BAR) {
			plotter.add(getKey(), pos.x - barWidth, pos.y - 0.5);
			plotter.addD(getKey(), 0, 2 * size);
			plotter.addD(getKey(), 2 * barWidth, 0);
			plotter.addD(getKey(), 0, -2 * size);
			plotter.addD(getKey(), -2 * barWidth, 0);
			// plotter.addD(key, 0, 2 * size);
			// plotter.setDataLineStyle(key, LineStyle.LINE);
			// plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.BLOCK) {
			plotter.add(getKey(), pos.x - size, pos.y - blockHeigth);
			plotter.addD(getKey(), 2 * size, 0);
			plotter.addD(getKey(), 0, 2 * blockHeigth);
			plotter.addD(getKey(), -2 * size, 0);
			plotter.addD(getKey(), 0, -2 * blockHeigth);

		} else if (type == SymbolType.PLUS) {
			plotter.add(getKey(), pos.x, pos.y);
			plotter.addD(getKey(), size, 0);
			plotter.addD(getKey(), -2 * size, 0);
			plotter.addD(getKey(), size, 0);
			plotter.addD(getKey(), 0, size);
			plotter.addD(getKey(), 0, -2 * size);
			plotter.setDataLineStyle(getKey(), LineStyle.LINE);
			plotter.setDataStroke(getKey(), stroke);

		} else if (type == SymbolType.UP) {
			plotter.add(getKey(), pos.x - size, pos.y - size);
			plotter.addD(getKey(), 2 * size, 2 * size);
			plotter.setDataLineStyle(getKey(), LineStyle.LINE);
			plotter.setDataStroke(getKey(), stroke);

		} else if (type == SymbolType.DOWN) {
			plotter.add(getKey(), pos.x + size, pos.y - size);
			plotter.addD(getKey(), -2 * size, 2 * size);
			plotter.setDataLineStyle(getKey(), LineStyle.LINE);
			plotter.setDataStroke(getKey(), stroke);

		} else if (type == SymbolType.STAR) {
			int spitzen = 8;
			double phi = 2 * Math.PI / spitzen;
			double radius1 = 0.4 * size;
			for (double t = -phi / 4; t < 2 * Math.PI; t += phi) {
				double x = pos.x + radius1 * Math.cos(t);
				double y = pos.y + radius1 * Math.sin(t);
				plotter.add(getKey(), x, y);
				x = pos.x + size * Math.cos(t + phi / 2.);
				y = pos.y + size * Math.sin(t + phi / 2.);
				plotter.add(getKey(), x, y);
			}
			double x = radius1 + pos.x;
			double y = pos.y;
			plotter.add(getKey(), x, y);

		} else if (type == SymbolType.TRIANGLE_LD) {
			plotter.add(getKey(), pos.x - size, pos.y - size);
			plotter.addD(getKey(), 2 * size, 0);
			plotter.addD(getKey(), -2 * size, 2 * size);
			plotter.addD(getKey(), 0, -2 * size);

		} else if (type == SymbolType.TRIANGLE_RD) {
			plotter.add(getKey(), pos.x + size, pos.y - size);
			plotter.addD(getKey(), 0, 2 * size);
			plotter.addD(getKey(), -2 * size, -2 * size);
			plotter.addD(getKey(), 2 * size, 0);

		} else if (type == SymbolType.TRIANGLE_LU) {
			plotter.add(getKey(), pos.x - size, pos.y + size);
			plotter.addD(getKey(), 2 * size, 0);
			plotter.addD(getKey(), -2 * size, -2 * size);
			plotter.addD(getKey(), 0, 2 * size);

		} else if (type == SymbolType.TRIANGLE_RU) {
			plotter.add(getKey(), pos.x + size, pos.y + size);
			plotter.addD(getKey(), -2 * size, 0);
			plotter.addD(getKey(), 2 * size, -2 * size);
			plotter.addD(getKey(), 0, 2 * size);

		} else if (type == SymbolType.DOT) {
			plotter.add(getKey(), pos.x - size, pos.y - size);
			plotter.setDataLineStyle(getKey(), LineStyle.DOT);

		}
	}

	public void clearForm(Plotter plotter) {
		if (getKey() != null) {
			plotter.removeAll(getKey());
		}
		for (String k : secondaryKeys) {
			plotter.removeDataObject(k);
		}
		secondaryKeys.clear();

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


}
