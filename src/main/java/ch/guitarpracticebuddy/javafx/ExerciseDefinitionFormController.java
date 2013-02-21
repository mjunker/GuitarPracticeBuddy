package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.Rating;
import ch.guitarpracticebuddy.domain.Tag;
import ch.guitarpracticebuddy.util.FileUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExerciseDefinitionFormController implements Initializable {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField minutesField;
    @FXML
    private TextField bpmField;
    @FXML
    private TextArea filesField;
    @FXML
    private ChoiceBox<Rating> ratingField;
    @FXML
    private Button selectFilesButton;
    @FXML
    private Button previewButton;
    @FXML
    private FlowPane tagPanel;
    @FXML
    private GridPane exerciseDefinitionPane;

    private ExerciseDefinition exerciseDefinition;
    private ObservableList<Tag> selectedTags = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ratingField.setItems(FXCollections.observableArrayList(Rating.values()));
        ratingField.setConverter(new EnumToStringConverter(Rating.class));

        initDropListener();

        selectFilesButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();

                List<File> files = fileChooser.showOpenMultipleDialog(null);
                if (files != null) {
                    List<String> relativePaths = FileUtil.copyFilesToApplicationHome(files, exerciseDefinition);
                    exerciseDefinition.setAttachmentsAsString(ExerciseDefinition.createAttachmentString(relativePaths));
                }
            }
        });

        initPreviewButton();
    }

    private void initPreviewButton() {

        previewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = new Stage();
                Scene scene = new Scene(new ImagePane(exerciseDefinition), 800, Screen.getPrimary().getBounds().getHeight());
                stage.setScene(scene);
                stage.show();
            }
        });


    }

    public void setExerciseDefinition(ExerciseDefinition exerciseDefinition) {

        bindFields(this.exerciseDefinition, exerciseDefinition);

        this.exerciseDefinition = exerciseDefinition;
        if (this.exerciseDefinition != null) {
            selectedTags.addAll(this.exerciseDefinition.getTags());
        }

        exerciseDefinitionPane.setVisible(this.exerciseDefinition != null);

    }

    private void bindFields(ExerciseDefinition oldValue, ExerciseDefinition newValue) {

        if (oldValue != null) {
            unbind(oldValue);
        }
        this.exerciseDefinition = null;
        selectedTags.clear();

        if (newValue != null) {
            bind(newValue);
        }

    }

    private void unbind(ExerciseDefinition oldValue) {
        Bindings.unbindBidirectional(minutesField.textProperty(), oldValue.minutesProperty());
        Bindings.unbindBidirectional(bpmField.textProperty(), oldValue.bpmProperty());
        Bindings.unbindBidirectional(filesField.textProperty(), oldValue.attachmentsAsStringProperty());
        Bindings.unbindBidirectional(titleField.textProperty(), oldValue.titleProperty());
        Bindings.unbindBidirectional(descriptionField.textProperty(), oldValue.descriptionProperty());
        Bindings.unbindBidirectional(ratingField.valueProperty(), oldValue.ratingProperty());
    }

    private void bind(ExerciseDefinition newValue) {
        Bindings.bindBidirectional(minutesField.textProperty(), newValue.minutesProperty(), (StringConverter) new IntegerStringConverter());
        Bindings.bindBidirectional(bpmField.textProperty(), newValue.bpmProperty(), (StringConverter) new IntegerStringConverter());
        Bindings.bindBidirectional(filesField.textProperty(), newValue.attachmentsAsStringProperty());
        Bindings.bindBidirectional(titleField.textProperty(), newValue.titleProperty());
        Bindings.bindBidirectional(descriptionField.textProperty(), newValue.descriptionProperty());
        Bindings.bindBidirectional(ratingField.valueProperty(), newValue.ratingProperty());
    }

    private void initDropListener() {

        tagPanel.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent dragEvent) {

                Object gestureSource = dragEvent.getGestureSource();
                if (gestureSource instanceof PlanningController.TagButton) {
                    PlanningController.TagButton tagButton = (PlanningController.TagButton) gestureSource;
                    if (!selectedTags.contains(tagButton.getTag())) {
                        dragEvent.acceptTransferModes(TransferMode.COPY);
                        dragEvent.consume();
                    }

                }

            }
        });

        tagPanel.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Object gestureSource = dragEvent.getGestureSource();
                if (gestureSource instanceof PlanningController.TagButton) {

                    PlanningController.TagButton tagButton = ((PlanningController.TagButton) gestureSource);
                    selectedTags.add(tagButton.getTag());
                    dragEvent.setDropCompleted(true);

                }

            }
        });

        selectedTags.addListener(new ListChangeListener<Tag>() {
            @Override
            public void onChanged(Change<? extends Tag> change) {

                if (exerciseDefinition != null) {
                    exerciseDefinition.setTags(new ArrayList<>(selectedTags));

                }

                while (change.next()) {
                    if (change.wasAdded()) {
                        for (Tag tag : change.getAddedSubList()) {
                            tagPanel.getChildren().add(new TagLabel(tag));
                        }
                    }

                    if (change.wasRemoved()) {
                        for (Node node : new ArrayList<>(tagPanel.getChildren())) {
                            TagLabel tagLabel = (TagLabel) node;
                            if (change.getRemoved().contains(tagLabel.getTag())) {
                                tagPanel.getChildren().remove(tagLabel);
                            }
                        }

                    }
                }

            }
        });
    }

    private class TagLabel extends Label {
        private Tag tag;

        private TagLabel(final Tag tag) {
            super(tag.getName());
            this.tag = tag;
            getStyleClass().add("tag");

            setOnDragDetected(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    selectedTags.remove(tag);
                    event.consume();
                }
            });

        }

        public Tag getTag() {
            return tag;
        }
    }

}
