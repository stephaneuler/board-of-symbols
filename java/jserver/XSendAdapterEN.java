package jserver;

public class XSendAdapterEN extends XSendEN {

	 
	@Override
	public void send() throws InterruptedException {
		// not needed
	}

	public XSendAdapterEN() {
		board = new Board();
	}

	public XSendAdapterEN(Board board) {
		this.board = board;
	}
}
