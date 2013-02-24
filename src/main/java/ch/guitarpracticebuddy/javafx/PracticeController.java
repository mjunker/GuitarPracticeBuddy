package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeController implements Initializable {

    @FXML
    private ProgressIndicator progressBar;
    @FXML
    private ListView currentExercisesTable;
    @FXML
    private Slider bpmSlider;
    @FXML
    private ToggleButton bpmButton;
    @FXML
    private ChoiceBox<Rating> ratingBox;
    @FXML
    private ExerciseDefinition exerciseDefinition;
    @FXML
    private Button skipButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button startButton;
    @FXML
    private VBox practiceContentPanel;

    @FXML
    ToggleButton playClipButton;
    @FXML
    ComboBox<ExerciseAttachment> clipComboBox;
    @FXML
    ProgressBar clipProgressBar;

    private TimerController timerController;
    private ExerciseInstance exerciseInstance;
    private ClipPlayerController clipPlayerController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initRatingBox();
        initTimerController();
        initCurrentExercisesTable();
        initButtons();
        initCurrentExercises();
        clipPlayerController = new ClipPlayerController(playClipButton, clipComboBox, clipProgressBar);
        initBpmButton();
        select(null);

    }

    private void initBpmButton() {
        bpmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                timerController.setMetronomeEnabled(bpmButton.isSelected());
            }
        });
        bpmButton.setId("bpmButton");

    }

    private void initButtons() {
        this.skipButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (exerciseInstance != null) {
                    exerciseInstance.skip();
                }
            }
        });

        this.resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (exerciseInstance != null) {
                    exerciseInstance.reset();
                }
            }
        });
    }

    private void initPracticeContent() {
        practiceContentPanel.getChildren().clear();
        if (exerciseDefinition != null) {
            practiceContentPanel.getChildren().add(new ImagePane(exerciseDefinition));

        }

    }

    private void initTimerController() {
        this.timerController = new TimerController(startButton);
    }

    public void togglePlay() {
        this.timerController.toggleTimer();
    }

    private void initCurrentExercisesTable() {
        currentExercisesTable.setEditable(false);
        currentExercisesTable.setCellFactory(new Callback<ListView<ExerciseDefinition>, ListCell<ExerciseDefinition>>() {

            @Override
            public ListCell<ExerciseDefinition> call(ListView<ExerciseDefinition> exerciseDefinitionListView) {
                return new ExerciseDefinitionListCell();
            }
        });
        currentExercisesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseDefinition>() {
            @Override
            public void changed(ObservableValue<? extends ExerciseDefinition> observableValue, ExerciseDefinition exerciseDefinition, ExerciseDefinition exerciseDefinition2) {
                select(exerciseDefinition2);
            }
        });

        currentExercisesTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.SPACE) {

                    togglePlay();
                }
            }
        });

    }

    private ObservableList<ExerciseDefinition> initCurrentExercises() {
        ObservableList<ExerciseDefinition> exerciseDefinitions = FXCollections.observableArrayList(PracticeBuddyBean.getInstance().getExcercisesForToday());
        currentExercisesTable.setItems(exerciseDefinitions);
        return exerciseDefinitions;
    }

    private void initRatingBox() {
        ratingBox.setItems(FXCollections.observableArrayList(Rating.values()));
        ratingBox.setConverter(new EnumToStringConverter(Rating.class));
    }

    private void select(ExerciseDefinition exerciseDefinition) {

        bind(this.exerciseDefinition, exerciseDefinition);
        this.exerciseDefinition = exerciseDefinition;
        clipPlayerController.setExerciseDefinition(exerciseDefinition);

        initExerciseInstance(exerciseDefinition);
        initPracticeContent();
        updateTimerController();
        setEnabled(this.exerciseDefinition != null);

    }

    private void setEnabled(boolean enabled) {
        boolean disabled = !enabled;
        this.bpmSlider.setDisable(disabled);
        this.ratingBox.setDisable(disabled);
        this.skipButton.setDisable(disabled);
        this.resetButton.setDisable(disabled);
        this.startButton.setDisable(disabled);
        this.progressBar.setDisable(disabled);
        this.bpmButton.setDisable(disabled);
        this.clipProgressBar.setDisable(disabled);
        this.playClipButton.setDisable(disabled);
        this.clipComboBox.setDisable(disabled);

    }

    private void initExerciseInstance(ExerciseDefinition exerciseDefinition) {
        if (this.exerciseDefinition != null) {
            this.exerciseInstance = exerciseDefinition.getTodaysExercises();
        } else {
            this.exerciseInstance = null;
        }
    }

    private void updateTimerController() {
        this.timerController.setExerciseInstance(this.exerciseInstance);
    }

    public void bind(ExerciseDefinition oldValue, ExerciseDefinition newValue) {
        if (oldValue != null) {
            Bindings.unbindBidirectional(ratingBox.valueProperty(), oldValue.ratingProperty());
            Bindings.unbindBidirectional(bpmSlider.maxProperty(), oldValue.bpmProperty());
            Bindings.unbindBidirectional(bpmSlider.valueProperty(), oldValue.getTodaysExercises().bpmProperty());
            Bindings.unbindBidirectional(bpmButton.textProperty(), oldValue.getTodaysExercises().bpmProperty());
            progressBar.progressProperty().unbind();
        }

        if (newValue != null) {
            Bindings.bindBidirectional(ratingBox.valueProperty(), newValue.ratingProperty());
            Bindings.bindBidirectional(bpmSlider.maxProperty(), newValue.bpmProperty());
            Bindings.bindBidirectional(bpmSlider.valueProperty(), newValue.getTodaysExercises().bpmProperty());
            Bindings.bindBidirectional(bpmButton.textProperty(), newValue.getTodaysExercises().bpmProperty(), (StringConverter) new IntegerStringConverter());
            progressBar.progressProperty().bind(newValue.getTodaysExercises().practiceTimeProgressProperty());

        }

    }

    public void refresh() {
        initCurrentExercises();
        currentExercisesTable.getSelectionModel().clearSelection();
    }

    private class ExerciseDefinitionListCell extends ListCell<ExerciseDefinition> {

        @Override
        protected void updateItem(final ExerciseDefinition exerciseDefinition, boolean b) {
            super.updateItem(exerciseDefinition, b);

            if (exerciseDefinition != null) {
                exerciseDefinition.getTodaysExercises().statusProperty().addListener(new ChangeListener<ExerciseStatus>() {
                    @Override
                    public void changed(ObservableValue<? extends ExerciseStatus> observableValue, ExerciseStatus exerciseStatus, ExerciseStatus exerciseStatus2) {
                        setPracticeListStyle(exerciseDefinition);
                    }
                });
                textProperty().bind(exerciseDefinition.titleProperty());
                setPracticeListStyle(exerciseDefinition);
            }

        }

        private void setPracticeListStyle(ExerciseDefinition exerciseDefinition) {
            getStyleClass().add(StyleClass.PRACTICE_LIST.getStyle());
            getStyleClass().remove(StyleClass.DONE.getStyle());
            getStyleClass().remove(StyleClass.SKIP.getStyle());
            getStyleClass().remove(StyleClass.PLANNED.getStyle());

            switch (exerciseDefinition.getTodaysExercises().getStatus()) {

                case DONE:
                    getStyleClass().add(StyleClass.DONE.getStyle());
                    break;
                case SKIPPED:
                    getStyleClass().add(StyleClass.SKIP.getStyle());
                    break;
                case PLANNED:
                    getStyleClass().add(StyleClass.PLANNED.getStyle());
                    break;
            }

        }
    }
}
