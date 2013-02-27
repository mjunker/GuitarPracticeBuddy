package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.*;
import com.google.common.base.Joiner;
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
import org.joda.time.LocalDate;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeController implements Initializable {

    ObservableList<ExerciseDefinition> exerciseDefinitions = FXCollections.observableArrayList();
    @FXML
    private ProgressIndicator progressBar;
    @FXML
    private ListView<ExerciseDefinition> currentExercisesTable;
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
    private Label practiceWeekInfoLabel;
    @FXML
    private ToggleButton playClipButton;
    @FXML
    private ComboBox<ExerciseAttachment> clipComboBox;
    @FXML
    private ProgressBar clipProgressBar;
    @FXML
    private Button previousDayButton;
    @FXML
    private Button nextDayButton;
    private TimerController timerController;
    private ExerciseInstance exerciseInstance;
    private ClipPlayerController clipPlayerController;
    private LocalDate today = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initRatingBox();
        initTimerController();
        initCurrentExercisesTable();
        initButtons();

        clipPlayerController = new ClipPlayerController(playClipButton, clipComboBox, clipProgressBar);
        initBpmButton();
        initDayButtons();
        currentExercisesTable.setItems(exerciseDefinitions);

        initExercisesForDayAndDeselect();

    }

    private void initDayButtons() {

        this.nextDayButton.setId("dayButtonRight");
        this.previousDayButton.setId("dayButtonLeft");

        this.nextDayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                updateDay(today.plusDays(1));

            }
        });

        this.previousDayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                updateDay(today.minusDays(1));
            }
        });
    }

    private void updateDay(LocalDate newDay) {
        this.today = newDay;
        initExercisesForDayAndDeselect();
    }

    private void initExercisesForDayAndDeselect() {
        initDayLabel();
        initCurrentExercises();
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

    private void initCurrentExercises() {
        exerciseDefinitions.clear();
        exerciseDefinitions.addAll(PracticeBuddyBean.getInstance().getExcercisesForDay(today));

    }

    private void initDayLabel() {
        PracticeWeek practicePlanForDay = PracticeBuddyBean.getInstance().getPracticePlanForDay(today);
        String practiceTimeAsString = null;
        if (practicePlanForDay != null) {
            practiceTimeAsString = practicePlanForDay.getPracticeTimeAsString();
        }
        String text = Joiner.on(" ").skipNulls().join(ExerciseDefinitionTreeCell.DATE_TIME_FORMATTER.print(today), practiceTimeAsString);
        practiceWeekInfoLabel.setText(text);
    }

    private void initRatingBox() {
        ratingBox.setItems(FXCollections.observableArrayList(Rating.values()));
        ratingBox.setConverter(new EnumToStringConverter(Rating.class));
    }

    private void select(ExerciseDefinition exerciseDefinition) {

        bind(this.exerciseDefinition, exerciseDefinition);
        currentExercisesTable.getSelectionModel().clearSelection();
        this.exerciseDefinition = exerciseDefinition;

        initExerciseInstance(exerciseDefinition);
        initPracticeContent();
        updateTimerController();
        setEnabled(this.exerciseDefinition != null);
        clipPlayerController.setExerciseDefinition(exerciseDefinition);

    }

    private void setEnabled(boolean enabled) {
        boolean disabled = !enabled;
        this.bpmSlider.setDisable(disabled);
        this.ratingBox.setDisable(disabled);
        this.skipButton.setDisable(disabled);
        this.resetButton.setDisable(disabled);
        this.startButton.setDisable(disabled);

    }

    private void initExerciseInstance(ExerciseDefinition exerciseDefinition) {
        if (this.exerciseDefinition != null) {
            this.exerciseInstance = exerciseDefinition.getExerciseForDay(today);
        } else {
            this.exerciseInstance = null;
        }
    }

    private void updateTimerController() {
        this.timerController.setExerciseInstance(this.exerciseInstance);
    }

    public void bind(ExerciseDefinition oldValue, ExerciseDefinition newValue) {
        unbindOldValue(oldValue);
        initCurrentExerciseInstance(newValue);
        bindNewValue(newValue);

    }

    private void initCurrentExerciseInstance(ExerciseDefinition newValue) {
        if (newValue != null) {
            this.exerciseInstance = newValue.getExerciseForDay(today);
        } else {
            this.exerciseInstance = null;
        }
    }

    private void unbindOldValue(ExerciseDefinition oldValue) {
        if (oldValue != null) {
            Bindings.unbindBidirectional(ratingBox.valueProperty(), oldValue.ratingProperty());
            Bindings.unbindBidirectional(bpmSlider.maxProperty(), oldValue.bpmProperty());
            Bindings.unbindBidirectional(bpmSlider.valueProperty(), this.exerciseInstance.bpmProperty());
            Bindings.unbindBidirectional(bpmButton.textProperty(), this.exerciseInstance.bpmProperty());
            progressBar.progressProperty().unbind();
        }
    }

    private void bindNewValue(ExerciseDefinition newValue) {
        if (newValue != null) {
            Bindings.bindBidirectional(ratingBox.valueProperty(), newValue.ratingProperty());
            Bindings.bindBidirectional(bpmSlider.maxProperty(), newValue.bpmProperty());
            Bindings.bindBidirectional(bpmSlider.valueProperty(), this.exerciseInstance.bpmProperty());
            Bindings.bindBidirectional(bpmButton.textProperty(), this.exerciseInstance.bpmProperty(), (StringConverter) new IntegerStringConverter());
            progressBar.progressProperty().bind(this.exerciseInstance.practiceTimeProgressProperty());

        }
    }

    public void refresh() {
        initExercisesForDayAndDeselect();

    }

    private class ExerciseDefinitionListCell extends ListCell<ExerciseDefinition> {

        @Override
        protected void updateItem(final ExerciseDefinition exerciseDefinition, boolean b) {
            super.updateItem(exerciseDefinition, b);

            if (exerciseDefinition != null) {
                exerciseDefinition.getExerciseForDay(today).statusProperty().addListener(new ChangeListener<ExerciseStatus>() {
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

            switch (exerciseDefinition.getExerciseForDay(today).getStatus()) {

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
