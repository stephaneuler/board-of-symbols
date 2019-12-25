package quiz;

/**
 * Abstract class for all types of questions. 
 * 
 * @author Euler
 *
 */
public abstract class Question {
	// all subclasses have a question text
	String question; 
	
	public String getQuestion() {
		return question;
	}

	// all subclasses must have a ask method, 
	// but implementation is different
	public abstract boolean ask(); 

}
