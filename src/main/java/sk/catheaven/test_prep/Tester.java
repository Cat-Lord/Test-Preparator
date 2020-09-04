package sk.catheaven.test_prep;

import TestUtils.Question;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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

	@FXML Button answerButton;
	@FXML Button nextButton;
	
	@FXML Label questionLabel;
	@FXML VBox answersBox;
	
	private final List<Question> questions;
	private int currentQuestionIndex = 0;
	
	// in the summary window keeps track of indices when user browses through 
	// correctly or incorrectly answered questions
	private int currentCorrect = 0;
	private int currentIncorrect = 0;

	// these lists hold indices of correctly/incorrectly answered 
	// questions to allow their later inspection by the user
	private final List<Integer> correctlyAnswered;
	private final List<Integer> incorrectlyAnswered;
	
	public Tester(List<Question> questions){
		this.questions = questions;
		Collections.shuffle(this.questions);
		currentQuestionIndex = 0;
		
		correctlyAnswered = new ArrayList<>();
		incorrectlyAnswered = new ArrayList<>();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		answerButton.setVisible(false);
		next();
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	/**
	 * When user checks for correctness of answer, this method is fired (even though 
	 * netbeans suggests that this method is not used). Question is then set to non-
	 * editable mode to hinder the user of changing their answers.
	 * According to the result, index of current questions is added into its proper
	 * list (list of correctly/incorrectly answered questions).
	 * @throws IOException 
	 */
    @FXML
    private void answer() throws IOException {
		if(questions.get(currentQuestionIndex).check())
			correctlyAnswered.add(currentQuestionIndex);
		else
			incorrectlyAnswered.add(currentQuestionIndex);
		
		questions.get(currentQuestionIndex).disableSelection();
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
		
		if(currentQuestionIndex >= questions.size()){
			try{
				finalizeTest();
				return;
			}
			catch(Exception e){
				e.printStackTrace();
				exit(1);
			}
		}
		
		Question current = questions.get(currentQuestionIndex++);
		questionLabel.setText(current.getQuestion());
		current.appendAnswers(answersBox);
	}
	
	// test summary
	private void finalizeTest() throws IOException {
		TestSummary testSummary = new TestSummary(questions, correctlyAnswered, incorrectlyAnswered);
		testSummary.setStage(this.stage);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/testSummary.fxml"));
		loader.setController(testSummary);
		
		stage.setScene(new Scene(loader.load()));
	}
}