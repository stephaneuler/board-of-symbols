package jserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class JServer implements Runnable {
	int backlog = 10;
	int port = 1958;
	Board board = new Board();
	Handler handler;
	Logger logger;

	JServer() {
		try {
			handler = new FileHandler("logs.xml");
			logger = Logger.getGlobal();
			logger.addHandler(handler);

		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		Thread t = new Thread(this, "Server");
		t.start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new JServer();

	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket socket = new ServerSocket(port, backlog);
			for (;;) {
				System.out.println("Waiting for next connection... ");
				Socket sockConnected = socket.accept();
				System.out.println("Connected with " + sockConnected);
				logger.info("Connected with " + sockConnected);
				PrintStream ps = new PrintStream(sockConnected.getOutputStream());

				InputStream isr = sockConnected.getInputStream();

				String line = getLine(isr);

				if (line == null) {
					System.out.println("No data read!!");
					logger.info("No data read!!");

				} else {
					System.out.println("> " + line);
					logger.info(line);
					if (line.startsWith("GET")) {
						ps.print("Hallo");
					} else {
						if (board.isFilterMode()) {
							line = Board.FILTER_PREFIX + line;
						}
						// send line to board and return answer to client
						ps.print(board.receiveMessage(line));

					}
				}
				isr.close();
				ps.close();
				System.out.println("closing socket");
				sockConnected.close();
			}

		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}

	private String getLine(InputStream isr) {
		byte[] b = new byte[1];
		String sb = "";
		try {
			while (isr.read(b) >= 0) {
				char c = (char) b[0];
				sb += (new Character(c)).toString();
				if (c == '\n') {
					return sb;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void printStatusPage(PrintStream ps) {
		ps.println(board.status());

	}

}
