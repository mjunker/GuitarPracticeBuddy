package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import javafx.scene.control.TreeItem;

public class PathExtractor {

    private final TreeItem path;
    private boolean userModelIsExcerciseDef;
    private boolean userModelIsPracticeWeek;
    private boolean exerciseDefInPracticeWeek;
    private PracticeWeek practiceWeek;
    private ExerciseDefinition exerciseDefinition;

    public PathExtractor(TreeItem path) {
        this.path = path;
    }

    public PathExtractor invoke() {

        if (path != null) {
            Object userObject = path.getValue();
            TreeItem parent = path.getParent();

            if (parent != null) {
                exerciseDefInPracticeWeek = parent.getValue() instanceof PracticeWeek;
                if (exerciseDefInPracticeWeek) {
                    extractPracticeWeek(parent.getValue());
                }
            }

            userModelIsExcerciseDef = userObject instanceof ExerciseDefinition;
            if (userModelIsExcerciseDef) {
                this.exerciseDefinition = (ExerciseDefinition) userObject;
            }
            userModelIsPracticeWeek = userObject instanceof PracticeWeek;
            if (userModelIsPracticeWeek) {
                extractPracticeWeek(userObject);
            }
        }

        return this;
    }

    private void extractPracticeWeek(Object userObject) {
        this.practiceWeek = (PracticeWeek) userObject;
    }

    public PracticeWeek getPracticeWeek() {
        return practiceWeek;
    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }

}
