package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeWeek;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

class PathExtractor {

    private TreeSelectionEvent e;
    private boolean userModelIsExcerciseDef;
    private boolean userModelIsPracticeWeek;
    private boolean exerciseDefInPracticeWeek;
    private PracticeWeek practiceWeek;
    private ExerciseDefinition exerciseDefinition;

    public PathExtractor(TreeSelectionEvent e) {
        this.e = e;
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

        TreePath path = e.getPath();
        DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = lastPathComponent.getUserObject();

        if (path.getPathCount() > 1) {
            DefaultMutableTreeNode previousLastPath = (DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 2);
            exerciseDefInPracticeWeek = previousLastPath.getUserObject() instanceof PracticeWeek;
            if (exerciseDefInPracticeWeek) {
                practiceWeek = (PracticeWeek) previousLastPath.getUserObject();
            }
        }


        userModelIsExcerciseDef = userObject instanceof ExerciseDefinition;
        if (userModelIsExcerciseDef) {
            this.exerciseDefinition = (ExerciseDefinition) userObject;
        }
        userModelIsPracticeWeek = userObject instanceof PracticeWeek;
        if (userModelIsPracticeWeek) {
            this.practiceWeek = (PracticeWeek) userObject;
        }
        return this;
    }

    public PracticeWeek getPracticeWeek() {
        return practiceWeek;
    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }
}
