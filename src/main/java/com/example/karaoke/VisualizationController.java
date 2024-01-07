package com.example.karaoke;

import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisualizationController {
    @FXML
    private Canvas canvas;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button backButton;
    @FXML
    private Label songNameLabel;
    @FXML
    private Label subtitleUpLabel;
    @FXML
    private Label subtitleDownLabel;


    private boolean isPlaying = false;
    private CustomAudioPlayer player;
    private MediaPlayer mediaPlayer;
    private Timeline timeline;
    private boolean isUp = false;
    private boolean isFirst = true;
    Subtitle nextSubtitle = null;
    Subtitle currentSubtitle = null;
    List<Subtitle> subtitles = new ArrayList<>();

    public void initialize() {

    }

    public void setSongName(String songName) throws StreamPlayerException {
//        songNameLabel.setText(songName);
        String songPath = "src/main/resources/songs/" + songName;
        loadSubtitles(songPath + ".srt");

        player = new CustomAudioPlayer(canvas);
        player.play(songPath);

        Media media = new Media(new File(songPath + ".mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();

        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.1), e -> updateSubtitle()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        isPlaying = true;
    }

    @FXML
    private void handlePause(ActionEvent event) {
        player.pause();
        mediaPlayer.pause();
        timeline.stop();
    }

    public void handlePlay(ActionEvent actionEvent) {
        player.resume();
        mediaPlayer.play();
        timeline.play();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Load the visualization scene
        try {
            player.stop();
            mediaPlayer.stop();
            timeline.stop();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("songSelection.fxml"));
            Parent songSelectionRoot = loader.load();

            // Get the current stage from the back button
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set the scene to the song selection screen
            stage.setScene(new Scene(songSelectionRoot, 500, 575));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSubtitle() {
        // Get current playback time
        double currentTime = mediaPlayer.getCurrentTime().toMillis();

        if (isFirst) {
            subtitleUpLabel.setText(subtitles.get(0).getText());
            isFirst = false;
        }

        // Check if we need to update to the next subtitle
        if (nextSubtitle == null || currentTime > nextSubtitle.start.toMillis()) {
            currentSubtitle = nextSubtitle;
            nextSubtitle = findNextSubtitle(currentSubtitle);
        }

        // Update the subtitle labels
        if (currentSubtitle != null && currentTime >= currentSubtitle.start.toMillis() && currentTime <= currentSubtitle.end.toMillis()) {
            // Current subtitle is active
            if (isUp) {
                subtitleUpLabel.setText(currentSubtitle.getText());
                subtitleUpLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: #FFFFFF; -fx-background-color: #FF1744; -fx-background-radius: 15;  -fx-padding: 5px; -fx-font-family: 'Arial';");
                subtitleDownLabel.setText(nextSubtitle != null ? nextSubtitle.getText() : "");
                subtitleDownLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: #FFFFFF; -fx-background-color: #444444; -fx-background-radius: 15;  -fx-padding: 5px; -fx-font-family: 'Arial';");
            } else {
                subtitleDownLabel.setText(currentSubtitle.getText());
                subtitleDownLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: #FFFFFF; -fx-background-color: #FF1744; -fx-background-radius: 15;  -fx-padding: 5px; -fx-font-family: 'Arial';");
                subtitleUpLabel.setText(nextSubtitle != null ? nextSubtitle.getText() : "");
                subtitleUpLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: #FFFFFF; -fx-background-color: #444444; -fx-background-radius: 15;  -fx-padding: 5px; -fx-font-family: 'Arial';");
            }
        } else if (currentSubtitle != null && currentTime > currentSubtitle.end.toMillis()) {
            // Current subtitle is inactive
            if (isUp) {
                subtitleUpLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: #FFFFFF; -fx-background-color: #444444; -fx-background-radius: 15;  -fx-padding: 5px; -fx-font-family: 'Arial';");
            } else {
                subtitleDownLabel.setStyle("-fx-font-size: 16pt; -fx-text-fill: #FFFFFF; -fx-background-color: #444444; -fx-background-radius: 15;  -fx-padding: 5px; -fx-font-family: 'Arial';");
            }
        }

        // Check if it's time to switch lines
        if (nextSubtitle != null && currentTime >= nextSubtitle.start.toMillis() - 100) {
            isUp = !isUp;
        }

    }

    private Subtitle findNextSubtitle(Subtitle currentSubtitle) {
        if (currentSubtitle == null) {
            return subtitles.isEmpty() ? null : subtitles.get(0);
        }
        int index = subtitles.indexOf(currentSubtitle);
        return (index >= 0 && index < subtitles.size() - 1) ? subtitles.get(index + 1) : null;
    }

    public void loadSubtitles(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            Subtitle currentSubtitle = null;
            StringBuilder textBuilder = new StringBuilder();
            boolean appendNextLine = false;

            //regex for time stamp
            Pattern timePattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2}),(\\d{3}) --> (\\d{2}):(\\d{2}):(\\d{2}),(\\d{3})");

            for (String line : lines) {
                Matcher matcher = timePattern.matcher(line);
                if (matcher.find()) {
                    if (currentSubtitle != null) {
                        currentSubtitle.text = textBuilder.toString().trim();
                        subtitles.add(currentSubtitle);
                    }

                    Duration start = Duration.ofHours(Integer.parseInt(matcher.group(1)))
                            .plusMinutes(Integer.parseInt(matcher.group(2)))
                            .plusSeconds(Integer.parseInt(matcher.group(3)))
                            .plusMillis(Integer.parseInt(matcher.group(4)));

                    Duration end = Duration.ofHours(Integer.parseInt(matcher.group(5)))
                            .plusMinutes(Integer.parseInt(matcher.group(6)))
                            .plusSeconds(Integer.parseInt(matcher.group(7)))
                            .plusMillis(Integer.parseInt(matcher.group(8)));

                    currentSubtitle = new Subtitle(start, end, "");
                    textBuilder = new StringBuilder();
                    appendNextLine = true; // set flag to true when a timestamp is found
                } else if (appendNextLine && !line.trim().isEmpty()) {
                    textBuilder.append(line.trim()); // append only the first line after timestamp
                    appendNextLine = false; // reset flag
                }
            }

            if (currentSubtitle != null) {
                currentSubtitle.text = textBuilder.toString().trim();
                subtitles.add(currentSubtitle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

