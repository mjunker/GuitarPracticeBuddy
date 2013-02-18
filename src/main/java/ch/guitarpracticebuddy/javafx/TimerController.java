package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.ui.ExerciseTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;

public class TimerController {

    private ButtonBase startButton;
    private ExerciseTimer timer;
    private ExerciseDefinition exerciseDefinition;
    private final PracticeController practiceController;

    public TimerController(ButtonBase startButton, PracticeController practiceController) {
        this.startButton = startButton;
        this.practiceController = practiceController;
        this.startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                toggleTimer();
            }
        });
    }

    public void restartBpmTime() {
        if (this.timer != null) {
            this.timer.restartBpmTimer();
        }
    }

    public void resetTimer() {
        if (this.timer != null) {

            this.timer.stop();
        }
        this.timer = null;
    }

    private void toggleTimer() {

        if (timer == null) {
            return;
        }
        if (timer.isRunning()) {
            stopTimer();
        } else {
            startTimer();
        }

    }

    public void setExerciseDefinition(ExerciseDefinition exerciseDefinition) {
        this.exerciseDefinition = exerciseDefinition;
        initTimerIfNecessary();
    }

    public void startTimer() {
        initTimerIfNecessary();
        timer.start();
        startButton.setText("Pause");
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
        startButton.setText("Play");
    }

    public void initTimerIfNecessary() {
        if (exerciseDefinition != null && (timer == null
                || !timer.getExerciseDefinition().equals(exerciseDefinition))) {
            timer = new ExerciseTimer(exerciseDefinition, practiceController);

        }
    }

}
