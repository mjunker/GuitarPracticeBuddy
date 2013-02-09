package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.util.SoundFile;
import ch.guitarpracticebuddy.util.SoundUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ExerciseTimer extends Timer {

    public static final int INTERVAL = 50;
    private final ExerciseDefinition exerciseDefinition;
    private final Timer clickTimer;
    private final PracticeForm practiceForm;
    private int currentTime = 0;

    ExerciseTimer(final PracticeForm practiceForm, final ExerciseDefinition exerciseDefinition) {
        super(INTERVAL, null);

        this.currentTime = exerciseDefinition.getTodaysExercises().getPracticedTime();
        this.practiceForm = practiceForm;
        this.exerciseDefinition = exerciseDefinition;
        this.clickTimer = new Timer(exerciseDefinition.getClickIntervalInMs(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (practiceForm.isMetronomeEnabled()) {
                    SoundUtil.playSound(SoundFile.CLICK);
                }

            }
        });
        this.addActionListener(new TimerActionListener(practiceForm, exerciseDefinition));

    }

    @Override
    public void start() {
        super.start();
        this.clickTimer.start();
    }

    @Override
    public void stop() {
        super.stop();
        this.clickTimer.stop();
        exerciseDefinition.getTodaysExercises().setPracticedTime(currentTime);

    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }

    private class TimerActionListener implements ActionListener {

        private final PracticeForm practiceForm;
        private final ExerciseDefinition exerciseDefinition;

        public TimerActionListener(PracticeForm practiceForm, ExerciseDefinition exerciseDefinition) {
            this.practiceForm = practiceForm;
            this.exerciseDefinition = exerciseDefinition;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateCurrentState();

            if (hasExerciseChanged()) {
                ExerciseTimer.this.stop();
            }
            if (isTimeUp()) {
                finishTimer();
            }

        }

        private void updateCurrentState() {
            currentTime += INTERVAL;
            practiceForm.updateProgressBar(currentTime);
            exerciseDefinition.getTodaysExercises().setPracticedTime(currentTime);

        }

        private boolean hasExerciseChanged() {
            return practiceForm.getSelectedExercise() != exerciseDefinition;
        }

        private void finishTimer() {

            SoundUtil.playSound(SoundFile.DONE);
            ExerciseTimer.this.stop();
            exerciseDefinition.getTodaysExercises().finish(practiceForm.getBpm(), currentTime);
            practiceForm.selectNextExcercise();
            practiceForm.resetTimer();
        }

        private boolean isTimeUp() {
            return currentTime > exerciseDefinition.getMiliSeconds();
        }

    }
}
