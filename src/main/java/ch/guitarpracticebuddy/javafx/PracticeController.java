package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.Rating;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeController implements Initializable {

    public static final ObservableList<ExerciseDefinition> data =
            FXCollections.observableArrayList();
    @FXML
    private ProgressIndicator progressBar;
    @FXML
    private ListView currentExercisesTable;
    @FXML
    private Slider bpmSlider;
    @FXML
    private Label bpmLabel;
    @FXML
    private ChoiceBox ratingBox;
    @FXML
    private ExerciseDefinition exerciseDefinition;
    @FXML
    private Button skipButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button startButton;

    private TimerController timerController;

    private void initProgressBar() {
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initCurrentExercises();
        initRatingBox();
        initButtons();
        initTimerController();
        initBpm();

        addListeners();

        // currentEx.setCellFactory(new PropertyValueFactory<ExerciseDefinition, String>("title"));

    }

    private void addListeners() {
        this.bpmSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setBpm((int) bpmSlider.getValue());
            }
        });
    }

    private void initTimerController() {
        this.timerController = new TimerController(startButton);
    }

    private void initButtons() {

    }

    private void initCurrentExercises() {
        data.addAll(PracticeBuddyBean.getInstance().getExcercisesForToday());
        currentExercisesTable.setItems(data);
        currentExercisesTable.setEditable(false);
        currentExercisesTable.setCellFactory(new Callback<ListView<ExerciseDefinition>, ListCell<ExerciseDefinition>>() {

            @Override
            public ListCell<ExerciseDefinition> call(ListView<ExerciseDefinition> exerciseDefinitionListView) {
                ListCell<ExerciseDefinition> exerciseDefinitionListCell = new ListCell<ExerciseDefinition>() {
                    @Override
                    protected void updateItem(ExerciseDefinition exerciseDefinition, boolean b) {
                        super.updateItem(exerciseDefinition, b);
                        if (exerciseDefinition != null) {
                            setText(exerciseDefinition.getTitle());
                        }
                    }
                };
                return exerciseDefinitionListCell;
            }
        });
        currentExercisesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseDefinition>() {
            @Override
            public void changed(ObservableValue<? extends ExerciseDefinition> observableValue, ExerciseDefinition exerciseDefinition, ExerciseDefinition exerciseDefinition2) {
                select(exerciseDefinition2);
            }
        });
    }

    private void initRatingBox() {
        ratingBox.setItems(FXCollections.observableArrayList(Rating.values()));
    }

    private void select(ExerciseDefinition exerciseDefinition) {
        if (exerciseDefinition == this.exerciseDefinition && this.exerciseDefinition != null)
            return;

        this.exerciseDefinition = exerciseDefinition;
        initRating();
        initBpm();
        this.timerController.setExerciseDefinition(exerciseDefinition);
    }

    private void initRating() {
        if (this.exerciseDefinition != null) {
            ratingBox.getSelectionModel().select(this.exerciseDefinition.getRating());
        }
    }

    private void initBpm() {

        if (this.exerciseDefinition != null) {
            bpmSlider.setMax(this.exerciseDefinition.getTargetBpm());
            bpmSlider.setValue(this.exerciseDefinition.getBpm());
            setBpm(this.exerciseDefinition.getBpm());

        } else {
            bpmSlider.setMax(0);
            bpmSlider.setValue(0);
            bpmLabel.setText("");
        }

    }

    private void setBpm(int bpm) {
        bpmLabel.setText(String.valueOf(bpm) + "bpm");
    }

    private float calculateTime(int practicedTime) {
        return ((float) practicedTime) / exerciseDefinition.getMiliSeconds();
    }

}
