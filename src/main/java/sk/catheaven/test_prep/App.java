package sk.catheaven.test_prep;

import java.io.File;	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * todo: create app communicator
 */
public class App extends Application {
	//private QuestionLoader loader;
	//private Tester tester;
	private Stage mainWindow;
	private TestLoader testLoader;
    private Scene testLoaderScene, testScene, summaryScene;

    @Override
    public void start(Stage stage) throws IOException {
		mainWindow = stage;
		
		initializeApplication();
		
		stage.setTitle("Test Prep");
        stage.setScene(testLoaderScene);
        stage.show();
    }
	
	private void initializeApplication() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		
		testLoader =  new TestLoader();
		testLoader.setStage(mainWindow);
		
		fxmlLoader.setController(testLoader);
		testLoaderScene = new Scene(fxmlLoader.load());
	}

	private Parent loadFXML(String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
		return loader.load();
	}
	
    public static void main(String[] args) {
        launch();
    }
}