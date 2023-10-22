package com.example.jmediaplayer;

import static  com.example.jmediaplayer.constants.Constants.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayer implements Initializable {

    @FXML
    private BorderPane background;
    @FXML
    private Pane mediaPane, imagePane;
    @FXML
    private Label mediaTitle, appTitle;
    @FXML
    private Button mediaActionButton, mediaNextbutton, mediaPreviousButton, mediaResetButton, openFileButton;
    @FXML
    private Slider progressSlider;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeBar;
    @FXML
    private VBox progressParent, titleParent;
    @FXML
    private HBox controlBar, mediaSelect;
    @FXML
    private MediaView mediaView;
    @FXML
    private ImageView imageView;

    private Media media;
    private javafx.scene.media.MediaPlayer mediaPlayer;
    private boolean isPlaying = false, isSeeking = false;
    private File file;
    private final String[] speeds = {"0.25x", "0.5x", "1x", "1.25x", "1.5x", "1.75x", "2x"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLayout();

//        mediaActionButton.setGraphic(new FontIcon());
        configureProgressSlider();

        for (String speed : speeds) {
            speedBox.getItems().add(speed);
        }
        speedBox.setOnAction(this::changeSpeed);

        mediaView.setFocusTraversable(true);
        volumeBar.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.setVolume(volumeBar.getValue() * 0.01));
    }

    private void configureProgressSlider() {
        progressSlider.setMin(0);
        progressSlider.setMax(1);
        progressSlider.setValueChanging(true);
        progressSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> isSeeking = true);
        progressSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> isSeeking = false);

        progressSlider.valueProperty().addListener((observableValue, number, newValue) -> {
            if (isSeeking) {
                double newTime = newValue.doubleValue() * media.getDuration().toMillis();
                mediaPlayer.seek(Duration.millis(newTime));
            }
        });
    }

    void setLayout() {
        progressSlider.prefWidthProperty().bind(progressParent.widthProperty());

        controlBar.prefWidthProperty().bind(progressParent.widthProperty());

        appTitle.prefWidthProperty().bind(titleParent.widthProperty());
        mediaSelect.prefWidthProperty().bind(titleParent.widthProperty());

        mediaPane.prefWidthProperty().bind(background.widthProperty());
        mediaPane.prefHeightProperty().bind(background.heightProperty());
        imagePane.prefWidthProperty().bind(background.widthProperty());
        background.setStyle("""
                -fx-border-color: orange;
                -fx-border-width: 5px;
                """);
        mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
        mediaView.fitHeightProperty().bind(mediaPane.heightProperty());
    }

    @FXML
    void changeSpeed(ActionEvent event) {
        if (speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Double.parseDouble(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)));
        }
    }

    @FXML
    void mediaAction(ActionEvent event) {
        if (isPlaying) {
            mediaPlayer.pause();
            mediaActionButton.setText("Play");
        } else {
            changeSpeed(null);
            mediaPlayer.setVolume(volumeBar.getValue() * 0.01);
            mediaPlayer.play();
            mediaActionButton.setText("Pause");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    void mediaNext(ActionEvent event) {

    }

    @FXML
    void mediaPrevious(ActionEvent event) {

    }

    @FXML
    void mediaReset(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @FXML
    void selectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        ExtensionFilter extensionFilter1 = new ExtensionFilter("MP4 files (*.mp4)", "*.mp4");
        ExtensionFilter extensionFilter2 = new ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extensionFilter1);
        fileChooser.getExtensionFilters().add(extensionFilter2);
        fileChooser.setSelectedExtensionFilter(extensionFilter1);
        file = fileChooser.showOpenDialog(null);

        setupMedia(file);
    }

    void setupMedia(File file) {
        if (file != null) {
            mediaReset(null);
            String extension = getFileExtension(file);
            mediaTitle.setText(file.getName());
            media = new Media(file.toURI().toString());
            mediaPlayer = new javafx.scene.media.MediaPlayer(media);
            mediaPlayer.currentTimeProperty().addListener((observableValue, duration, newTime) -> {
                double value = newTime.toMillis() / media.getDuration().toMillis();
                progressSlider.setValue(value);
            });

            if ("mp3".equalsIgnoreCase(extension)) {
                mediaView.setVisible(false);
                imageView.setVisible(true);
                Image bg_for_mp3 = new Image(Objects.requireNonNull(getClass().getResource(IMAGE_PATH)).toExternalForm());
                imageView.setImage(bg_for_mp3);
            } else if ("mp4".equalsIgnoreCase(extension)) {
                imageView.setVisible(false);
                mediaView.setVisible(true);
                mediaView.setMediaPlayer(mediaPlayer);
            }
            if (isPlaying) {
                mediaAction(null);
            }
            progressSlider.setValue(0);
        }
    }

    String getFileExtension(File file) {
        int index = file.getName().lastIndexOf('.');
        if (index > 0) {
            return file.getName().substring(index+1);
        }
        return "";
    }
}