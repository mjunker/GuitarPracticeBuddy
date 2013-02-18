package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.javafx.PracticeController;
import ch.guitarpracticebuddy.util.SoundFile;
import ch.guitarpracticebuddy.util.SoundUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class ExerciseTimer {

    public static final int INTERVAL = 50;
    private final ExerciseDefinition exerciseDefinition;
    private Timeline timer;
    private Timeline bpmTimer;
    private int currentTime = 0;
    private int bpm = 100;
    private boolean metronomeEnabled = true;
    private final PracticeController practiceController;

    public ExerciseTimer(final ExerciseDefinition exerciseDefinition, PracticeController practiceController) {

        this.practiceController = practiceController;
        this.currentTime = exerciseDefinition.getTodaysExercises().getPracticedTime();
        this.exerciseDefinition = exerciseDefinition;
        initBpmTimer();
        timer = new Timeline(new KeyFrame(Duration.millis(INTERVAL), new TimerActionListener(exerciseDefinition)));
        timer.setCycleCount(Timeline.INDEFINITE);

    }

    private void initBpmTimer() {

        bpmTimer = new Timeline(new KeyFrame(Duration.millis(exerciseDefinition.getClickIntervalInMs()), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (metronomeEnabled) {
                    SoundUtil.playSound(SoundFile.CLICK);
                }
            }
        }));
        bpmTimer.setCycleCount(Timeline.INDEFINITE);

    }

    public void start() {
        this.bpmTimer.play();
        this.timer.play();
    }

    public void stop() {

        this.bpmTimer.stop();
        this.timer.stop();

    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }

    public void restartBpmTimer() {
        if (this.bpmTimer != null) {
            this.bpmTimer.stop();

        }
        initBpmTimer();
        if (isRunning()) {
            bpmTimer.play();
        }
    }

    public boolean isRunning() {
        return timer.getStatus() == Animation.Status.RUNNING;
    }

    private class TimerActionListener implements EventHandler<ActionEvent> {

        private final ExerciseDefinition exerciseDefinition;

        public TimerActionListener(ExerciseDefinition exerciseDefinition) {
            this.exerciseDefinition = exerciseDefinition;
        }

        private void updateCurrentState() {
            currentTime += INTERVAL;
            exerciseDefinition.getTodaysExercises().setPracticedTime(currentTime);
            practiceController.refreshProgress();

        }

        private void finishTimer() {

            SoundUtil.playSound(SoundFile.DONE);
            ExerciseTimer.this.stop();
            exerciseDefinition.getTodaysExercises().finish(bpm, currentTime);
            practiceController.refresh();
        }

        private boolean isTimeUp() {
            return currentTime > exerciseDefinition.getMiliSeconds();
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            updateCurrentState();
            if (isTimeUp()) {
                finishTimer();
            }
        }
    }
}
