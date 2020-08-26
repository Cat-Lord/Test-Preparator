package sk.catheaven.test_prep;

import java.io.File;	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.FileChooser;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
	private FileChooser chooser;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"));
		
		stage.setTitle("Test Prep");
        stage.setScene(scene);
        stage.show();
		/*
		// add an option to choose a file that loads questions file
		
		chooser.setTitle("Open file...");
		File selectedFile = null;
		
		while(selectedFile == null){
			selectedFile = chooser.showOpenDialog(stage);
		}
		*/
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}