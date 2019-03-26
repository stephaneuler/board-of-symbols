package jserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor {
	private List<String> outputLines = new ArrayList<>();
	private List<String> errorLines = new ArrayList<>();
	private boolean verbose = false;

	public List<String> getOutputLines() {
		return outputLines;
	}

	public List<String> getErrorLines() {
		return errorLines;
	}

	void executeCommand(String[] command) {
		ProcessBuilder pb;
		String line;

		if (verbose) {
			System.out.println("Execute: " + Arrays.toString(command));
		}

		outputLines.clear();
		errorLines.clear();

		pb = new ProcessBuilder(command);
		try {
			Process p = pb.start();

			new Thread() {
				public void run() {
					BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					try {
						while ((line = output.readLine()) != null) {
							outputLines.add(line);
						}
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (verbose) {
						System.out.println("Output: " + outputLines);
					}
				}
			}.start();

			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = error.readLine()) != null) {
				errorLines.add(line);
			}
			error.close();
			if (verbose) {
				System.out.println("Errors: " + errorLines);
			}
			p.waitFor();

		} catch (IOException | InterruptedException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
