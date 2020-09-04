/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.test_prep;

import TestUtils.MultiChoiceQuestion;
import TestUtils.Question;
import TestUtils.SingleChoiceQuestion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

/**
 *
 * @author catlord
 */
public class TestLoader implements Initializable {
	@FXML Button startTestButton;
	@FXML Label testInfoLabel;
	@FXML Label inputFileLabel;
	@FXML VBox errorsBox;
	
	private final FileChooser chooser;
	private Stage stage;
	private final List<Question> questions;
	
	private final String incorrectAnswerMultiChoice = "~";
	private final String correctAnswerMultiChoice = "*";
	
	public TestLoader(){
		questions = new ArrayList<>();
		
		chooser = new FileChooser();
		chooser.setInitialDirectory(new File("/home/catlord/NetBeansProjects/Test_prep/src/main/resources/sk/catheaven/test_prep"));
		chooser.setTitle("Open file...");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// reset all necessary labels
		testInfoLabel.setText("");
		inputFileLabel.setText("");
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	/**
	 * Opens up a chooser menu and allows user to load a test file.
	 * Questions have patterns (as described in the Question.java class). If the question is improperly
	 * formatted, it will be skipped along with its questions.
	 * @throws java.io.FileNotFoundException In case something goes wrong when user selects test file.
	 */
	public void loadTestFile() throws FileNotFoundException {
		File selectedFile = chooser.showOpenDialog(stage);
		
		if(selectedFile == null){
			System.out.println("no file was selected");
			return;
		}
		
		inputFileLabel.setText("".concat(selectedFile.getName()));
		java.util.Scanner scanner = new java.util.Scanner(selectedFile).useDelimiter("\n");
		Pattern questionPattern = Pattern.compile("^[!]?\\d+\\..*");

		Matcher matcher;
		int lineCounter = 0;
		boolean haveQuestion = false;
	
		String questionLabel = "?";
		List<String> errorLog = new ArrayList<>();				// stores information about malformed questions
		List<String> correctAnswers = new ArrayList<>();
		List<String> incorrectAnswers = new ArrayList<>();
		
		while(scanner.hasNext()) {
			String line = scanner.next().replaceAll("\n", "");
			lineCounter++;
			
			matcher = questionPattern.matcher(line);
			
			// we have new question
			if(matcher.lookingAt()){
				System.out.println("--" + line);
				
				haveQuestion = true;
				questionLabel = line;
				correctAnswers = new ArrayList<>();
				incorrectAnswers = new ArrayList<>();
			}
			else{
				
				System.out.println("++" + line);
				// in case of an empty line a question with answers was formed 
				// or a few empty lines were skipped
				if(line.isEmpty()  ||  line.isBlank()){
					
					// if we had question, store it and expect next one
					if(haveQuestion){
						haveQuestion = false;
						
						// there must be answers for a questions (at least one answer)
						if(correctAnswers.isEmpty()  &&  incorrectAnswers.isEmpty()){
							errorLog.add(String.format("Line %d: Question `%s` doesn't have ANY answers", lineCounter, questionLabel));
							continue;
						}
						
						// add a new question to the list OR ignore loaded questions and append to error log
						addNewQuestion(questionLabel, lineCounter, correctAnswers, incorrectAnswers, errorLog);
					}
				}
				else {	// we have a line with answer (or a corrupted line, which will be ignored)
					
					// in case there is an error in test file (missing or broken question format) skip this and all answers
					if( ! haveQuestion){
						errorLog.add(String.format("Line %d: Don't have question loaded, but found this `%s`", lineCounter, line));
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
					else
						errorLog.add(String.format("Line %d: Unknown answer type ! `%s`", lineCounter, line));
				}
			}
		}
		
		if(haveQuestion)
			addNewQuestion(questionLabel, lineCounter, correctAnswers, incorrectAnswers, errorLog);
		
		// after loading questions, decide, what to do with them (error checking)
		if(questions.isEmpty()){
			testInfoLabel.setText("Questions were not properly loaded ! Check input file.");
			startTestButton.setDisable(true);
		}
		else if(! errorLog.isEmpty()){
			testInfoLabel.setText("Found Errors in Test File !");
			startTestButton.setDisable(true);
			displayErrors(errorLog);
		}
		else{
			testInfoLabel.setText("Loaded " + questions.size() + " questions");
			
			questions.forEach((q) -> {
				System.out.println(q.getQuestion());
			});
			
			startTestButton.setDisable(false);
		}
	}
	
	public void startTest() throws IOException {
		inputFileLabel.setText("");
		testInfoLabel.setText("");
		startTestButton.setDisable(true);
		
		Tester tester = new Tester(questions);
		tester.setStage(this.stage);				// pass the stage to the tester
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test.fxml"));
		loader.setController(tester);
		
		if(this.inputFileLabel.getText().isEmpty())
			stage.setTitle("Test");
		else
			stage.setTitle(this.inputFileLabel.getText());
		
		stage.setScene(new Scene(loader.load()));
	}
	
	private void displayErrors(List<String> errors){
		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		
		errors.forEach((error) -> {
			textArea.setText(textArea.getText() + error + "\n");
		});
				
		errorsBox.getChildren().clear();
		errorsBox.getChildren().add(textArea);
	}
	
	private void addNewQuestion(String questionLabel, int lineCounter, List<String> correctAnswers, List<String> incorrectAnswers, List<String> errorLog){
		if(questionLabel.startsWith(Question.singleChoiceQuestionPrefix)){
							
			// single choice question must have ONE answer correct
			if(correctAnswers.isEmpty()){
				errorLog.add(String.format("Line %d: Single choice question `%s` is supposed to have exactly ONE correct answer, but doesn't", lineCounter, questionLabel));
				return;
			}

			questions.add(new SingleChoiceQuestion(questionLabel, correctAnswers.get(0), incorrectAnswers));
		}
		else
			questions.add(new MultiChoiceQuestion(questionLabel, correctAnswers, incorrectAnswers));
	}
}
