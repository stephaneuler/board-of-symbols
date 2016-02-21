package plotter;

public class Sleep {

	public static void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void sleep(int min, int max) {
		try {
			Thread.sleep(min + (int) (Math.random() * (max - min)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
