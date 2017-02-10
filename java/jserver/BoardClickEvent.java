package jserver;

public class BoardClickEvent {
	int x;
	int y;
	int clicks;
	int button;
	
	public int getButton() {
		return button;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getClicks() {
		return clicks;
	}

	public BoardClickEvent(int x, int y, int clicks, int button) {
		super();
		this.x = x;
		this.y = y;
		this.clicks = clicks;
		this.button = button;
	}

	@Override
	public String toString() {
		return "BoardClickEvent [x=" + x + ", y=" + y + ", clicks=" + clicks + ", button=" + button + "]";
	}
	
	
}
