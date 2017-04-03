package jserver;

public class TrainerLevel {
	Mode mode;
	boolean randomForm;
	boolean useBackGroundForm;

	public TrainerLevel(Mode mode, boolean randomForm, boolean useBackGroundForm) {
		super();
		this.mode = mode;
		this.randomForm = randomForm;
		this.useBackGroundForm = useBackGroundForm;
	}

	@Override
	public String toString() {
		String s = "" + mode;
		if( randomForm ) {
			s += " F?";
		}
		if( useBackGroundForm ) {
			s += " BG";
		}
		return s;
	}
}
