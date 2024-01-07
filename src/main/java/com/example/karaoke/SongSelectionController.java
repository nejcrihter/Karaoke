package com.example.karaoke;

import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;

public class SongSelectionController {
    @FXML
    protected void onSongSelected(ActionEvent event) {
        Button songButton = (Button) event.getSource();
        String songChoice = songButton.getText();
        System.out.println("Song selected: " + songChoice);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("visualization.fxml"));
            Scene visualizationScene = new Scene(loader.load());

            // Get the visualization controller and set the song name
            VisualizationController visualizationController = loader.getController();


            Stage stage = (Stage) songButton.getScene().getWindow();
            stage.setScene(visualizationScene);
            stage.show();
            visualizationController.setSongName(songChoice);

        } catch (IOException | StreamPlayerException e) {
            e.printStackTrace();
        }
    }
}
