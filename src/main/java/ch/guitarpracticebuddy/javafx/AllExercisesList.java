package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.Rating;
import ch.guitarpracticebuddy.domain.Tag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 11/17/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class AllExercisesList extends AbstractExerciseDefintionTreeView {

    private List<Tag> selectedTags = new ArrayList<>();
    private List<Rating> selectedRatings = new ArrayList<>();
    private List<ExerciseDeletedListener> changeListener = new ArrayList<>();

    @Override
    protected ContextMenuFactory getContextMenuFactory() {
        return new ContextMenuFactory() {
            @Override
            public ContextMenu createPracticePlanRootMenu() {
                MenuItem addItem = new MenuItem("Add exercise");
                addItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        ExerciseDefinition newExerciseDefinition = PracticeBuddyBean.getInstance().createNewExerciseDefinition();
                        newExerciseDefinition.setTitle("New exercise");
                        root.getChildren().add(0, new TreeItem<>(newExerciseDefinition));

                    }
                });
                return new ContextMenu(addItem);
            }
        };
    }

    @Override
    protected void deleteFromModel() {
        practiceBuddyBean.deleteExerciseDefinition(selectedExerciseDefinition);
        removeSelectedNodesWithSameUserObjectFromTree(((TreeItem) getSelectionModel().getSelectedItem()).getValue());
        notifyListeners();
    }

    private void notifyListeners() {
        for (ExerciseDeletedListener exerciseDeletedListener : changeListener) {
            exerciseDeletedListener.deleted(selectedExerciseDefinition);
        }
    }

    public void setFilter(List<Tag> selectedTags, List<Rating> selectedRatings) {
        this.selectedTags = selectedTags;
        this.selectedRatings = selectedRatings;
        buildTree();

    }

    private void addAllExcercisesModels() {
        for (ExerciseDefinition exerciseDefinition : practiceBuddyBean.getExerciseDefinitions(selectedTags, selectedRatings)) {
            TreeItem node = createExcerciseDefNode(exerciseDefinition);
            root.getChildren().add(node);
        }
    }

    public void setChangeListener(ExerciseDeletedListener changeListener) {
        this.changeListener.add(changeListener);
    }

    protected void buildTree() {

        root.getChildren().clear();
        addAllExcercisesModels();
    }

}
