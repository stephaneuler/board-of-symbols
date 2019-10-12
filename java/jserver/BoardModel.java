package jserver;

public class BoardModel {
	private int rows = 10;
	private int columns = 10;
	
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public void incRows(int i) {
		rows += i;
		
	}
	public void incColumns(int i) {
		columns += i;
		
	}
	public int getNumberSymbols() {
		return rows * columns;
	}
	public void decRows(int i) {
		rows -= i;
		
	}

}
