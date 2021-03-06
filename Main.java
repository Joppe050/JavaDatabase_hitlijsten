package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //window maken
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hitdossier");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        //get all artistNames
        DB.open();
        Controller.doQuery("SELECT * FROM artiest", 4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
