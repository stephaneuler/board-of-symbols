package jserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This implementation executes Java code. 
 * 
 * @author Euler
 *
 */
public class CodeExecutorJava extends CodeExecutor {
	String fileName = "ATest.java";
	private Thread sendThread = null;

	// public static void main(String[] args) {
	// CodeExecutorJava ce = new CodeExecutorJava();
	// ce.createTmpSourceFile("");
	// System.out.println(ce.compileAndExecute(""));
	// }

	public CodeExecutorJava(Board world) {
		this();
		this.board = world;
	}

	public CodeExecutorJava() {
		setCompileMode( javaText );
	}

	@Override
	public void stopExecution() {
		if( sendThread != null ) {
			sendThread.interrupt();
		}
	}

	@Override
	public String compileAndExecute(String ignorefileName ) {
		StringBuffer result = new StringBuffer();
		String line;

		System.out.println("compileAndExecute fileName: " + fileName);
		boolean hasErrors = false;

		 new File("Atest.class").delete();

		// Compile source file.
		// System.setProperty("java.home",
		// "C:\\Program Files\\Java\\jdk1.7.0_17");
		// System.out.println(System.getProperty("java.home"));
		// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// System.out.println( compiler );
		// compiler.run(null, null, null, fileName);
		//

		ProcessBuilder pb;
		pb = new ProcessBuilder("javac", "-cp", ".;jserver.jar", fileName);
		for (ExecutorListener el : listeners) {
			el.startCompilation();
		}
		try {
			Process p = pb.start();

//			BufferedReader input = new BufferedReader(new InputStreamReader(
//					p.getInputStream()));
//			while ((line = input.readLine()) != null) {
//				System.out.println(line);
//			}
//			input.close();

			BufferedReader error = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = error.readLine()) != null) {
				//result.append(line + "\n");
				messageField.append(line + "\n");
				hasErrors = true;
			}
			error.close();
			p.waitFor();

		} catch (IOException | InterruptedException e) {
			//result.append(e.getMessage() + "\n");
			messageField.append(e.getMessage() + "\n");
			hasErrors = true;
			// return result.toString();
		}

		if (hasErrors) {
			//result.append("compile failed\n");
			messageField.append("compile gescheitert\n");
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return result.toString();
		} else {
			messageField.append("ausf�hren \n");
		}

		for (ExecutorListener el : listeners) {
			el.endCompilation();
			el.startExecution();
		}

		File root = new File(".");
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { root.toURI()
					.toURL() });
			Class<?> c = Class.forName("ATest", true, classLoader);
			final XSend xsend = (XSend) c.newInstance();
			final StringBuffer sb = new StringBuffer();
			sendThread = new Thread(new Runnable() {
				@Override
				public void run() {
					xsend.setBoard(board);
					xsend.setUp( messageField );
					String command = "";
					try {
						xsend.send();
						command = xsend.getResult();
					} catch (InterruptedException e) {
						command = "Ausf�hrung unterbrochen";
					}
					sb.append(command);
				}
			});
			sendThread.start();
			try {
				sendThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xsend.setDown();
			result.append( sb );
		} catch (MalformedURLException | ClassNotFoundException
				| IllegalAccessException | IllegalArgumentException
				| InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (ExecutorListener el : listeners) {
			el.endExecution();
		}
		return result.toString();
	}

	@Override
	public String createTmpSourceFile(String codeInput) {
//		boolean useHeaderFile = true;
		try {
			Files.copy(Paths.get("head.java"), Paths.get(fileName),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e1) {
//			useHeaderFile = false;
			try {
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName ) );
				fw.write("import jserver.*;\n");
				fw.write("public class ATest extends XSend {\n" );
				fw.write("public void send()  throws InterruptedException { ;");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName,
					true));
			insertColors(fw);
			//fw.write("result =\"\";");
			String[] lines = codeInput.split("\\n");
			for (String line : lines) {
				fw.write(line + "\n");
			}
			//fw.write("return result;\n");
			fw.write("}");
			fw.write("}");
			fw.newLine();
			fw.close();
		} catch (IOException e) {
			board.setLastError("can not complete file <<" + e.getMessage()
					+ ">>");
			System.out.println("cancel createTmpCFile: " + e.getMessage());
			return null;
		}

		System.out.println("created " + fileName);

		return fileName;
	}

	private void insertColors(BufferedWriter fw) {
		CodeDB codeDB = new CodeDB();

		List<String> colors = codeDB.getColorNames();
		for (String s : colors) {
			try {
				fw.write(" final int " + s + " = " + codeDB.getColorValue(s)
						+ "; \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	

}