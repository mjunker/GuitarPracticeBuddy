package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.*;
import ch.guitarpracticebuddy.util.FileUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    private TimerController timerController;
    private ExerciseInstance exerciseInstance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initRatingBox();
        initTimerController();
        initCurrentExercisesTable();
        initButtons();
        initCurrentExercises();
        select(null);

        this.bpmSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                timerController.restartBpmTime();
            }
        });

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
        this.bpmLabel.setDisable(disabled);

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
