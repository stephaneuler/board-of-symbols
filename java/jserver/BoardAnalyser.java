package jserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.Deflater;

public class BoardAnalyser {
	Board board;
	int serialSize;
	int compressedSize;

	public BoardAnalyser(Board board) {
		super();
		this.board = board;
	}

	public int getSerialSize() {
		return serialSize;
	}

	public int getCompressedSize() {
		return compressedSize;
	}

	public int getKC() {
		BoardSerializer bs = new BoardSerializer();
		bs.buildDocument(board);
		String s = bs.write();
		
		//System.out.println( s );

		byte[] input = s.getBytes();
		serialSize = input.length;
		byte[] o =  compress(input);
		compressedSize = o.length;
		return o.length;
	}

	private String compress(String inputString) {
		byte[] input = inputString.getBytes();
		compress(input);
		return "";
	}

	// http://qupera.blogspot.com/2013/02/howto-compress-and-uncompress-java-byte.html
	public byte[] compress(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer); // returns the generated code... index
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] output = outputStream.toByteArray();
		System.out.print("Original: " + data.length  );
		System.out.println(" Compressed: " + output.length  );
		return output;
	}
	
	public Map<String, Integer> getDuplicateMessages() {
		Map<String, Integer> map = new HashMap<>();

		try {
			for (String temp : board.getMessageHistory() ) {
				Integer count = map.get(temp);
				map.put(temp, (count == null) ? 1 : count + 1);
			}
		} catch (java.util.ConcurrentModificationException e) {
			//e.printStackTrace();
		}
		Integer one = Integer.valueOf(1);
		map.entrySet().removeIf(
                matches -> matches.getValue().compareTo( one ) == 0);
		return map;
	}

	public int calcNumBoSLCommands() {
		CounterMap<SymbolType> formCount = new CounterMap<>();
		CounterMap<String> colorCount = new CounterMap<>();
		int sizeCount = 0;
		int textCount = 0;
		int bgCount = 0;
		List<Symbol>symbols = board.getSymbols();
		for( Symbol symbol : symbols ) {
			formCount.put(symbol.getType( )); 
			colorCount.put(symbol.getFarbe( ).toString()); 
			if( symbol.getSize() != Board.DEFAULT_SYMBOL_SIZE ) {
				++sizeCount;
			}
			if( symbol.hasText() ) {
				++textCount;
			}
			if( symbol.getHintergrund() != null ) {
				++bgCount;
			}
		}
		
		//formCount.printSorted();
		int boslForm = 1 + formCount.getSum() - formCount.max();
		System.out.println( "bosl-form:  " +  boslForm);		
		Entry<SymbolType, Integer> mostFrequentForm = formCount.maxEntry();
		System.out.println( "most frequent bosl-form:  " +  mostFrequentForm);		
		if( mostFrequentForm.getKey().equals( SymbolType.CIRCLE)) {
			System.out.println( "ignore default form");		
			--boslForm;
		}

		//colorCount.printSorted();
		int boslColor = 1 + colorCount.getSum() - colorCount.max();
		System.out.println( "bosl-color:  " +  boslColor);		
		Entry<String, Integer> mostFrequentColor = colorCount.maxEntry();
		System.out.println( "bosl-form:  " +  mostFrequentColor);		
		if( mostFrequentColor.getKey().equals( Symbol.getBoSColor().toString() )) {
			System.out.println( "ignore default symbol");		
			--boslColor;
		}
		
		// 1 for size(n,m)
		int sum =  1 + boslForm + boslColor + sizeCount + textCount + bgCount;
		System.out.println( "sum:  " +  sum);		
		return sum;
}


}
