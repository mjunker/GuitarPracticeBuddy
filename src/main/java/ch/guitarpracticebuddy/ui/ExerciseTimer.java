package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseInstance;
import ch.guitarpracticebuddy.javafx.TimerController;
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
    private int currentTime = 0;
    private int bpm = 100;
    private boolean metronomeEnabled = true;
    private final TimerController timerController;
    private Runnable runnable;

    public ExerciseTimer(final ExerciseInstance exerciseInstance, TimerController timerController) {

        this.timerController = timerController;
        this.currentTime = exerciseInstance.getPracticedTime();
        this.exerciseInstance = exerciseInstance;
        initBpmTimer();
        timer = new Timeline(new KeyFrame(Duration.millis(INTERVAL), new TimerActionListener(exerciseInstance)));
        timer.setCycleCount(Timeline.INDEFINITE);

    }

    private void initBpmTimer() {

        runnable = new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    if (metronomeEnabled) {
                        SoundUtil.playSound(SoundFile.CLICK);
                    }
                    try {
                        Thread.sleep(exerciseInstance.getClickIntervalInMs());
                    } catch (InterruptedException e) {

                    }
                }

            }
        };
    }

    public void start() {

        this.timer.play();
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void stop() {

        this.timer.stop();

    }

    public void setMetronomeEnabled(boolean metronomeEnabled) {
        this.metronomeEnabled = metronomeEnabled;
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
            timerController.stopTimer();
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
