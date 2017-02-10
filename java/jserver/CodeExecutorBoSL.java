package jserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * This implementation executes primitive commands in the BoS language (BoSL).
 * No compilation is necessary. The commands are send directly to the Board
 * instance. Using the BoSL commands is provided mainly for test purposes.
 * 
 * @author Euler
 * 
 */
public class CodeExecutorBoSL extends CodeExecutor {
	private String fileName = "commands.bosl";

	private CodeExecutorBoSL() {
		super();
		// set compile mode for initialization
		setCompileMode(boSLText);
	}

	public CodeExecutorBoSL(Board board) {
		this();
		this.board = board;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see jserver.CodeExecutor#createTmpSourceFile(java.lang.String)
	 */
	@Override
	public String createTmpSourceFile(String codeInput) {
		String[] lines = codeInput.split("\\n");
		BufferedWriter fw;
		try {
			fw = new BufferedWriter(new FileWriter(fileName, false));
			for (String line : lines) {
				line = line.trim();
				if (!line.equals("")) {
					fw.write(line + "\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			board.setLastError("can not create file <<" + e.getMessage() + ">>");
			return null;
		}
		return fileName;
	}

	@Override
	public void stopExecution() {
	}

	@Override
	public String compileAndExecute(String fileName) {
		String result = "";

		try {
			List<String> commands = Files.readAllLines(Paths.get(fileName),
					Charset.defaultCharset());

			System.out.println("compileAndExecute: " + commands);
			errorCount = 0;
			for (String command : commands) {
				String r = board.receiveMessage(Board.FILTER_PREFIX + command);
				result += command;
				if (r != null) {
					System.out.println(r + " " + r.indexOf("ERROR"));
					if (r.indexOf("ERROR") >= 0) {
						++errorCount;
					}
					result += ": " + r;
				}
				result += "\n";
			}
		} catch (IOException e) {
			result = "Failed: " + e.getMessage();
		}
		for (ExecutorListener el : listeners) {
			el.endExecution();
		}
		return result;
	}

}
