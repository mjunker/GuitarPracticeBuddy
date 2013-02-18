package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.Tag;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlanningController implements Initializable {

    @FXML
    private Pane formPanel;
    @FXML
    private TableView weekOverviewTable;
    @FXML
    private FlowPane tagPanel;
    @FXML
    private PlanningTree planningTree;
    private ExerciseDefinitionFormController formController;

    private PracticeBuddyBean practiceBuddyBean;
    private List<Tag> selectedTags = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.practiceBuddyBean = PracticeBuddyBean.getInstance();
        initForm();
        updateTagPanel();
        planningTree.setPracticeBuddyBean(PracticeBuddyBean.getInstance());
        planningTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {

            @Override
            public void changed(ObservableValue<? extends TreeItem> observableValue, TreeItem treeItem, TreeItem treeItem2) {
                if (treeItem2 != null && treeItem2.getValue() instanceof ExerciseDefinition) {
                    formController.setExerciseDefinition((ExerciseDefinition) treeItem2.getValue());
                } else {
                    formController.setExerciseDefinition(null);
                }
            }
        });

    }

    private void updateTagPanel() {
        tagPanel.getChildren().clear();
        for (final Tag tag : practiceBuddyBean.getTags()) {
            final ToggleButton tagButton = new ToggleButton();
            tagButton.setText(tag.getName());
            tagButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    toggleTagSelected(tag);
                }
            });

            tagPanel.getChildren().add(tagButton);

        }
    }

    private void toggleTagSelected(Tag tag) {
        if (this.selectedTags.contains(tag)) {
            this.selectedTags.remove(tag);
        } else {
            this.selectedTags.add(tag);
        }
        filterExercises();
    }

    private void filterExercises() {
        planningTree.setTagFilter(selectedTags);
    }

    private void initForm() {
        FxmlLoader fxmlLoader = new FxmlLoader();
        Parent root = fxmlLoader.load("exerciseDefinitionForm.fxml");
        formController = fxmlLoader.getFxmlLoader().getController();
        formPanel.getChildren().add(root);

    }

    public void refresh() {
        //To change body of created methods use File | Settings | File Templates.
    }
}
