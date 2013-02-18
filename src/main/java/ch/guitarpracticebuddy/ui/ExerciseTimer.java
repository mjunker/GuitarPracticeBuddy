package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseInstance;
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
    private final ExerciseInstance exerciseInstance;
    private Timeline timer;
    private Timeline bpmTimer;
    private int currentTime = 0;
    private int bpm = 100;
    private boolean metronomeEnabled = true;

    public ExerciseTimer(final ExerciseInstance exerciseInstance) {

        this.currentTime = exerciseInstance.getPracticedTime();
        this.exerciseInstance = exerciseInstance;
        initBpmTimer();
        timer = new Timeline(new KeyFrame(Duration.millis(INTERVAL), new TimerActionListener(exerciseInstance)));
        timer.setCycleCount(Timeline.INDEFINITE);

    }

    private void initBpmTimer() {

        bpmTimer = new Timeline(new KeyFrame(Duration.millis(exerciseInstance.getClickIntervalInMs()), new EventHandler<ActionEvent>() {

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

    public void restartBpmTimer() {
        if (this.bpmTimer != null) {
            this.bpmTimer.stop();

        }
        initBpmTimer();
        if (isRunning()) {
            bpmTimer.play();
        }
    }

    public ExerciseInstance getExerciseInstance() {
        return exerciseInstance;
    }

    public boolean isRunning() {
        return timer.getStatus() == Animation.Status.RUNNING;
    }

    private class TimerActionListener implements EventHandler<ActionEvent> {

        private final ExerciseInstance exerciseInstance;

        public TimerActionListener(ExerciseInstance exerciseInstance) {
            this.exerciseInstance = exerciseInstance;
        }

        private void updateCurrentState() {
            currentTime += INTERVAL;
            exerciseInstance.setPracticedTime(currentTime);

        }

        private void finishTimer() {

            SoundUtil.playSound(SoundFile.DONE);
            ExerciseTimer.this.stop();
            exerciseInstance.finish(bpm, currentTime);
        }

        private boolean isTimeUp() {
            return currentTime > exerciseInstance.getExerciseDefinition().getMiliSeconds();
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
