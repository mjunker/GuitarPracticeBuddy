package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseInstance;
import ch.guitarpracticebuddy.ui.ExerciseTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;

public class TimerController {

    private ButtonBase startButton;
    private ExerciseTimer timer;
    private ExerciseInstance exerciseInstance;
    private boolean metronomeEnabled;

    public TimerController(ButtonBase startButton) {
        this.startButton = startButton;
        this.startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                toggleTimer();
            }
        });
    }

    public void toggleTimer() {

        if (timer == null) {
            return;
        }
        if (timer.isRunning()) {
            stopTimer();
        } else {
            startTimer();
        }

    }

    public void setExerciseInstance(ExerciseInstance exerciseInstance) {

        this.exerciseInstance = exerciseInstance;

        stopCurrentTimerIfNecessary();
        initTimerIfNecessary();
    }

    private void stopCurrentTimerIfNecessary() {
        if (timer != null && !timer.getExerciseInstance().equals(this.exerciseInstance)) {
            stopTimer();
            timer = null;
        }
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
        if (exerciseInstance != null && (timer == null
                || !timer.getExerciseInstance().equals(exerciseInstance))) {
            timer = new ExerciseTimer(exerciseInstance, this);
            timer.setMetronomeEnabled(metronomeEnabled);

        }
    }

    public void setMetronomeEnabled(boolean metronomeEnabled) {
        if (timer != null) {
            this.timer.setMetronomeEnabled(metronomeEnabled);
        }
        this.metronomeEnabled = metronomeEnabled;
    }
}
