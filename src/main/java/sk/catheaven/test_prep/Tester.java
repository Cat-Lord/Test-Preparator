package sk.catheaven.test_prep;

import TestUtils.SingleChoiceQuestion;
import TestUtils.MultiChoiceQuestion;
import TestUtils.Question;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main app controller, that manipulates question management.
 * Questions are formatted as follows:
 * [!]N. followed by any number of any characters (identifiable with regex '.')
 * where N is a number (without spaces) followed by a dot. 
 * 
 * There are two types of questions: questions with single and multi choice answers. 
 * Every question is considered as multi choice. Single choice question <b>must</b>
 * begin with an exclamation mark as shown in the question pattern above.
 * @author catlord
 */
public class Tester implements Initializable {
	private Stage stage;
	private int correctlyAnswered;							// keeps track of correctly answered questions

	@FXML Button answerButton;
	@FXML Button nextButton;
	@FXML Button goToMenuButton;
	
	@FXML Label questionLabel;
	@FXML VBox answersBox;
	
	private final List<Question> questions;
	private int currentQuestionIndex = 0;
	
	public Tester(List<Question> questions){
		this.questions = questions;
		correctlyAnswered = 0;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		answerButton.setVisible(false);
		goToMenuButton.setVisible(false);
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	/**
	 * When user checks for correctness of answer, this method is fired (even though 
	 * netbeans suggests that this method is not used).
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
			finalizeTest();
			return;
		}
		
		Question current = questions.get(currentQuestionIndex);
		questionLabel.setText(current.getQuestion());
		current.appendAnswers(answersBox);
	}
	
	// test summary
	private void finalizeTest(){
		questionLabel.setText("END OF QUIZ");
		answersBox.getChildren().clear();
		answersBox.getChildren().add(new Label(String.format("Correctly answered %.1f\n", ((double)correctlyAnswered / (double)questions.size()))));
		goToMenuButton.setVisible(true);
		answerButton.setVisible(false);
		nextButton.setVisible(false);
	}
	
	public void goToMenu() throws IOException{
		TestLoader testLoader = new TestLoader();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		loader.setController(testLoader);
		
		stage.setTitle("New Test");
		stage.setScene(loader.load());
	}
}