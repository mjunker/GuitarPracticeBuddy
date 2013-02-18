package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import ch.guitarpracticebuddy.domain.Tag;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class PlanningTree extends TreeView {

    private ExerciseDefinition selectedExerciseDefinition;
    private PracticeWeek selectedPracticeWeek;
    private PracticeBuddyBean practiceBuddyBean;
    private List<Tag> selectedTags = new ArrayList<>();
    private TreeItem root = new TreeItem();
    ;

    public PlanningTree() {
        addSelectionListener();
        addKeyListener();
        configure();
    }

    private void configure() {
        setRoot(root);
        setShowRoot(false);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell call(TreeView p) {
                return new ExerciseDefinitionTreeCell();
            }
        });

    }

    private void addKeyListener() {

        setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                    deleteFromModel();
                }
            }
        });

    }

    private void addSelectionListener() {
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object o2) {
                PathExtractor pathExtractor = new PathExtractor((TreeItem) getSelectionModel().getSelectedItem()).invoke();
                selectedExerciseDefinition = pathExtractor.getExerciseDefinition();
                selectedPracticeWeek = pathExtractor.getPracticeWeek();

            }
        });

    }

    private boolean isExerciseDefOfPracticeWeekSelected() {
        return selectedExerciseDefinition != null && selectedPracticeWeek != null;
    }

    private boolean isStandaloneExerciseDefSelected() {
        return selectedExerciseDefinition != null && selectedPracticeWeek == null;
    }

    private boolean isPracticeWeekSelected() {
        return selectedExerciseDefinition == null && selectedPracticeWeek != null;
    }

    public PracticeWeek getSelectedPracticeWeek() {
        return selectedPracticeWeek;
    }

    public void setTagFilter(List<Tag> selectedTags) {
        this.selectedTags = selectedTags;
        filter();
    }

    private void filter() {
        buildTree();
    }

    public void refresh() {
        buildTree();
    }

    private void deleteFromModel() {
        if (isExerciseDefOfPracticeWeekSelected()) {
            selectedPracticeWeek.deleteExerciseDef(selectedExerciseDefinition);
            removeSelectedNodeFromTree();

        } else if (isStandaloneExerciseDefSelected()) {
            practiceBuddyBean.deleteExerciseDefinition(selectedExerciseDefinition);
            removeSelectedNodesWithSameUserObjectFromTree();

        } else if (isPracticeWeekSelected()) {
            practiceBuddyBean.deletePracticeWeek(selectedPracticeWeek);
            removeSelectedNodeFromTree();

        }

    }

    private void removeSelectedNodesWithSameUserObjectFromTree() {

        removeAllNodesWithSameModel(getRoot(), ((TreeItem) getSelectionModel().getSelectedItem()).getValue());

    }

    private void removeAllNodesWithSameModel(TreeItem root, Object userObject) {
        if (root.getValue() != null && root.getValue().equals(userObject)) {
            removeNode(root);
        } else {
            for (TreeItem child : new ArrayList<TreeItem>(root.getChildren())) {
                removeAllNodesWithSameModel(child, userObject);

            }
        }

    }

    private void removeSelectedNodeFromTree() {

        removeNode((TreeItem) getSelectionModel().getSelectedItem());
    }

    private void removeNode(TreeItem nodeToRemove) {

        nodeToRemove.getParent().getChildren().remove(nodeToRemove);
    }

    private void buildTree() {

        root.getChildren().add(ExerciseDefinitionTreeCell.PRACTICE_PLAN_ROOT_NODE);
        root.getChildren().add(ExerciseDefinitionTreeCell.ALL_EXERCISES_NODE);

        addPracticePlanModels(ExerciseDefinitionTreeCell.PRACTICE_PLAN_ROOT_NODE);
        addAllExcercisesModels(ExerciseDefinitionTreeCell.ALL_EXERCISES_NODE);
        setRoot(root);
        expandAllRows();
    }

    private void expandAllRows() {

        // TODO
    }

    private void addAllExcercisesModels(TreeItem rootNode) {
        for (ExerciseDefinition exerciseDefinition : practiceBuddyBean.getExerciseDefinitions(selectedTags)) {
            TreeItem node = createExcerciseDefNode(exerciseDefinition);
            rootNode.getChildren().add(node);
        }
    }

    private void addPracticePlanModels(TreeItem rootNode) {
        for (final PracticeWeek practiceWeek : practiceBuddyBean.getPracticeWeeks()) {
            TreeItem node = createPracticeWeekNode(practiceWeek);
            rootNode.getChildren().add(node);
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

    private TreeItem createExcerciseDefNode(final ExerciseDefinition exerciseDefinition) {
        return new TreeItem(exerciseDefinition);
    }

    public void setPracticeBuddyBean(PracticeBuddyBean practiceBuddyBean) {
        this.practiceBuddyBean = practiceBuddyBean;
        refresh();
    }
}
