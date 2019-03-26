package jserver;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageUtils {

	public static BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight) {
		System.out.println("new dim: " + newWidth + " x " + newHeight);
		BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		double scalew = (double) newWidth / img.getWidth();
		double scaleh = (double) newHeight / img.getHeight();
		System.out.println("Scaling: " + scalew + " " + scaleh);
		if (scaleh < 0.5 | scalew < 0.5) {
			BufferedImage r2 = scaleImage(img, newWidth * 2, newHeight * 2);
			return scaleImage(r2, newWidth, newHeight);
		}
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(scalew, scaleh);
		AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BICUBIC);
		scaleOp.filter(img, resized);

		return resized;
	}

	public static  String getImageCode(BufferedImage image) {
		String code = "// Java code for image" + System.lineSeparator();
		code += "int[][] image = {  " + System.lineSeparator();
		int columns = image.getWidth();
		int rows = image.getHeight();
		for (int col = 0; col < columns; col++) {
			code += "{";
			for (int row = 0; row < rows; row++) {
				int cc = image.getRGB(col, rows - row - 1);
				code += "0x" + Integer.toHexString(cc & 0xffffff);
				if (row == rows - 1) {
					code += "}";
				} else {
					code += ",";
				}
			}
			if (col < columns - 1) {
				code += "," + System.lineSeparator();
			}
		}
		code += "};" + System.lineSeparator();
		code += "for (int x = 0; x < image.length; x ++) {" + System.lineSeparator();
		code += "   for (int y = 0; y <image[x].length; y ++) {" + System.lineSeparator();
		code += "       farbe2(x,y, image[x][y]);" + System.lineSeparator();
		code += "   }" + System.lineSeparator();
		code += "}" + System.lineSeparator();
		return code;
	}

	static public void drawImageWithSymbols(BufferedImage image, Board board) {
		List<Symbol> symbols = board.getSymbols();
		int columns = image.getWidth();
		int rows = image.getHeight();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				int i = image.getRGB(col, rows - row - 1);
				Color c = new Color(i);
				int index = col + row * columns;
				symbols.get(index).setFarbe(c);
				symbols.get(index).setType(SymbolType.SQUARE);
			}
		}
		board.redrawSymbols();
	}



}
