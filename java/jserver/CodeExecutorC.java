package jserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import jserver.OsCheck.OSType;

/**
 * This implementation executes C code. Currently Gnu CC, Dev Cpp and MS Visual
 * Studio are supported.
 * 
 * @author Euler
 *
 */
public class CodeExecutorC extends CodeExecutor {
	private static String gccPath = "c:\\Dev-Cpp\\bin\\gcc";
	private Properties vsProperties = new Properties();
	private Process executionProzess;

	public CodeExecutorC() {
		super();
		// set compile mode for initialization
		setCompileMode(compileMode);
	}

	public CodeExecutorC(Board board) {
		this();
		this.board = board;
	}

	public static String getGccPath() {
		return gccPath;
	}

	public static void setGccPath(String gccPath) {
		if( gccPath != null ) {
			CodeExecutorC.gccPath = gccPath;
		}
	}

	// public static void main(String[] args) {
	// CodeExecutor ce = new CodeExecutor();
	// ce.compileAndExecute("test.c");
	// }

	@Override
	public void setCompileMode(String compileMode) {
		super.setCompileMode(compileMode);
		if (compileMode != null) {
			if (compileMode.equals(vsText)) {
				readVSProp();
			}
		}
	}

	void readVSProp() {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertieFile));
			vsProperties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println("property file " + propertieFile + " not found");
			JOptionPane.showMessageDialog(null, "property file " + propertieFile + " not found", "No property file",
					JOptionPane.ERROR_MESSAGE);

		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error while reading VS-File",
					JOptionPane.ERROR_MESSAGE);
		}
		System.out.println(vsProperties.getProperty("path"));

	}

	@Override
	public String createTmpSourceFile(String codeInput) {

		codeInput = codeInput.trim();
		// addMode: add head and trail
		boolean addMode = ! codeInput.startsWith("#include");
		//boolean addMode = ! codeInput.matches("^#include *\"bos\\.h.*");

		if (addMode) {
			try {
				Files.copy(Paths.get("head.c"), Paths.get(exeName), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e1) {
				// e1.printStackTrace();

				++errorCount;
				board.setLastError("can not copy file <<" + e1.getMessage() + ">>");
				System.out.println("cancel createTmpCFile: " + e1.getMessage());
				return null;
			}
		} else {
			JOptionPane.showMessageDialog(null, "using direct mode" , "direct mode",
					JOptionPane.INFORMATION_MESSAGE);


		}

		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(exeName, addMode));
			String[] lines = codeInput.split("\\n");
			for (String line : lines) {
				fw.write(line + "\n");
			}
			if (addMode) {
				fw.write("}");
				fw.newLine();
			}
			fw.close();
		} catch (IOException e) {
			board.setLastError("can not complete file <<" + e.getMessage() + ">>");
			System.out.println("cancel createTmpCFile: " + e.getMessage());
			return null;
		}

		return exeName;
	}

	@Override
	public void showGeneratedCode(ResourceBundle messages) {
		showFileContent( exeName, messages.getString("generatedCode") + " - C");
	}


	@Override
	public String compileAndExecute(String fileName) {
		StringBuffer result = new StringBuffer();
		String line;

		// System.out.println( "compileAndExecute fileName: " + fileName );
		boolean hasErrors = false;
		String exeName = null;
		OSType osType = OsCheck.getOperatingSystemType();
		String exeUseCommand = null;
		if (exeCommand != null) {
			exeUseCommand = exeCommand;
		} else {
			if (osType == OSType.Windows) {
				exeName = fileName.replaceAll("\\.c$", ".exe");
				exeUseCommand = exeName;
			} else if (osType == OSType.MacOS || osType == OSType.Linux) {
				exeName = "./" + fileName.replaceAll("\\.c$", "");
				exeUseCommand = exeName;
			} else {
				result.append("unknown operating system  " + System.getProperty("os.name", "generic") + "\n");
				for (ExecutorListener el : listeners) {
					el.failedCompilation();
				}
				return result.toString();
			}
		}

		ProcessBuilder pb;
		if (compileMode.equals(gccText)) {
			pb = new ProcessBuilder("gcc", fileName, "-o", exeName);
		} else if (compileMode.equals(devCText)) {
			pb = new ProcessBuilder(gccPath, fileName, "-o", exeName);
		} else if (compileMode.equals(vsText)) {
			// pb = new ProcessBuilder("cmd.exe", "/C", "start", "cl", fileName,
			// ">", "vslog.txt", "2>&1");
			pb = new ProcessBuilder("mscomp.bat");
			Map<String, String> env = pb.environment();
			System.out.println(env);
			for (Map.Entry<?, ?> entry : vsProperties.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				env.put(key, value);
			}
			String path = env.get("Path");
			System.out.println(path);
			// path += ";" + vsProperties.getProperty("path");
			// System.out.println(path);
			// env.put("Path", path);
			// System.out.println(env);
			// System.out.println(pb.directory());
			// pb.directory(new File( vsProperties.getProperty("path")));
			// System.out.println(pb.directory());
		} else {
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return "kein gültiger Compiler";
		}

		for (ExecutorListener el : listeners) {
			el.startCompilation();
		}
		try {
			Process p = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();

			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = error.readLine()) != null) {
				result.append(line + "\n");
				hasErrors = true;
			}
			error.close();

		} catch (IOException e) {
			result.append(e.getMessage() + "\n");
			hasErrors = true;
		}

		// for VS check log file, should be empty (= without errors)
		if (compileMode.equals(vsText)) {
			try {
				List<String> lines = Files.readAllLines(Paths.get(logFileName), Charset.defaultCharset());
				if (lines.size() > 1) {
					for (int i = 0; i < lines.size(); i++) {
						result.append("\n" + lines.get(i));
					}
					for (ExecutorListener el : listeners) {
						el.failedCompilation();
					}
					// return result.toString();
					hasErrors = true;
				}
			} catch (IOException e) {
				board.setLastError("can not read log file <<" + e.getMessage() + ">>");
				result.append("\nsiehe Datei v2.log\n");
				hasErrors = true;
			}
		}
		if (hasErrors) {
			result.append("compile failed\n");
			for (ExecutorListener el : listeners) {
				el.failedCompilation();
			}
			return result.toString();
		}

		for (ExecutorListener el : listeners) {
			el.endCompilation();
			el.startExecution();
		}

		final List<String> commands = new ArrayList<String>();

		ProcessBuilder pbRun = new ProcessBuilder(exeUseCommand);

		pbRun.redirectErrorStream(true);
		try {
			executionProzess = pbRun.start();
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
							// System.out.println(line);
							commands.add(line);
						}
						input.close();
					} catch (IOException e) {
						board.setLastError("can not run thread <<" + e.getMessage() + ">>");
					}
				}
			}).start();

			final int exitValue = executionProzess.waitFor();
			System.out.println("Process ended with code " + exitValue);
			System.out.println("#lines:  " + commands.size());

			for (ExecutorListener el : listeners) {
				el.endExecution();
			}
			// now send commands
			errorCount = 0;
			for (String command : commands) {
				String r = board.receiveMessage(command);
				result.append(command);
				if (r != null) {
					System.out.println(r + " " + r.indexOf("ERROR"));
					if (r.indexOf("ERROR") >= 0) {
						++errorCount;
					}
					result.append(": " + r);
				}
				result.append("\n");
				// System.out.println(command);
			}
		} catch (IOException | InterruptedException e) {
			result.append(e.getMessage());
			// result += "\ndirectory: " + pbRun.directory();

		}
		return result.toString();

	}

	@Override
	public void stopExecution() {
		if (executionProzess != null) {
			executionProzess.destroy();
		}
	}

}
