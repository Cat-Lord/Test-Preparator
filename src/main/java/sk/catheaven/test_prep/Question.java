/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.test_prep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 *
 * @author catlord
 */
public class Question {
	private final String question;
	private final List<String> correct;
	private final List<String> incorrect;
	private List<CheckBox> boxes;
	
	public Question(String question, List<String> correct, List<String> incorrect){
		this.boxes = new ArrayList<>();
		
		this.question = question;
		this.correct = correct;
		this.incorrect = incorrect;
		this.initialize();
	}
	
	public void check(){
		for(CheckBox box : boxes){
			String answer = box.getText();

			// if the box is selected, check if it should be selected
			if(box.isSelected()){
				if(checkIncorrect(answer) == false)
					box.setBackground(new Background(new BackgroundFill(Color.valueOf("#ff4444"), CornerRadii.EMPTY, Insets.EMPTY)));		// color red
				else
					box.setBackground(new Background(new BackgroundFill(Color.valueOf("#4dff4d"), CornerRadii.EMPTY, Insets.EMPTY)));		// color green
			}
			// else check if an unselected box should be selected
			else {
				if(checkCorrect(answer) == false)
					box.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffc266"), CornerRadii.EMPTY, Insets.EMPTY)));		// only color red, ignore if this part is correctly not selected
			}
		}
	}
	
	public String getQuestion(){
		return this.question;
	}
	
	private void initialize(){
		System.out.println("Initializing question " + question);
		int inIndex = 0;
		int cIndex = 0;
		
		int limit = incorrect.size() + correct.size();
		for(int i = 0; i < limit; i++){
			CheckBox box;
			
			if(Math.random() < 0.5d){
				if(inIndex < incorrect.size())
					box = new CheckBox(incorrect.get(inIndex++));
				else
					box = new CheckBox(correct.get(cIndex++));
			} else{
				if(cIndex < correct.size())
					box = new CheckBox(correct.get(cIndex++));
				else
					box = new CheckBox(incorrect.get(inIndex++));
			}
			
			box.setStyle("-fx-font-size: 17;");
			box.setFocusTraversable(false);
			boxes.add(box);
		}
	}
	
	public void reset(){
		for(CheckBox box : boxes){
			box.setSelected(false);
			box.setBackground(null);
		}
		Collections.shuffle(boxes);
	}
	
	public List<CheckBox> getBoxes(){
		return boxes;
	}
	
	private boolean checkIncorrect(String answer){
		for(String incorrectAnswer : incorrect)
			if(answer.equals(incorrectAnswer))
				return false;
		return true;
	}

	private boolean checkCorrect(String answer) {
		for(String correctAnswer : correct)
			if(correctAnswer.equals(answer))
				return false;
		return true;
	}
}
