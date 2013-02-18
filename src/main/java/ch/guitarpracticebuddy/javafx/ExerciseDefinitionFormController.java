package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.Rating;
import ch.guitarpracticebuddy.domain.Tag;
import ch.guitarpracticebuddy.util.FileUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
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
    private ListView tagField;
    @FXML
    private ChoiceBox ratingField;
    @FXML
    private Button selectFilesButton;
    @FXML
    private Button previewButton;
    private PracticeBuddyBean practiceBuddyBean;
    private ExerciseDefinition exerciseDefinition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        practiceBuddyBean = PracticeBuddyBean.getInstance();
        ratingField.setItems(FXCollections.observableArrayList(Rating.values()));
        initTagField();

        selectFilesButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();

                List<File> files = fileChooser.showOpenMultipleDialog(null);
                if (files != null) {
                    appendFiles(files);
                }
            }
        });
    }

    private void appendFiles(List<File> selectedFiles) {
        List<String> relativePaths = FileUtil.copyFilesToApplicationHome(selectedFiles, exerciseDefinition);
        filesField.setText(ExerciseDefinition.createAttachmentString(relativePaths));

    }

    private void initTagField() {
        tagField.setItems(FXCollections.observableArrayList(PracticeBuddyBean.getInstance().getTags()));

        tagField.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object o2) {
                exerciseDefinition.setTags(tagField.getItems());
            }
        });

    }

    public void setExerciseDefinition(ExerciseDefinition exerciseDefinition) {
        bindFields(this.exerciseDefinition, exerciseDefinition);
        this.exerciseDefinition = exerciseDefinition;
        updateTags();

    }

    private void bindFields(ExerciseDefinition oldValue, ExerciseDefinition newValue) {

        if (oldValue != null) {
            Bindings.unbindBidirectional(minutesField.textProperty(), oldValue.minutesProperty());
            Bindings.unbindBidirectional(bpmField.textProperty(), oldValue.bpmProperty());
            Bindings.unbindBidirectional(filesField.textProperty(), oldValue.attachmentsAsStringProperty());
            Bindings.unbindBidirectional(titleField.textProperty(), oldValue.titleProperty());
            Bindings.unbindBidirectional(descriptionField.textProperty(), oldValue.descriptionProperty());
            Bindings.unbindBidirectional(ratingField.valueProperty(), oldValue.ratingProperty());
        }

        if (newValue != null) {
            Bindings.bindBidirectional(minutesField.textProperty(), newValue.minutesProperty(), (StringConverter) new IntegerStringConverter());
            Bindings.bindBidirectional(bpmField.textProperty(), newValue.bpmProperty(), (StringConverter) new IntegerStringConverter());
            Bindings.bindBidirectional(filesField.textProperty(), newValue.attachmentsAsStringProperty());
            Bindings.bindBidirectional(titleField.textProperty(), newValue.titleProperty());
            Bindings.bindBidirectional(descriptionField.textProperty(), newValue.descriptionProperty());
            Bindings.bindBidirectional(ratingField.valueProperty(), newValue.ratingProperty());
        }

    }

    private int[] getIndices(List<Tag> tags) {
        List<Integer> indices = new ArrayList<>();
        for (Tag tag : tags) {
            indices.add(practiceBuddyBean.getTags().indexOf(tag));
        }

        return toIntArray(indices);
    }

    private int[] toIntArray(List<Integer> indices) {

        int[] intArr = new int[indices.size()];
        for (Integer index : indices) {
            intArr[indices.indexOf(index)] = index;
        }
        return intArr;
    }

    private void updateTags() {

        if (exerciseDefinition != null && !exerciseDefinition.getTags().isEmpty()) {
            List<Tag> tags = this.practiceBuddyBean.getTags();
            int[] selectedIndices = getIndices(exerciseDefinition.getTags());
            sortTags(tags, selectedIndices);
            this.tagField.setItems(FXCollections.observableArrayList(tags));
            this.tagField.getSelectionModel().selectIndices(-1, toIntArray(createIndices()));
        } else {
            this.tagField.getSelectionModel().clearSelection();

        }

    }

    private List<Integer> createIndices() {

        List<Integer> indices = new ArrayList<>();
        int i = 0;
        for (Tag ignored : exerciseDefinition.getTags()) {
            indices.add(i);
            i++;
        }
        return indices;
    }

    private void sortTags(List<Tag> tags, int[] selectedIndizes) {
        for (int selectedIndices : selectedIndizes) {
            Tag removedTag = tags.remove(selectedIndices);
            tags.add(0, removedTag);
        }
    }

}
