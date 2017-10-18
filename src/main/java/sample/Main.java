package sample;

import interfaces.Default;
import interfaces.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(Path.FXML_PATH+"sample.fxml"));
        primaryStage.setTitle("LOL Enemy Checker by dzN");
        primaryStage.setScene(new Scene(root, Default.STATS_PAGE_X, Default.STATS_PAGE_Y));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
