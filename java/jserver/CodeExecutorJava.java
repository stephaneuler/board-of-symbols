package jserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

import plotter.Sleep;

/**
 * This implementation executes Java code.
 * 
 * @author Euler
 * 
 */
public class CodeExecutorJava extends CodeExecutor {
	String fileName = "ATest.java";
	private static String javacPath = "";
	private Thread sendThread = null;
	StringBuffer result = new StringBuffer();
	boolean hasErrors = false;

	// public static void main(String[] args) {
	// CodeExecutorJava ce = new CodeExecutorJava();
	// ce.createTmpSourceFile("");
	// System.out.println(ce.compileAndExecute(""));
	// }

	public CodeExecutorJava(Board board) {
		this();
		this.board = board;
	}

	public CodeExecutorJava() {
		setCompileMode(javaText);
	}

	public static String getJavacPath() {
		return javacPath;
	}

	public static void setJavacPath(String javacPath) {
		CodeExecutorJava.javacPath = javacPath;
	}

	@Override
	public void stopExecution() {
		// 
		System.out.println("CodeExcecutorJava::stopExecution");
		if (sendThread != null) {
			// 
			System.out.println("CodeExcecutorJava::send interrupt");
			sendThread.interrupt();
		}
	}

	@Override
	public String compileAndExecute(String ignorefileName) {
		hasErrors = false;
		result = new StringBuffer();
		String line;
		
		if (verbose) {
			System.out.println("compileAndExecute fileName: " + fileName);
		}

		File jarTest = new File("jserver.jar");
		if (!jarTest.exists()) {
			messageField.append("jserver.jar nicht gefunden\n");
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return "";

		}

		new File("Atest.class").delete();

		// Compile source file.
		// System.setProperty("java.home",
		// "C:\\Program Files\\Java\\jdk1.7.0_17");
		// System.out.println(System.getProperty("java.home"));
		// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// System.out.println( compiler );
		// compiler.run(null, null, null, fileName);
		//

		compile();

		if (hasErrors) {
			messageField.append(board.getMessages().getString("failedCompile") + "\n");
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return result.toString();
		} else {
			messageField.append(board.getMessages().getString("compileExecute") + "\n");
		}

		for (ExecutorListener el : listeners) {
			el.endCompilation();
			el.startExecution();
		}

		execute();

		for (ExecutorListener el : listeners) {
			el.endExecution();
		}
		return result.toString();
	}

