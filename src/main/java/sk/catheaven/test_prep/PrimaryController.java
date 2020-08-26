package sk.catheaven.test_prep;

import com.sun.javafx.scene.control.skin.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Main app controller, that manipulates question management.
 * @author catlord
 */
public class PrimaryController implements Initializable {
	private int correctlyAnswered;							// keeps track of correctly answered questions
	private final String incorrectAnswerMultiChoice = "~";
	private final String correctAnswerMultiChoice = "*";
	
	@FXML Button answerButton;
	@FXML Button nextButton;
	@FXML Label questionLabel;
	@FXML VBox answersBox;
	
	private List<Question> questions;
	private int currentQuestionIndex = 0;
	
	/**
	 * When user checks for correctness of answer, this method is fired (even though 
	 * netbeans suggests that this method is not called).
	 * @throws IOException 
	 */
    @FXML
    private void answer() throws IOException {
		if(questions.get(currentQuestionIndex).check())
			correctlyAnswered++;
		
		nextButton.setVisible(true);
		answerButton.setVisible(false);
	}

	/**
	 * Loads next question after user checks answer for current one.
	 */
	@FXML
	public void next(){
		nextButton.setVisible(false);
		answerButton.setVisible(true);
		answersBox.getChildren().clear();
		
		questions.get(currentQuestionIndex).reset();		// remove coloring and selections
		currentQuestionIndex++;
		
		if(currentQuestionIndex >= questions.size()){
			questionLabel.setText("END OF QUIZ");
			// time for test summary
			System.out.println("Correctly answered " + ((double)correctlyAnswered / (double)questions.size()) );
		}
		
		Question current = questions.get(currentQuestionIndex);
		questionLabel.setText(current.getQuestion());
		
		for(CheckBox box : current.getBoxes()){
			answersBox.getChildren().add(box);
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		correctlyAnswered = 0;
		questions = new ArrayList<>();
	
		InputStream text;
		try {
			text = (InputStream) PrimaryController.class.getResource("/sk/catheaven/test_prep/test").getContent();
		} catch (IOException e) {
			System.err.println("Failed to load input file 'test'");
			return;
		}
		
		java.util.Scanner utilScanner = new java.util.Scanner(text).useDelimiter("\n");
		Pattern questionPattern = Pattern.compile("^\\d+\\..*");

		Matcher matcher;
		List<String> correctAnswers = new ArrayList<>();
		List<String> incorrectAnswers = new ArrayList<>();
		String question = "?";
		
		while(utilScanner.hasNext()) {
			String line = utilScanner.next().replaceAll("\n", "");
			
			matcher = questionPattern.matcher(line);
			
			// we have new question
			if(matcher.lookingAt()){
				question = line;
				correctAnswers = new ArrayList<>();
				incorrectAnswers = new ArrayList<>();
			}
			else{
				if(line.isEmpty()  ||  line.isBlank() ||  line.equals("")){
					System.out.println("Empty question line");
					if( ! question.isEmpty()){
						questions.add(new Question(question, correctAnswers, incorrectAnswers));
						question = "";
					}	
					continue;
				}
				
				if(line.contains(incorrectAnswerMultiChoice)){
					String incLine = line.replaceAll("^[ ]*~[ ]*", "");
					incorrectAnswers.add(incLine);
				}
				else if(line.contains(correctAnswerMultiChoice)){
					String cLine = line.replaceAll("^[ ]*\\*[ ]*", "");
					correctAnswers.add(cLine);
				}
				else{
					System.out.println("? Unknown line type ! `" + line + "`");
				}
			}
		}
		
		answerButton.setVisible(false);
		questionLabel.setText("Loaded " + questions.size() + " questions");
	}
}