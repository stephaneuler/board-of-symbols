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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * This implementation executes Java code.
 * 
 * @author Euler
 * 
 */
public class CodeExecutorJavaServer extends CodeExecutor {
	String fileName = "ATest.java";
	private static String javacPath = "c:\\Program Files\\Java\\jdk1.8.0_131\\bin\\";
	private Thread sendThread = null;


	public CodeExecutorJavaServer() {
		setCompileMode(javaText);
	}

	public static String getJavacPath() {
		return javacPath;
	}

	public static void setJavacPath(String javacPath) {
		CodeExecutorJavaServer.javacPath = javacPath;
	}

	@Override
	public void stopExecution() {
		// System.out.println("CodeExcecutorJava::stopExecution");
		if (sendThread != null) {
			// System.out.println("CodeExcecutorJava::send interrupt");
			sendThread.interrupt();
		}
	}

	public void javaCompile(String fileName) throws IOException {
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
	    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(fileName));
	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, null, compilationUnits);
	    boolean success = task.call();
	    if (!success) {
	        List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
	        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
	            // read error dertails from the diagnostic object
	            System.out.println(diagnostic.getMessage(null));
	        }
	    }
	    fileManager.close();
	    System.out.println("Success: " + success);
	}
	
	@Override
	public String compileAndExecute(String ignorefileName) {
		StringBuffer result = new StringBuffer();
		String line;

//		try {
//			System.out.println(System.getProperty("java.home"));
//			System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_131");
//			javaCompile( "Atest.java");
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		
		//System.out.println("compileAndExecute fileName: " + fileName);
		boolean hasErrors = false;

		File jarTest = new File("jserver.jar");
		if (!jarTest.exists()) {
			System.out.println("jserver.jar nicht gefunden\n");
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return "";

		}

		new File("Atest.class").delete();

		ProcessBuilder pb;
		//System.out.println( "Path to javac: " + javacPath );
		String classPath = "." + File.pathSeparatorChar + "jserver.jar";
		//System.out.println( "classpath: " + classPath );
		pb = new ProcessBuilder(javacPath + "javac", "-cp", classPath, fileName);
		for (ExecutorListener el : listeners) {
			el.startCompilation();
		}
		try {
			Process p = pb.start();
			
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = error.readLine()) != null) {
				System.out.println(line );
				hasErrors = true;
			}
			error.close();
			p.waitFor();

		} catch (IOException | InterruptedException e) {
			hasErrors = true;
		}

		if (hasErrors) {
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return result.toString();
		} 
		
		for (ExecutorListener el : listeners) {
			el.endCompilation();
			el.startExecution();
		}

		File root = new File(".");
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
			Class<?> c = Class.forName("ATest", true, classLoader);
			final XSend xsend = (XSend) c.newInstance();
			final StringBuffer sb = new StringBuffer();
	
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			Future future = executorService.submit(new Runnable() {
			    public void run() {
			    	try {
						xsend.send();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			});
			String r = null;
			try {
			    r = (String) future.get(100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | ExecutionException e) {
			    e.printStackTrace();
			} catch (TimeoutException e) {
				System.out.println("Ausführung abgebrochen" );
			}
			
			executorService.shutdown();
			try {
			    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
			        executorService.shutdownNow();
			    } 
			} catch (InterruptedException e) {
			    executorService.shutdownNow();
			}

			
//			sendThread = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					String command = "";
//					try {
//						xsend.send();
//						command = xsend.getResult();
//						if (!command.matches("(okay)+")) {
//							System.out.println(command);
//							sb.append(command);
//						}
//					} catch (InterruptedException e) {
//						command = "Ausführung unterbrochen";
//					} catch (Exception |  Error e) {
//						e.printStackTrace(System.out);
//					}
//				}
//			});
//			sendThread.start();
//			try {
//				sendThread.join();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			result.append(sb);
		} catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException
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
		// boolean useHeaderFile = true;
		String language = "JJ";
		try {
			Files.copy(Paths.get("head.java"), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e1) {
			// useHeaderFile = false;
			try {
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName));
				fw.write("import jserver.*;\n");
				fw.write("import java.util.*;\n");

				// Probleme bei Abbruch, da exceptions gefangen werden!!!
				// fw.write("import plotter.Sleep;\n");

				fw.write("public class ATest extends XSend" + language + " {\n");
				insertColors(fw);
				fw.write("public void send()  throws InterruptedException { ");
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
			if ( isComplete( lines) ) {
				fw.write("mySend();");
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

		//System.out.println("created " + fileName);

		return fileName;
	}

	private boolean isComplete(String[] lines) {
		for (String line : lines) {
			if (line.startsWith("@Complete")) return true;
		}
		return false;
	}

	private void insertColors(BufferedWriter fw) {
		CodeDB codeDB = new CodeDB();

		List<String> colors = codeDB.getColorNames();
		if (colors != null) {
			for (String s : colors) {
				try {
					fw.write(" final int " + s + " = " + codeDB.getColorValue(s) + "; \n");
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
		String template = "@Complete" + System.lineSeparator(); 
		template += ""+ System.lineSeparator();
		template += "// methode to start with"+ System.lineSeparator();
		template += "void mySend() {"+ System.lineSeparator();
		template += "	// replace with own code"+ System.lineSeparator();
		template += "	System.out.println(\"BoS\");"+ System.lineSeparator();
		template += "}"+ System.lineSeparator();
		return template;
	}

}
