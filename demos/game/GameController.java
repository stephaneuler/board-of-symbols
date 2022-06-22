package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jserver.BoardClickEvent;
import jserver.BoardClickListener;

// Der Controller wartet auf Nachrichten vom GUI und führt entsprechende Aktionen aus. 
// Version 
// 1.0  Mai 2022  SE
// 1.1  Juni 2022 SE Reset bei Neustart

public class GameController implements BoardClickListener, ActionListener {
	enum GameState {
		P1_TO_MOVE, P1_MOVES, P2_TO_MOVE, P2_MOVES
	};

	private GameBoard gameBoard;
	private GameGUI gameGui;
	private GameState state = GameState.P1_TO_MOVE;
	private int xOld;
	private int yOld;

	public GameController(GameBoard gameBoard, GameGUI gameGui) {
		super();
		this.gameBoard = gameBoard;
		this.gameGui = gameGui;

		this.gameGui.addClickListener(this);
		this.gameGui.addActionListener(this);

	}

	@Override
	public void boardClick(BoardClickEvent info) {
		System.out.println(state + " " + info);

		int x = info.getX();
		int y = info.getY();

		changeState(x, y);
	}

	private void changeState(int x, int y) {
		int piece = gameBoard.get(x, y);

		switch (state) {
		case P1_TO_MOVE:
			if (piece == GameBoard.PLAYER_1) {
				touch(x, y);
				state = GameState.P1_MOVES;
			}
			break;
		case P1_MOVES:
			if (x == xOld && y == yOld) {
				gameGui.unsetActive(x, y);
				gameGui.showInfo("SpielerIn 1 am Zug");
				state = GameState.P1_TO_MOVE;
			} else if (piece == GameBoard.EMPTY) {
				move(x, y);
				gameGui.showInfo("SpielerIn 2 am Zug");
				state = GameState.P2_TO_MOVE;
			}
			break;
		case P2_TO_MOVE:
			if (piece == GameBoard.PLAYER_2) {
				touch(x, y);
				state = GameState.P2_MOVES;
			}
			break;
		case P2_MOVES:
			if (x == xOld && y == yOld) {
				gameGui.unsetActive(x, y);
				gameGui.showInfo("SpielerIn 2 am Zug");
				state = GameState.P2_TO_MOVE;
			} else if (piece == GameBoard.EMPTY) {
				move(x, y);
				gameGui.showInfo("SpielerIn 1 am Zug");
				state = GameState.P1_TO_MOVE;
			}
			break;
		default:
			gameGui.showInfo("Ungueltiger Zustand  " + state);
		}
	}

	private void move(int x, int y) {
		gameBoard.move(xOld, yOld, x, y);
		gameGui.showGameBoard();
	}

	private void touch(int x, int y) {
		gameGui.setActive(x, y);
		xOld = x;
		yOld = y;
		gameGui.showInfo("Figur in Hand");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println(command);
		if (command.equals("Neustart")) {
			state = GameState.P1_TO_MOVE;
			gameBoard.restart();
			gameGui.showGameBoard();
			gameGui.showInfo("Neustart: SpielerIn 1 am Zug");
		}
	}
}
