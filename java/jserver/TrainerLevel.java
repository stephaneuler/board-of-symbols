package jserver;

public class TrainerLevel {
	Mode mode;
	boolean randomForm;
	boolean useBackGroundForm;
	String topic = "default";

	public String getTopic() {
		return topic;
	}

	public TrainerLevel(Mode mode, boolean randomForm, boolean useBackGroundForm, String topic) {
		this(mode, randomForm, useBackGroundForm);
		this.topic = topic;
	}

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

	public boolean hasSameTopic(TrainerLevel level2) {
		return topic.equals( level2.topic );
	}
}
