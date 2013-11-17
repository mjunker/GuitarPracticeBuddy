package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.joda.time.DateTime;

public class PlanningTree extends AbstractExerciseDefintionTreeView {

    private PracticeWeek selectedPracticeWeek;

    private boolean isExerciseDefOfPracticeWeekSelected() {
        return selectedExerciseDefinition != null && selectedPracticeWeek != null;
    }

    private boolean isPracticeWeekSelected() {
        return selectedExerciseDefinition == null && selectedPracticeWeek != null;
    }

    public PracticeWeek getSelectedPracticeWeek() {
        return selectedPracticeWeek;
    }

    protected void deleteFromModel() {
        if (isExerciseDefOfPracticeWeekSelected()) {
            selectedPracticeWeek.deleteExerciseDef(selectedExerciseDefinition);
            removeSelectedNodeFromTree();
        } else if (isPracticeWeekSelected()) {
            practiceBuddyBean.deletePracticeWeek(selectedPracticeWeek);
            removeSelectedNodeFromTree();

        }

    }

    protected void addSelectionListener() {

        super.addSelectionListener();
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object o2) {
                PathExtractor pathExtractor = new PathExtractor((TreeItem) getSelectionModel().getSelectedItem()).invoke();
                selectedPracticeWeek = pathExtractor.getPracticeWeek();

            }
        });

    }

    private void removeSelectedNodeFromTree() {

        removeNode((TreeItem) getSelectionModel().getSelectedItem());
    }

    protected void buildTree() {

        root.getChildren().clear();
        addPracticePlanModels();
    }

    @Override
    protected ContextMenuFactory getContextMenuFactory() {
        return new ContextMenuFactory() {
            @Override
            public ContextMenu createPracticePlanRootMenu() {
                MenuItem addItem = new MenuItem("Add practice plan");
                addItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        PracticeWeek newPracticePlan = PracticeBuddyBean.getInstance()
                                .createNewPracticePlan(DateTime.now().toLocalDate(), DateTime.now().plusDays(6).toLocalDate());
                        root.getChildren().add(new TreeItem<>(newPracticePlan));

                    }
                });
                return new ContextMenu(addItem);
            }
        };
    }

    private void addPracticePlanModels() {
        for (final PracticeWeek practiceWeek : practiceBuddyBean.getPracticeWeeks()) {
            TreeItem node = createPracticeWeekNode(practiceWeek);
            root.getChildren().add(node);
            addExerciseDefNodes(practiceWeek, node);

        }
    }

    private TreeItem createPracticeWeekNode(final PracticeWeek practiceWeek) {
        return new TreeItem(practiceWeek);
    }

    private void addExerciseDefNodes(PracticeWeek practiceWeek, TreeItem practicePlanNode) {
        for (final ExerciseDefinition exerciseDefinition : practiceWeek.getExerciseDefinitions()) {
            TreeItem node = createExcerciseDefNode(exerciseDefinition);
            practicePlanNode.getChildren().add(0, node);

        }
    }

}