	private void execute() {
		File root = new File(".");
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
			// System.out.println( "got classLoader " + classLoader);
			// Class<?> c = Class.forName("ATest", true, classLoader);
			Class<?> c = classLoader.loadClass("ATest");
			// System.out.println( "got class " + c);
			final XSend xsend = (XSend) c.getDeclaredConstructor().newInstance();
			final StringBuffer sb = new StringBuffer();
			sendThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// System.out.println( "Start run ...");
					xsend.setBoard(board);
					xsend.setUp(messageField);
					String command = "";
					try {
						xsend.send();
						command = xsend.getResult();
						if (!command.matches("(okay)+")) {
							//System.out.println("::: " + command);
							System.out.println( command.replaceAll("okay", ""));
							//sb.append(command.replaceAll("okay", ""));
						}
					} catch (InterruptedException e) {
						command = "Ausführung unterbrochen";
					} catch (Exception | Error e) {
						// copy message so that it appears in the result field
						e.printStackTrace(System.out);
					}
				}
			});
			// System.out.println( "got send ");
			sendThread.start();
			try {
				sendThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			xsend.setDown();
			result.append(sb);
		} catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException
				| InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
			messageField.append(e1.getMessage() + "\n");
		}
	}

	private void compile() {
		String line;
		ProcessBuilder pb;
		// System.out.println( "Path to javac: " + javacPath );
		String classPath = "." + File.pathSeparatorChar + "jserver.jar";
		// System.out.println( "classpath: " + classPath );
		pb = new ProcessBuilder(javacPath + "javac", "-cp", classPath, fileName);
		for (ExecutorListener el : listeners) {
			el.startCompilation();
		}
		try {
			Process p = pb.start();
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = error.readLine()) != null) {
				// result.append(line + "\n");
				messageField.append(line + "\n");
				hasErrors = true;
			}
			error.close();
			p.waitFor();

		} catch (IOException | InterruptedException e) {
			// result.append(e.getMessage() + "\n");
			messageField.append(e.getMessage() + "\n");
			hasErrors = true;
			// return result.toString();
		}

	}

	private String getLanguage() {
		String language = board.getMessages().getLocale().getLanguage().toUpperCase();
		if (board.getCodeWindow() == null || board.getCodeWindow().getSnippetLocale() == null) {
			return language;
		}
		String localeFromSnippet = board.getCodeWindow().getSnippetLocale();
		return languageFromLocale( localeFromSnippet );
	}

	private String languageFromLocale(String localeFromSnippet) {
		String[] parts = localeFromSnippet.split("_");
		return parts[0].toUpperCase();
	}

	@Override
	public String createTmpSourceFile(String codeInput) {
		// get language from GUI
		return createTmpSourceFile(codeInput, getLanguage());
	}

	public String createTmpSourceFile(String codeInput, String locale) {
		String language = languageFromLocale(locale);
		// boolean useHeaderFile = true;
		try {
			Files.copy(Paths.get("head.java"), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e1) {
			// useHeaderFile = false;
			try {
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName));
				fw.write("import java.util.*;" + Board.LS);
				fw.write("import jserver.*;" + Board.LS);
				fw.write(Board.LS);

				// Probleme bei Abbruch, da exceptions gefangen werden!!!
				// fw.write("import plotter.Sleep;\n");

				fw.write("public class ATest extends XSend" + language + " {" + Board.LS);
//				insertColors(fw);
				fw.write(Board.LS);
				fw.write("/**" + Board.LS);
				fw.write(" * Die methode zum senden der BoS befehle." + Board.LS);
				fw.write("* @throws InterruptedException kann vorkommen" + Board.LS);
				fw.write("*/" + Board.LS);

				fw.write("@Override public void send()  throws InterruptedException { " + Board.LS);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			// fw.write("result =\"\";");
			String[] lines = codeInput.split("\\n");
			if (isComplete(lines)) {
				fw.write("mySend();");
				fw.newLine();
				fw.write("}");
				fw.newLine();
				for (String line : lines) {
					if (!line.startsWith("@Complete")) {
						fw.write(line);
						fw.newLine();
					}
				}
			} else {
				for (String line : lines) {
					fw.write(line);
					fw.newLine();
				}
				fw.write("}");
				fw.newLine();
			}

			// close class
			fw.write("}");
			fw.newLine();
			fw.close();
		} catch (IOException e) {
			board.setLastError("can not complete file <<" + e.getMessage() + ">>");
			System.out.println("cancel createTmpCFile: " + e.getMessage());
			return null;
		}

		if (verbose) {
			System.out.println("created " + fileName);
		}

		return fileName;
	}

	public static boolean isComplete(String[] lines) {
		for (String line : lines) {
			if (line.startsWith("@Complete"))
				return true;
		}
		return false;
	}

	public static boolean isComplete(String code) {
		return isComplete(code.split("\n", -1));
	}

	private void insertColors(BufferedWriter fw) {
		CodeDB codeDB = new CodeDB();

		List<String> colors = codeDB.getColorNames();
		if (colors != null) {
			for (String s : colors) {
				try {
					fw.write("  public static final int " + s + " = " + codeDB.getColorValue(s) + ";" + Board.LS);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void showGeneratedCode(ResourceBundle messages) {
		showFileContent(fileName, messages.getString("generatedCode") + " - Java");
	}

	@Override
	public String getCompleteTemplate() {
		String template = "@Complete" + Board.LS;
		template += "" + Board.LS;
		template += "// methode to start with" + Board.LS;
		template += "void mySend() {" + Board.LS;
		template += "	// replace with own code" + Board.LS;
		template += "	System.out.println(\"BoS\");" + Board.LS;
		template += "}" + Board.LS;
		return template;
	}

	@Override
	public String getInteractiveTemplate() {
		String template = String.join(Board.LS, "@Complete",
				"void mySend() throws InterruptedException {", "   board.receiveMessage(\">>showInput\");",
				"   for( ;; ) Thread.sleep( 100 );", "}", "", "public void receiveCommand( String s ) {",
				"	// hier eigenen Code einbauen", "	text2(3,3, \"::\" + s + \"::\");", "}");

		return template;
	}

}
