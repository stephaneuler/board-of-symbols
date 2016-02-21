package plotter;


public class ThreadPlot implements Runnable {
	private int mode = 0;
	
	@Override
	public void run() {
		(new PlotDemo()).plot( mode);	
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	
}
