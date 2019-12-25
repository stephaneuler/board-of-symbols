package quiz;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Questioner {
	List<Question> questions = new ArrayList<>();
	String info;

	public Questioner() {
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	void loadQuestions(String fileName) {
		info = "keine Infos";
		Path path = FileSystems.getDefault().getPath("ressources", fileName);
		System.out.println(path.toAbsolutePath());
		try {
			List<String> lines = Files.readAllLines(path);
			for (String line : lines) {
				line = line.trim();
				System.out.println(line);
				if ( line.startsWith("!")) {
					info = line.substring(1);
				} else if ( line.startsWith("#")) {
					continue;
				} else {
					questions.add(parseLine(line));
				}
			}
		} catch (MalformedInputException e) {
			JOptionPane.showMessageDialog(null, "Datei " + fileName + ": Problem mit Zeichendarstellung",  "Laden der Fragen", JOptionPane.ERROR_MESSAGE );	
			System.out.println( e );
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Datei " + fileName + " " + " nicht gefunden", "Laden der Fragen",
					JOptionPane.ERROR_MESSAGE);
			System.out.println( e );
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Laden der Fragen", JOptionPane.ERROR_MESSAGE);
		}
	}

	private Question parseLine(String line) throws Exception {
		String[] parts = line.split("#");
		if( parts.length == 1 ) {
			throw new Exception("cannot parse line " + line);
		}
		String typ = parts[0];
		String questionText = parts[1];
		switch (typ) {
		case "YesNo":
			return new YesNoQuestion(questionText, Boolean.parseBoolean(parts[2]));
		case "Open":
			return new OpenQuestion(parts[1], parts[2]);
		case "RegOpen":
			return new RegOpenQuestion(parts[1], parts[2]);
		case "Correct":
			return new CorrectQuestion(parts[1], parts[2]);
		case "Options":
			return new OptionsQuestion(parts[1], Arrays.copyOfRange(parts, 3, parts.length),
					Integer.parseInt(parts[2]));
		case "MultiOptions":
			return new MultiOptionsQuestion(parts[1], Arrays.copyOfRange(parts, 3, parts.length),
					parts[2]);
		case "Dyn":
			return new DynamicQuestion( parts[1] );
		default:
			throw new Exception("don't know question type " + parts[0]);
		}

	}

	public static void main(String[] args) {
		Questioner q = new Questioner();
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public boolean askQuestion() {
		Question q = questions.get((int) (Math.random() * questions.size()));
		boolean result = q.ask();

		if (result) {
			JOptionPane.showMessageDialog(null, "Richtig", "Bewertung", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Leider Falsch", "Bewertung", JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}

	public int getNumberQuestions() {
		return questions.size();
	}

	public void removeAllQuestions() {
		questions.clear();
		
	}

}
