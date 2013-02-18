package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.ExerciseInstance;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.Rating;
import ch.guitarpracticebuddy.util.FileUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeController implements Initializable {

    public static final int IMAGE_WIDTH = 1100;
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
    @FXML
    private VBox practiceContentPanel;
    private TimerController timerController;
    private ExerciseInstance exerciseInstance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initRatingBox();
        initTimerController();
        initCurrentExercises();

    }

    private void initPracticeContent() {
        practiceContentPanel.getChildren().clear();
        if (exerciseDefinition != null) {

            ScrollPane scrollPane = new ScrollPane();

            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            if (!exerciseDefinition.getAttachments().isEmpty()) {

                final Pagination pagination = PaginationBuilder.create().pageCount(exerciseDefinition.getAttachments().size()).pageFactory(new Callback<Integer, Node>() {
                    @Override
                    public Node call(Integer pageIndex) {

                        VBox box = new VBox();
                        Image image = FileUtil.loadImage(exerciseDefinition, exerciseDefinition.getAttachments().get(pageIndex));
                        ImageView iv = new ImageView(image);
                        iv.setPreserveRatio(true);
                        iv.setFitHeight(IMAGE_WIDTH);
                        box.setAlignment(Pos.CENTER);
                        box.getChildren().add(iv);
                        return box;
                    }
                }).build();
                pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                scrollPane.setContent(pagination);

                practiceContentPanel.getChildren().add(scrollPane);
            }

        }

    }

    private void initTimerController() {
        this.timerController = new TimerController(startButton);
    }

    private void initCurrentExercises() {
        ObservableList<ExerciseDefinition> exerciseDefinitions = FXCollections.observableArrayList(PracticeBuddyBean.getInstance().getExcercisesForToday());
        currentExercisesTable.setItems(exerciseDefinitions);
        currentExercisesTable.setEditable(false);
        currentExercisesTable.setCellFactory(new Callback<ListView<ExerciseDefinition>, ListCell<ExerciseDefinition>>() {

            @Override
            public ListCell<ExerciseDefinition> call(ListView<ExerciseDefinition> exerciseDefinitionListView) {
                ListCell<ExerciseDefinition> exerciseDefinitionListCell = new ListCell<ExerciseDefinition>() {
                    @Override
                    protected void updateItem(ExerciseDefinition exerciseDefinition, boolean b) {
                        super.updateItem(exerciseDefinition, b);
                        if (exerciseDefinition != null) {
                            textProperty().bind(exerciseDefinition.titleProperty());
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

        if (!exerciseDefinitions.isEmpty()) {
            select(exerciseDefinitions.get(0));
        }
    }

    private void initRatingBox() {
        ratingBox.setItems(FXCollections.observableArrayList(Rating.values()));
    }

    private void select(ExerciseDefinition exerciseDefinition) {

        bind(this.exerciseDefinition, exerciseDefinition);

        this.exerciseDefinition = exerciseDefinition;
        if (this.exerciseDefinition != null) {
            this.exerciseInstance = exerciseDefinition.getTodaysExercises();

        } else {
            this.exerciseInstance = null;
        }
        initPracticeContent();
        updateTimerController();

        if (this.exerciseInstance != null) {

        } else {
            this.timerController.resetTimer();
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
            Bindings.unbindBidirectional(bpmLabel.textProperty(), oldValue.getTodaysExercises().bpmProperty());
            progressBar.progressProperty().unbind();
        }

        if (newValue != null) {
            Bindings.bindBidirectional(ratingBox.valueProperty(), newValue.ratingProperty());
            Bindings.bindBidirectional(bpmSlider.maxProperty(), newValue.bpmProperty());
            Bindings.bindBidirectional(bpmSlider.valueProperty(), newValue.getTodaysExercises().bpmProperty());
            Bindings.bindBidirectional(bpmLabel.textProperty(), newValue.getTodaysExercises().bpmProperty(), (StringConverter) new IntegerStringConverter());
            progressBar.progressProperty().bind(newValue.getTodaysExercises().practiceTimeProgressProperty());

        }

    }

    public void refresh() {
        initCurrentExercises();
        select(exerciseDefinition);
    }

}
