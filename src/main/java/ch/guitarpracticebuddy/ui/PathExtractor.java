package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeWeek;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class PathExtractor {

    private boolean userModelIsExcerciseDef;
    private boolean userModelIsPracticeWeek;
    private boolean exerciseDefInPracticeWeek;
    private PracticeWeek practiceWeek;
    private ExerciseDefinition exerciseDefinition;
    private final TreePath path;
    private DefaultMutableTreeNode practiceWeekNode;

    public PathExtractor(TreePath path) {
        this.path = path;
    }

    public boolean isRootNodeSelected() {
        return exerciseDefinition == null && practiceWeek == null;
    }

    public boolean isUserModelIsExcerciseDef() {
        return userModelIsExcerciseDef;
    }

    public boolean isUserModelIsPracticeWeek() {
        return userModelIsPracticeWeek;
    }

    public boolean isExerciseDefInPracticeWeek() {
        return exerciseDefInPracticeWeek;
    }

    public PathExtractor invoke() {

        DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = lastPathComponent.getUserObject();

        if (path.getPathCount() > 1) {
            DefaultMutableTreeNode previousLastPath = (DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 2);
            exerciseDefInPracticeWeek = previousLastPath.getUserObject() instanceof PracticeWeek;
            if (exerciseDefInPracticeWeek) {
                extractPracticeWeek(previousLastPath, previousLastPath.getUserObject());

            }
        }

        userModelIsExcerciseDef = userObject instanceof ExerciseDefinition;
        if (userModelIsExcerciseDef) {
            this.exerciseDefinition = (ExerciseDefinition) userObject;
        }
        userModelIsPracticeWeek = userObject instanceof PracticeWeek;
        if (userModelIsPracticeWeek) {
            extractPracticeWeek(lastPathComponent, userObject);
        }
        return this;
    }

    private void extractPracticeWeek(DefaultMutableTreeNode lastPathComponent, Object userObject) {
        this.practiceWeek = (PracticeWeek) userObject;
        this.practiceWeekNode = lastPathComponent;
    }

    public PracticeWeek getPracticeWeek() {
        return practiceWeek;
    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }

    public DefaultMutableTreeNode getPracticeWeekNode() {
        return this.practiceWeekNode;
    }
}
