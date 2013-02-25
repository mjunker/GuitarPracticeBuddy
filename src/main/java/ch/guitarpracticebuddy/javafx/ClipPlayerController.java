package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseAttachment;
import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.util.FileUtil;
import ch.lambdaj.Lambda;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.Matchers.equalTo;

public class ClipPlayerController {
    final ToggleButton playClipButton;
    final ComboBox<ExerciseAttachment> clipComboBox;
    final ProgressBar clipProgressBar;
    private MediaPlayer mediaPlayer;
    private ExerciseDefinition exerciseDefinition;
    private BooleanProperty playingProperty = new SimpleBooleanProperty() {

        @Override
        public void set(boolean shouldPlay) {
            super.set(shouldPlay);
            if (mediaPlayer != null) {
                if (shouldPlay) {
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }

            }
        }
    };

    public ClipPlayerController(ToggleButton playClipButton, ComboBox<ExerciseAttachment> clipComboBox, ProgressBar clipProgressBar) {
        this.playClipButton = playClipButton;
        this.clipComboBox = clipComboBox;
        this.clipProgressBar = clipProgressBar;
        init();
    }

    public void setExerciseDefinition(ExerciseDefinition exerciseDefinition) {
        this.exerciseDefinition = exerciseDefinition;
        initContent();
        setEnabled(!clipComboBox.getItems().isEmpty());

    }

    void init() {
        initPlayButton();
        initConverter();
        initComboBoxSelectionListener();
        initClickableProgressBar();
        setEnabled(false);

    }

    private void initPlayButton() {
        playClipButton.selectedProperty().bindBidirectional(playingProperty);
        playClipButton.setId("playButton");

    }

    private void initConverter() {
        clipComboBox.setConverter(new StringConverter<ExerciseAttachment>() {
            @Override
            public String toString(ExerciseAttachment exerciseAttachment) {
                return exerciseAttachment.getFilePath();
            }

            @Override
            public ExerciseAttachment fromString(String s) {
                return Lambda.selectFirst(exerciseDefinition.getAttachments(), Lambda.having(Lambda.on(ExerciseAttachment.class).getFilePath(), equalTo(s)));
            }
        });
    }

    private void initComboBoxSelectionListener() {
        clipComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseAttachment>() {
            @Override
            public void changed(ObservableValue<? extends ExerciseAttachment> observableValue, ExerciseAttachment exerciseAttachment, ExerciseAttachment exerciseAttachment2) {
                setSelectedExerciseAttachment(exerciseAttachment2);
            }
        });
    }

    private void setSelectedExerciseAttachment(ExerciseAttachment exerciseAttachment) {
        destroyCurrentMediaPlayer();

        if (exerciseAttachment != null) {
            initNewMediaPlayer(exerciseAttachment);

        }
    }

    private void initNewMediaPlayer(ExerciseAttachment exerciseAttachment) {
        mediaPlayer = new MediaPlayer(createMedia(exerciseAttachment));
        initProgressBarBinding();
    }

    private void initProgressBarBinding() {
        clipProgressBar.progressProperty().bind(Bindings.createDoubleBinding(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                if (!mediaPlayer.getCurrentTime().isUnknown() && !mediaPlayer.getTotalDuration().isUnknown()) {

                    return mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis();
                }
                return Double.valueOf(0);
            }
        }, mediaPlayer.currentTimeProperty()));

    }

    private void destroyCurrentMediaPlayer() {

        if (mediaPlayer != null) {
            playingProperty.set(false);
            clipProgressBar.progressProperty().unbind();
            clipProgressBar.setProgress(0);
            mediaPlayer = null;
        }

    }

    private void initClickableProgressBar() {
        clipProgressBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mediaPlayer != null) {
                    playClip();
                    mediaPlayer.seek(new Duration(mediaPlayer.getTotalDuration().toMillis() * ((float) mouseEvent.getX()) / clipProgressBar.getWidth()));
                }
            }
        });
    }

    private void playClip() {
        playingProperty.set(true);
    }

    private Media createMedia(ExerciseAttachment exerciseAttachment) {
        return new Media(FileUtil.toFiles(exerciseDefinition, exerciseAttachment).toURI().toString());
    }

    private void initContent() {

        ObservableList<ExerciseAttachment> exerciseAttachments = FXCollections.observableArrayList();
        if (exerciseDefinition != null) {
            exerciseAttachments.addAll(selectAudioFiles(exerciseDefinition.getAttachments()));

        }
        clipComboBox.setItems(exerciseAttachments);
        clipComboBox.getSelectionModel().selectFirst();

    }

    private List<ExerciseAttachment> selectAudioFiles(List<ExerciseAttachment> attachments) {
        List<ExerciseAttachment> mediaFiles = new ArrayList<>();
        for (ExerciseAttachment attachment : attachments) {
            try {
                createMedia(attachment);
                mediaFiles.add(attachment);
            } catch (MediaException e) {
                // NOOP
            }
        }
        return mediaFiles;
    }

    public void setEnabled(boolean enabled) {
        boolean disabled = !enabled;
        this.clipProgressBar.setDisable(disabled);
        this.playClipButton.setDisable(disabled);
        this.clipComboBox.setDisable(disabled);
    }
}