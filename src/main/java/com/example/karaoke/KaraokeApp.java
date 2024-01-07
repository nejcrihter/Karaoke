package com.example.karaoke;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class KaraokeApp extends Application {
    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        primaryStage.setTitle("Karaoke");
        primaryStage.setScene(new Scene(root, 500, 575));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}