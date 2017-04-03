package jserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

/**
 * Executes JS code
 * 
 * @author Euler
 * 
 */
public class CodeExecutorPython extends CodeExecutor {
	private String fileName = "commands.py";
	private String code = "";
	private String xName = "xSendAdapter";

	private CodeExecutorPython() {
		super();
		// set compile mode for initialization
		setCompileMode(pythonText);
	}

	public CodeExecutorPython(Board board) {
		this();
		this.board = board;
		xName = "XSend" + board.getMessages().getLocale().getLanguage().toUpperCase();
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see jserver.CodeExecutor#createTmpSourceFile(java.lang.String)
	 */
	@Override
	public String createTmpSourceFile(String codeInput) {
		// String language =
		// board.getMessages().getLocale().getLanguage().toUpperCase();
		// String className = "jserver.XSend" + language;
		// String[] methods = MethodExtractor.getMethodsJS(className, xName);
		//
		// System.out.println( className );
		//
		// code = "";
		// for (String method : methods) {
		// //System.out.println( method );
		// code += method + "\n";
		// }

		BufferedWriter fw;
		try {
			fw = new BufferedWriter(new FileWriter(fileName));
			fw.write("#Python code");
			fw.newLine();
			fw.write("import socket");
			fw.newLine();
			fw.write("import sys");
			fw.newLine();

			fw.write("def sendMessage( command ) :");
			fw.newLine();
			fw.write("	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)");
			fw.newLine();
			fw.write("	server_address = ('localhost', 1958)");
			fw.newLine();
			fw.write("	sock.connect(server_address)");
			fw.newLine();

			fw.write("	sock.sendall( bytes( command, 'UTF-8' ) )");
			fw.newLine();
			fw.write("	sock.close()");
			fw.newLine();
			
			writeFunction( fw, "color( n, f )", "str(n) + ' '  + str(f)" );
			writeFunction( fw, "color2( x, y, f )", "'# ' + str(x) + ' ' + str(y) + ' '  + str(f)" );
			writeFunction( fw, "form2( x, y, fo )", "'#fi ' + str(x) + ' ' + str(y) + ' '  + str(fo)" );
			writeFunction( fw, "formen( fo )", "'f '  + str(fo)" );
			fw.newLine();
			

			String[] lines = codeInput.split("\\n");
			for (String line : lines) {
				fw.write(line);
				fw.newLine();
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}

	private void writeFunction(BufferedWriter fw, String name, String body) throws IOException {
		fw.write("def "+ name + " :");
		fw.newLine();
		fw.write("	sendMessage(" + body + "+ '\\n' )");
		fw.newLine();
		
	}

	@Override
	public void stopExecution() {
	}

	@Override
	public String compileAndExecute(String fileName) {
		final StringBuffer result = new StringBuffer();

		ProcessBuilder pbRun;
		pbRun = new ProcessBuilder("py", fileName);

		pbRun.redirectErrorStream(true);
		try {
			Process executionProzess = pbRun.start();
			final InputStream is = executionProzess.getInputStream();

			System.out.println("Started execution process");

			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("Start reader thread");
					BufferedReader input = new BufferedReader(new InputStreamReader(is));
					String line;
					try {
						while ((line = input.readLine()) != null) {
							result.append(line);
						}
						input.close();
					} catch (IOException e) {
						board.setLastError("can not run thread <<" + e.getMessage() + ">>");
					}
				}
			}).start();

			final int exitValue = executionProzess.waitFor();
			System.out.println("Process ended with code " + exitValue);

			for (ExecutorListener el : listeners) {
				el.endExecution();
			}
		} catch (IOException | InterruptedException e) {
			result.append(e.getMessage());

		}
		return result.toString();

	}

	@Override
	public void showGeneratedCode(ResourceBundle messages) {
		showFileContent(fileName, messages.getString("generatedCode") + " - Python");

	}
}
