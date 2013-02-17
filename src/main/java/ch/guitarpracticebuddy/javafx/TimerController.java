package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.ui.ExerciseTimer;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.MouseEvent;

public class TimerController {

    private ButtonBase startButton;
    private ExerciseTimer timer;
    private ExerciseDefinition exerciseDefinition;

    public TimerController(ButtonBase startButton) {
        this.startButton = startButton;
        this.startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent actionEvent) {
                toggleTimer();
            }
        });
    }

    public void restartBpmTime() {
        if (this.timer != null) {
            this.timer.restartBpmTimer();
        }
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
        if (timer == null || !timer.getExerciseDefinition().equals(exerciseDefinition)) {
            timer = new ExerciseTimer(exerciseDefinition);
        }
    }
}
