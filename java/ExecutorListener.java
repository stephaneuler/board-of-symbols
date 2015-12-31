package jserver;

public interface ExecutorListener {
	public void startCompilation();
	public void failedCompilation();
	public void endCompilation();
	public void startExecution();
	public void endExecution();

}
