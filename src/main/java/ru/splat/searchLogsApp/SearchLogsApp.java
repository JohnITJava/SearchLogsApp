package ru.splat.searchLogsApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchLogsApp extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("SearchLogsApp");
        primaryStage.setScene(new Scene(root, 1000, 400));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);}
}
