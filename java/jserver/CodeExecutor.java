package jserver;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

/**
 * This class defines common behaviors for code executors. 
 * A subclass has to implement the details for a given programming language.
 * 
 * @author Euler
 *
 */
public abstract class CodeExecutor {

	protected static final String boSLText = "BoSL";
	protected static final String gccText = "GNU_C";
	protected static final String devCText = "DEV_C";
	protected static final String vsText = "MS_VS";
	protected static final String javaText = "Java";
	protected static final String logFileName = "v2.log";
	protected Board board;
	protected List<ExecutorListener> listeners = new ArrayList<ExecutorListener>();
	protected int errorCount = 0;
	protected String propertieFile = "vc.properties";
	protected String compileMode = "undefined";
	protected String exeName = "commandgenerator.c";
	protected String exeCommand = null;
	protected JTextArea messageField;

	public CodeExecutor() {
		super();
	}

	public void addListener(ExecutorListener el) {
		listeners.add(el);
	}

	public void setMessageField(JTextArea messageField) {
		this.messageField = messageField;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public String getCompileMode() {
		return compileMode;
	}

	public void setCompileMode(String compileMode) {
		if (compileMode != null) {
			this.compileMode = compileMode;
		}
	}

	public String getExeName() {
		return exeName;
	}

	public void setExeName(String exeName) {
		this.exeName = exeName;
	}

	public String getExeCommand() {
		return exeCommand;
	}

	public void setExeCommand(String exeCommand) {
		this.exeCommand = exeCommand;
	}


	abstract public void stopExecution();

	abstract public String compileAndExecute(String fileName);

	abstract public String createTmpSourceFile(String codeInput);

	public static CodeExecutor getExecutor(String mode, Board board,
			CodeWindow codeWindow) {
		CodeExecutor e = null;
		if( mode.equals( javaText ) )  {
			e = new CodeExecutorJava(board);
		} else if( mode.equals( boSLText ) ) {
			e = new CodeExecutorBoSL(board);
		} else {
			e = new CodeExecutorC(board);
		}
		e.setCompileMode( mode );
		e.addListener(codeWindow);
		return e;
	}

	public static boolean isCodeExecuterSelection(String cmd) {
		return 	cmd.equals(CodeExecutor.gccText)
				| cmd.equals(CodeExecutor.vsText)
				| cmd.equals(CodeExecutor.devCText )
				| cmd.equals(CodeExecutor.boSLText )
				| cmd.equals(CodeExecutor.javaText);
	}

}