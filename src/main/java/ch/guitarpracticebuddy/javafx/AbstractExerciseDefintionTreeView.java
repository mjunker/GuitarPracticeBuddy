package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 11/17/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractExerciseDefintionTreeView extends TreeView {

    protected PracticeBuddyBean practiceBuddyBean = PracticeBuddyBean.getInstance();
    protected TreeItem root = new TreeItem();
    protected ExerciseDefinition selectedExerciseDefinition;

    public AbstractExerciseDefintionTreeView() {
        initTreeModel();
        addSelectionListener();
        addKeyListener();
        configure();
    }

    protected abstract void buildTree();

    public void refresh() {
        buildTree();
    }

    private void initTreeModel() {

        setRoot(root);

    }

    private void configure() {
        setShowRoot(false);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell call(TreeView p) {
                return new ExerciseDefinitionTreeCell(root, getContextMenuFactory());
            }
        });

    }

    protected abstract ContextMenuFactory getContextMenuFactory();

    private void addKeyListener() {

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                    deleteFromModel();
                }
            }
        });

    }

    protected abstract void deleteFromModel();

    protected void addSelectionListener() {
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object o2) {
                PathExtractor pathExtractor = new PathExtractor((TreeItem) getSelectionModel().getSelectedItem()).invoke();
                selectedExerciseDefinition = pathExtractor.getExerciseDefinition();

            }
        });

    }

    protected void removeNode(TreeItem nodeToRemove) {

        nodeToRemove.getParent().getChildren().remove(nodeToRemove);
    }

    protected TreeItem createExcerciseDefNode(final ExerciseDefinition exerciseDefinition) {
        return new TreeItem(exerciseDefinition);
    }

    protected void removeSelectedNodesWithSameUserObjectFromTree(Object selectedNode) {

        removeAllNodesWithSameModel(getRoot(), selectedNode);

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
}
