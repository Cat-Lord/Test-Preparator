/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestUtils;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;

/**
 * Base class for a question.
 * @author catlord
 */
public abstract class Question {
	public static final String singleChoiceQuestionPrefix = "!";
	
	protected String question;			// the question itself
	protected List<Object> answers;
	
	// color codes of correct/incorrect answers
	protected final String correctColor = "#4dff4d";
	protected final String incorrectColor = "#ff4444";
	protected final String shouldBeSelectedColor = "#ffc266";
	
	public abstract void appendAnswers(Pane pane);
	public abstract boolean check();
	public abstract void reset();
	
	public Question(String question){
		this.question  = question;
		this.answers = new ArrayList<>();
	}
	
	public String getQuestion(){
		return this.question;
	}
	
	public List<Object> getBoxes(){
		return this.answers;
	}
}
