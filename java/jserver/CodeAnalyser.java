package jserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeAnalyser {
	String code;
	CommandExecutor commandExecutor = new CommandExecutor();

	public CodeAnalyser(String code) {
		super();
		this.code = code;
//		String[] lines = {"a", "b"};
//		Arrays.stream(lines).forEach(line -> System.out.println( line));
	}

	public String getResult() {
		String text = "** Code Analyser" + Board.LS;
		CodeLayouter codeLayouter = new CodeLayouter();
		codeLayouter.autoLayout(code);

		text += codeLayouter.getNumLines() + " lines of code" + Board.LS;
		text += codeLayouter.getNumComment() + " comments" + Board.LS;
		text += codeLayouter.getNumInlineComment() + " inline comments" + Board.LS;
		text += codeLayouter.getNumBlocks() + " {}-blocks" + Board.LS;
		text += codeLayouter.getMaxIndent() + " max indent" + Board.LS;
		text += CodeExecutorJava.isComplete(code) + " uses methods" + Board.LS;
		return text;
	}

	public Map<String, String> getResultMap() {
		Map<String, String> result = new HashMap<>();
		CodeLayouter codeLayouter = new CodeLayouter();

		codeLayouter.autoLayout(code);

		result.put("lines", "" + codeLayouter.getNumLines());
		result.put("comments", "" + codeLayouter.getNumComment());
		result.put("inline-comments", "" + codeLayouter.getNumInlineComment());
		result.put("blocks", "" + codeLayouter.getNumBlocks());
		result.put("max-indent", "" + codeLayouter.getMaxIndent());
		result.put("uses-methods", "" + (CodeExecutorJava.isComplete(code)? "1":"0"));
		return result;
	}

	int checkstyle(XMLProtocol protocol, CounterMap<String> warningCount) {
		String result = "";
		String[] command = { CodeExecutorJava.getJavacPath() + "java", "-jar", "checkstyle-8.11-all.jar", "-c",
				"bos_checks.xml", "ATest.java" };
		int lines = 0;
		commandExecutor.executeCommand(command);

		for (String line : commandExecutor.getOutputLines()) {
			if (line.startsWith("[")) {
				result += line + Board.LS;
				protocol.writeCData("style-warning", "" + line);
				++lines;
			}
			int lastBracket = line.lastIndexOf("[");
			if (lastBracket >= 0) {
				String warningType = line.substring(lastBracket + 1, line.length() - 1);
				// System.out.println( warningType);
				warningCount.put(warningType);
			}
		}
		System.out.println(result);
		return lines;
	}

	public void errorProne(XMLProtocol protocol) {
		String[] command = { "javac", "-cp", "error_prone_ant-2.3.1.jar;release/jserver.jar", "-Xplugin:ErrorProne",
				"-XDcompilePolicy=byfile", "ATest.java" };
		commandExecutor.executeCommand(command);
		protocol.writeInfo("command", command[0]);
		writeLines("errorProne", commandExecutor.getOutputLines(), protocol);
		writeLines("errorProne",commandExecutor.getErrorLines(), protocol);
	}

	public void runPMD(XMLProtocol protocol) {
		String[] command = { "pmd.bat", "-d", "ATest.java", "-R", "bos-rules.xml", "-f", "text" };
		commandExecutor.executeCommand(command);
		protocol.writeInfo("command", command[0]);
		writeLines("PMD", commandExecutor.getOutputLines(), protocol, ".*ATest.java.*");
		writeLines("PMD", commandExecutor.getErrorLines(), protocol, ".*ATest.java.*");
	}

	private void writeLines(String key, List<String> lines, XMLProtocol protocol, String filter) {
		lines.stream().filter(s -> s.matches(filter)).forEach(line -> protocol.writeCData(key, line));
	}

	private void writeLines(String key, List<String> lines, XMLProtocol protocol) {
		for (String line : lines) {
			protocol.writeCData(key, line);
		}
	}

	private String escapeSpecials(String line) {
		String result = line.replaceAll("@", "&amp;");
		return result;
	}

}
