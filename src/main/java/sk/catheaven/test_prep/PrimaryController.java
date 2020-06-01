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
	module sk.catheaven.test_prep {
	   requires javafx.controls;
	   requires javafx.fxml;

	   opens sk.catheaven.test_prep to javafx.fxml;
	   exports sk.catheaven.test_prep;
	}
	
 * @author catlord
 */
public class PrimaryController implements Initializable {
	@FXML Button answerButton;
	@FXML Button nextButton;
	@FXML Label questionLabel;
	@FXML VBox answersBox;
	
	private List<Question> questions;
	private int currentQuestionIndex = 0;
	
    @FXML
    private void answer() throws IOException {
		questions.get(currentQuestionIndex).check();
		
		nextButton.setVisible(true);
		answerButton.setVisible(false);
	}

	@FXML
	public void next(){
		nextButton.setVisible(false);
		answerButton.setVisible(true);
		answersBox.getChildren().clear();
		
		questions.get(currentQuestionIndex).reset();		// remove coloring and selections
		currentQuestionIndex++;
		//currentQuestionIndex = (int)(Math.random() * questions.size());
		if(currentQuestionIndex >= questions.size()){
			questionLabel.setText("END OF QUIZ");
			
		}
		Question current = questions.get(currentQuestionIndex);
		questionLabel.setText(current.getQuestion());
		
		for(CheckBox box : current.getBoxes()){
			answersBox.getChildren().add(box);
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
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
				System.out.println("New question: " + line);
				question = line;
				correctAnswers = new ArrayList<>();
				incorrectAnswers = new ArrayList<>();
			}
			else{
				if(line.isEmpty()  ||  line.isBlank() ||  line.equals("")){
					System.out.println("Empty line");
					if( ! question.isEmpty()){
						questions.add(new Question(question, correctAnswers, incorrectAnswers));
						System.out.println("Added " + question + "\n");
						question = "";
					}
					
					continue;
				}
				
				if(line.contains("~")){
					System.out.println("- Incorrect line: " + line);
					String incLine = line.replaceAll("^[ ]*~[ ]*", "");
					incorrectAnswers.add(incLine);
				}
				else if(line.contains("*")){
					System.out.println("+ Correct line: " + line);
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