package quiz;

public class QuizField {
	private static final int MAX_COUNT = 4;
	Question question;
	int count = 1;

	public int getCount() {
		return count;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void incCount() {
		count  = Integer.min( MAX_COUNT, count + 1);		
	}
	
	public void decCount() {
		count  = Integer.max( 0, count - 1);		
	}
	
	
}
