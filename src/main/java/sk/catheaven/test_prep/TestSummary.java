/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.test_prep;

import TestUtils.Question;
import java.io.IOException;
import java.net.URL;
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
 *
 * @author catlord
 */
public class TestSummary implements Initializable {
	private final List<Question> questions;
	private Stage stage;
	
	/**
	 * SUMMARY PANE
	 */
	@FXML Label testResultLabel;				// percentual success of user in the test
	@FXML Label NOcorrectLabel;					// label of number of correctly answered questions
	@FXML Label NOincorrectLabel;				// and a label about incorrect ones
	
	/**
	 * PANE OF CORRECT ANSWERS
	 */
	@FXML VBox correctBox;
	@FXML Label correctQuestionLabel;
	@FXML Button showPreviousCorrectButton;
	@FXML Button showNextCorrectButton;
	@FXML Label correctIndexLabel;
	@FXML Label correctCountLabel;
	
	/**
	 * PANE OF INCORRECT ANSWERS
	 */
	@FXML VBox incorrectBox;
	@FXML Label incorrectQuestionLabel;
	@FXML Button showPreviousIncorrectButton;
	@FXML Button showNextIncorrectButton;
	@FXML Label incorrectIndexLabel;
	@FXML Label incorrectCountLabel;
	
	private final List<Integer> correctly;
	private int correctIndex = 0;
	
	private final List<Integer> incorrectly;
	private int incorrectIndex = 0;
			
	public TestSummary(List<Question> questions, List<Integer> correctly, List<Integer> incorrectly){
		this.questions = questions;
		this.correctly = correctly;
		this.incorrectly = incorrectly;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		testResultLabel.setText(  String.format("%.1f%%", ((double)correctly.size() / (double)(questions.size()))) );		// inform about the result
		
		// Set up the indicators on which page is user currently, when browsing 
		// through correctly/incorrectly answered questions
		if(correctly.size() > 0){
			correctCountLabel.setText(String.format("%d", correctly.size()));
			correctIndexLabel.setText(String.format("%d", correctIndex + 1));
			showNextCorrectButton.setDisable(false);
		}
		
		if(incorrectly.size() > 0){
			incorrectCountLabel.setText(String.format("%d", incorrectly.size()));
			incorrectIndexLabel.setText(String.format("%d", incorrectIndex + 1));
			showNextIncorrectButton.setDisable(false);
		}
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	/**			   **
	 *				*
	 *   CORRECT	*
	 *				*
	 *			   **/				
	public void showNextCorrect(){
		if(correctIndex + 1 >= correctly.size())
			return;
		
		correctIndex++;
		showPreviousCorrectButton.setDisable(false);		// allowed, because we are loading next 'page'
		
		if(correctIndex >= correctly.size())
			showNextCorrectButton.setDisable(true);
		
		updateCorrect();
	}
	
	public void showPreviousCorrect(){
		if(correctIndex - 1 < 0)
			return;
		
		correctIndex--;
		showNextCorrectButton.setDisable(false);
		
		if(correctIndex <= 0)
			showPreviousCorrectButton.setDisable(true);
			
		updateCorrect();
	}
	
	private void updateCorrect(){
		correctBox.getChildren().clear();
		Question current = questions.get(correctIndex);
		correctQuestionLabel.setText(current.getQuestion());
		current.appendAnswers(correctBox);
	}
	
	
	
	/**			   **
	 *				*
	 * INCORRECT	*
	 *				*
	 *			   **/				
	public void showNextIncorrect(){
		if(incorrectIndex + 1 >= incorrectly.size())
			return;
		
		incorrectIndex++;
		showPreviousIncorrectButton.setDisable(false);		// allowed, because we are loading next 'page'
		
		if(incorrectIndex >= incorrectly.size())
			showNextIncorrectButton.setDisable(true);
		
		updateIncorrect();
	}
	
	public void showPreviousIncorrect(){
		if(incorrectIndex - 1 < 0)
			return;
		
		incorrectIndex--;
		showNextIncorrectButton.setDisable(false);
		
		if(incorrectIndex <= 0)
			showPreviousIncorrectButton.setDisable(true);
			
		updateIncorrect();
	}
	
	private void updateIncorrect(){
		incorrectBox.getChildren().clear();
		Question current = questions.get(incorrectIndex);
		incorrectQuestionLabel.setText(current.getQuestion());
		current.appendAnswers(incorrectBox);
	}
	
	
	//-------------------------------------------------------------------------------------
	
	
	public void restartTest() throws IOException {
		// reset questions and answers
		for(Question q : questions){
			q.reset();
			q.enableSelection();
		}
		
		Tester tester = new Tester(questions);
		tester.setStage(this.stage);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test.fxml"));
		loader.setController(tester);
		
		stage.setScene(new Scene(loader.load()));
	}
	
	public void goToMenu() throws IOException{
		TestLoader testLoader = new TestLoader();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		loader.setController(testLoader);
		
		stage.setTitle("New Test");
		stage.setScene(new Scene(loader.load()));
	}
}
