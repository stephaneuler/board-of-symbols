package jserver;

/**
 * The listener interface for receiving compilation and execution events. The
 * class that is interested in processing such an event implements this
 * interface, and the object created with that class is registered with a
 * CodeExecutor, using the executor's addListener method.
 * 
 * @author Euler
 * 
 */

public interface ExecutorListener {
	public void startCompilation();

	public void failedCompilation();

	public void endCompilation();

	public void startExecution();

	public void endExecution();

}
