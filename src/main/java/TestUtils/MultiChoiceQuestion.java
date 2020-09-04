/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestUtils;

import TestUtils.Question;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Allows none, single or multiple correct answers.
 * @author catlord
 */
public class MultiChoiceQuestion extends Question {
	private final List<String> correct;
	private final List<String> incorrect;
	
	public MultiChoiceQuestion(String question, List<String> correct, List<String> incorrect){
		super(question);
			
		this.correct = correct;
		this.incorrect = incorrect;
		
		int inIndex = 0;
		int cIndex = 0;
		
		int limit = this.incorrect.size() + correct.size();
		for(int i = 0; i < limit; i++){
			CheckBox box;
			
			// randomly append correct or incorrect answers
			if(Math.random() < 0.5d){
				if(inIndex < this.incorrect.size())
					box = new CheckBox((String) this.incorrect.get(inIndex++));
				else
					box = new CheckBox((String) correct.get(cIndex++));
			} else{
				if(cIndex < correct.size())
					box = new CheckBox((String) correct.get(cIndex++));
				else
					box = new CheckBox((String) this.incorrect.get(inIndex++));
			}
			
			box.setStyle("-fx-font-size: 17;");
			box.setFocusTraversable(false);
			this.answers.add(box);
		}
	}
	
	@Override
	public void appendAnswers(Pane pane){
		for(Object baseBox : this.answers){
			CheckBox box = (CheckBox) baseBox;
			pane.getChildren().add(box);
		}
	}
	
	// TODO: test this
	@Override
	public boolean check(){
		for(Object boxBase : answers){
			CheckBox box = (CheckBox) boxBase;							// need to downcast to check box
			
			String answer = box.getText();

			// if the box is selected, check if it should be selected
			if(box.isSelected()){
				if(isCorrect(answer))
					box.setBackground(new Background(new BackgroundFill(Color.valueOf(correctColor), CornerRadii.EMPTY, Insets.EMPTY)));			// color green
				else{
					box.setBackground(new Background(new BackgroundFill(Color.valueOf(incorrectColor), CornerRadii.EMPTY, Insets.EMPTY)));			// color red
					return false;
				}
			}
			// else check if an unselected box should be selected
			else {
				if(isCorrect(answer)){
					box.setBackground(new Background(new BackgroundFill(Color.valueOf(shouldBeSelectedColor), CornerRadii.EMPTY, Insets.EMPTY)));	// only color orange, ignore if this part is correctly not selected
					return false;
				}
			}
		}
		return true;
	}

	public void reset(){
		for(Object baseBox : this.answers){
			CheckBox box = (CheckBox) baseBox;
			box.setSelected(false);
			box.setBackground(null);
		}
		Collections.shuffle(this.answers);
	}
	
	private boolean isCorrect(String answer){
		for(String correctAnswer : correct)
			if(correctAnswer.equals(answer))
				return true;
		return false;
	}
}
