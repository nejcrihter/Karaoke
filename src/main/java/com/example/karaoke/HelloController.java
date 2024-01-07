package com.example.karaoke;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private Button startButton;

    @FXML
    protected void onStartButtonClick() {
        // Logic to switch to the main Karaoke screen
        showSongSelection();
    }

    private void showSongSelection() {
        try {
            // Load the FXML file for the song selection screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("songSelection.fxml"));
            Parent songSelectionRoot = loader.load();

            // Get the current stage from the start button (assuming startButton is a member of WelcomeController)
            Stage stage = (Stage) startButton.getScene().getWindow();

            // Set the scene to the song selection screen
            stage.setScene(new Scene(songSelectionRoot, 500, 575)); // Adjust the size as needed
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }
}