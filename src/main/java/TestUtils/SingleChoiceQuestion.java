/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestUtils;

import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Question which has only one correct answer.
 * @author catlord
 */
public class SingleChoiceQuestion extends Question {
	private ToggleGroup toggleGroup;
	private final String correct;
	private final List<String> incorrect;
	
	public SingleChoiceQuestion(String question, String correct, List<String> incorrect) {
		super(question);
		
		this.correct = correct;
		this.incorrect = incorrect;
		Collections.shuffle(this.incorrect);		// immediately shuffle incorrect answers, so we get randomized order of answers
		
		int inIndex = 0;
		boolean correctUsed = false;
		
		int limit = incorrect.size() + 1;			// '+1' because there is (only) one correct answer
		for(int i = 0; i < limit; i++){
			RadioButton btn;
			
			// randomly create list of answers
			if(Math.random() < 0.5d  ||  correctUsed){
				btn = new RadioButton(this.incorrect.get(inIndex++));
			}
			else {
				correctUsed = true;
				btn = new RadioButton(correct);
			}
			
			btn.setStyle("-fx-font-size: 17;");
			btn.setFocusTraversable(false);
			btn.setToggleGroup(toggleGroup);
			this.answers.add(btn);					// just to be able to manipulate with the buttons
		}
		
		// final check
		if( ! correctUsed){
			RadioButton btn = new RadioButton(correct);
			
			btn.setStyle("-fx-font-size: 17;");
			btn.setFocusTraversable(false);
			btn.setToggleGroup(toggleGroup);
		}
	}
	
	@Override
	public void appendAnswers(Pane pane){
		for(Object baseButton : this.answers){
			RadioButton button = (RadioButton) baseButton;
			pane.getChildren().add(button);
		}
	}
	
	/**
	 * Selected answer must not be null (no answer chosen) and must equal to the 
	 * correct answer. Since this is single choice question, we only check against
	 * one correct answer.
	 * @return 
	 */
	@Override
	public boolean check(){
		RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
		
		// Check the best case scenario first (correct answer was selected)
		if(selected != null  &&  selected.getText().equals(this.correct)){
			selected.setBackground(new Background(new BackgroundFill(Color.valueOf(correctColor), CornerRadii.EMPTY, Insets.EMPTY)));
			return true;
		}
		
		// At this point we know that (if selected), the answer is NOT correct, so we color it red
		if(selected != null)
			selected.setBackground(new Background(new BackgroundFill(Color.valueOf(incorrectColor), CornerRadii.EMPTY, Insets.EMPTY)));
		
		// and finally, we search for the right answer and display, that is should've been selected
		for(Object baseButton : this.answers){
			RadioButton button = (RadioButton) baseButton;
			
			if(button.getText().equals(this.correct))
				button.setBackground(new Background(new BackgroundFill(Color.valueOf(shouldBeSelectedColor), CornerRadii.EMPTY, Insets.EMPTY)));
		}
		
		return false;
	}

	@Override
	public void reset(){
		for(Object baseButton : this.answers){
			RadioButton button = (RadioButton) baseButton;
			button.setSelected(false);
			button.setBackground(null);
		}
		Collections.shuffle(this.answers);
	}
}
