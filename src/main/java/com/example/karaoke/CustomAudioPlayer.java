package com.example.karaoke;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Map;

public class CustomAudioPlayer {

    private StreamPlayer player;
    private Canvas canvas;

    private final int colorSize = 360;
    private int colorIndex = 0;
    private float bandWidth;
    private int x = 0;
    private int y = 0;
    private int xOld = 0;
    int frameRate = 44100;

    public CustomAudioPlayer(Canvas canvas) {
        this.canvas = canvas;
    }

    public void play(String path) throws StreamPlayerException {
        player = new StreamPlayer();

        player.addStreamPlayerListener(new StreamPlayerListener() {
            @Override
            public void opened(Object dataSource, Map<String, Object> properties) {
            }

            @Override
            public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
                int sampleSize = 2; // 2 bytes for 16-bit audio
                float[] leftChannel = new float[pcmData.length / 2 / sampleSize];
                float[] rightChannel = new float[pcmData.length / 2 / sampleSize];

                for (int i = 0, sampleIndex = 0; i < pcmData.length; i += 4, sampleIndex++) {
                    // Extract left channel (first two bytes of every four)
                    int leftSample = (pcmData[i + 1] << 8) | (pcmData[i] & 0xFF);
                    leftChannel[sampleIndex] = leftSample / 32768.0f; // Normalize to range -1.0 to 1.0

                    // Extract right channel (second two bytes of every four)
                    int rightSample = (pcmData[i + 3] << 8) | (pcmData[i + 2] & 0xFF);
                    rightChannel[sampleIndex] = rightSample / 32768.0f; // Normalize to range -1.0 to 1.0
                }


                // Update oscilloscope visualization
                Platform.runLater(() -> updateOscilloscopeVisualization(leftChannel, rightChannel));
            }

            @Override
            public void statusUpdated(StreamPlayerEvent event) {

            }



            private void updateOscilloscopeVisualization(float[] leftChannel, float[] rightChannel) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                colorIndex = (colorIndex == colorSize - 1) ? 0 : colorIndex + 1;
                gc.setStroke(Color.hsb(colorIndex, 1.0f, 1.0f));
                gc.setLineWidth(2);


                int halfHeight = (int) (canvas.getHeight() / 2);
                int quarterHeight = (int) (canvas.getHeight() / 4);
                int newSampleCount = (int) (frameRate * 0.023);
                bandWidth = (float) canvas.getWidth() / (float) newSampleCount;
                xOld = 0;

                for (int i = 0; i < newSampleCount; i++) {
                    x = (int) (i * bandWidth);
                    y = halfHeight
                            + (int) (quarterHeight * (leftChannel[i] + rightChannel[i]));

                    x = (int) Math.min(Math.max(0, x), canvas.getWidth());
                    y = (int) Math.min(Math.max(0, y), canvas.getHeight());

                    gc.strokeLine(xOld, halfHeight, x, y);
                    xOld = x;
                }
            }

            public void endOfMedia() {
            }
        });

        // Load and play the audio file
        player.open(new File(path + " - Normal.mp3"));
        player.play();
        player.setMute(true);
        System.out.println(player.getMute());
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.resume();
        player.setMute(true);
    }

    public void stop() {
        player.stop();
    }
}

