package jserver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.w3c.dom.Node;

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

	Position pos;
	private double size;
	private double fullSize;
	int index = -1;
	// boolean aktiv;
	String key = null;
	SymbolType type = SymbolType.CIRCLE;
	protected Color farbe = Color.LIGHT_GRAY;
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
	 * @param farbe
	 *            the farbe to set
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
		if (key == null) {
			key = plotter.nextVector();
			plotter.setDataLineStyle(key, LineStyle.FILL);
		}
		// if (aktiv)
		// plotter.setDataColor(key, farbe.brighter());
		// else {
		// plotter.setDataColor(key, farbe.darker());
		// }
		plotter.setDataColor(key, farbe);
		for (String k : secondaryKeys) {
			plotter.removeDataObject(k);
		}
		secondaryKeys.clear();

		draw(plotter);

	}

	public void reset() {
		farbe = Color.LIGHT_GRAY;
		hintergrund = null;
		clearHintergrund();
	}

	protected void draw(Plotter plotter) {
		plotter.setDataLineStyle(key, LineStyle.FILL);

		if (hintergrund != null) {
			DataObject d = plotter.getDataSet(key);
			d.setCorners(new plotter.Point(pos.x - fullSize, pos.y + fullSize),
					new plotter.Point(pos.x + fullSize, pos.y - fullSize));
			d.setBackGroundColor(hintergrund);
			mitHintergrund = d;
		}

		if (numbering) {
			plotter.setDataColor(key, new Color(farbe.getRed(), farbe.getGreen(), farbe.getBlue(), alpha));
			String numText = "";
			if (linearNumbering) {
				numText += index;
			} else {
				numText += index % board.getRows() + "," + index / board.getRows();
			}
			plotter.setText(numText, pos.x, pos.y);
		} else {
			plotter.setDataColor(key, farbe);
		}

		if (text != null) {
			if (useAlphaWithText && text.length() != 0) {
				plotter.setDataColor(key, new Color(farbe.getRed(), farbe.getGreen(), farbe.getBlue(), alpha));
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
			for (double t = 0; t < 2 * Math.PI; t += 0.03) {
				double x = pos.x + size * Math.cos(t);
				double y = pos.y + size * Math.sin(t);
				plotter.add(key, x, y);
			}
		} else if (Dice.isDiceType(type)) {
			Dice.draw(key, secondaryKeys, Dice.getValue(type), plotter, pos, size);

		} else if (type == SymbolType.SQUARE) {
			plotter.add(key, pos.x - size, pos.y - size);
			plotter.addD(key, 2 * size, 0);
			plotter.addD(key, 0, 2 * size);
			plotter.addD(key, -2 * size, 0);
			plotter.addD(key, 0, -2 * size);

		} else if (type == SymbolType.DIAMOND) {
			plotter.add(key, pos.x - size, pos.y);
			plotter.addD(key, size, -size);
			plotter.addD(key, size, +size);
			plotter.addD(key, -size, size);
			plotter.addD(key, -size, -size);

		} else if (type == SymbolType.VLINE) {
			plotter.add(key, pos.x, pos.y - size);
			plotter.addD(key, 0, 2 * size);
			plotter.setDataLineStyle(key, LineStyle.LINE);
			plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.HLINE) {
			plotter.add(key, pos.x - size, pos.y);
			plotter.addD(key, 2 * size, 0);
			plotter.setDataLineStyle(key, LineStyle.LINE);
			plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.BAR) {
			plotter.add(key, pos.x - barWidth, pos.y - 0.5);
			plotter.addD(key, 0, 2 * size);
			plotter.addD(key, 2 * barWidth, 0);
			plotter.addD(key, 0, -2 * size);
			plotter.addD(key, -2 * barWidth, 0);
			// plotter.addD(key, 0, 2 * size);
			// plotter.setDataLineStyle(key, LineStyle.LINE);
			// plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.BLOCK) {
			plotter.add(key, pos.x - size, pos.y - blockHeigth);
			plotter.addD(key, 2 * size, 0);
			plotter.addD(key, 0, 2 * blockHeigth );
			plotter.addD(key, -2 * size, 0 );
			plotter.addD(key, 0, -2 * blockHeigth );

		} else if (type == SymbolType.PLUS) {
			plotter.add(key, pos.x, pos.y);
			plotter.addD(key, size, 0);
			plotter.addD(key, -2 * size, 0);
			plotter.addD(key, size, 0);
			plotter.addD(key, 0, size);
			plotter.addD(key, 0, -2 * size);
			plotter.setDataLineStyle(key, LineStyle.LINE);
			plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.UP) {
			plotter.add(key, pos.x - size, pos.y - size);
			plotter.addD(key, 2 * size, 2 * size);
			plotter.setDataLineStyle(key, LineStyle.LINE);
			plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.DOWN) {
			plotter.add(key, pos.x + size, pos.y - size);
			plotter.addD(key, -2 * size, 2 * size);
			plotter.setDataLineStyle(key, LineStyle.LINE);
			plotter.setDataStroke(key, stroke);

		} else if (type == SymbolType.STAR) {
			int spitzen = 8;
			double phi = 2 * Math.PI / spitzen;
			double radius1 = 0.4 * size;
			for (double t = -phi / 4; t < 2 * Math.PI; t += phi) {
				double x = pos.x + radius1 * Math.cos(t);
				double y = pos.y + radius1 * Math.sin(t);
				plotter.add(key, x, y);
				x = pos.x + size * Math.cos(t + phi / 2.);
				y = pos.y + size * Math.sin(t + phi / 2.);
				plotter.add(key, x, y);
			}
			double x = radius1 + pos.x;
			double y = pos.y;
			plotter.add(key, x, y);

		} else if (type == SymbolType.TRIANGLE_LD) {
			plotter.add(key, pos.x - size, pos.y - size);
			plotter.addD(key, 2 * size, 0);
			plotter.addD(key, -2 * size, 2 * size);
			plotter.addD(key, 0, -2 * size);

		} else if (type == SymbolType.TRIANGLE_RD) {
			plotter.add(key, pos.x + size, pos.y - size);
			plotter.addD(key, 0, 2 * size);
			plotter.addD(key, -2 * size, -2 * size);
			plotter.addD(key, 2 * size, 0);

		} else if (type == SymbolType.TRIANGLE_LU) {
			plotter.add(key, pos.x - size, pos.y + size);
			plotter.addD(key, 2 * size, 0);
			plotter.addD(key, -2 * size, -2 * size);
			plotter.addD(key, 0, 2 * size);

		} else if (type == SymbolType.TRIANGLE_RU) {
			plotter.add(key, pos.x + size, pos.y + size);
			plotter.addD(key, -2 * size, 0);
			plotter.addD(key, 2 * size, -2 * size);
			plotter.addD(key, 0, 2 * size);

		}
	}

	public void clearForm(Plotter plotter) {
		if (key != null) {
			plotter.removeAll(key);
		}
		for (String k : secondaryKeys) {
			plotter.removeDataObject(k);
		}
		secondaryKeys.clear();

	}

}
