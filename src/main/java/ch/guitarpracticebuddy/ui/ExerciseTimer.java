package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.util.SoundFile;
import ch.guitarpracticebuddy.util.SoundUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/3/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
class ExerciseTimer extends Timer {

    public static final int INTERVAL = 50;
    private final ExerciseDefinition exerciseDefinition;
    private final Timer clickTimer;
    private final PracticeForm practiceForm;
    private int currentTime = 0;


    ExerciseTimer(final PracticeForm practiceForm, final ExerciseDefinition exerciseDefinition) {
        super(INTERVAL, null);

        this.practiceForm = practiceForm;
        this.exerciseDefinition = exerciseDefinition;
        this.clickTimer = new Timer(exerciseDefinition.getClickIntervalInMs(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundUtil.playSound(SoundFile.CLICK);

            }
        });
        this.addActionListener(new MyActionListener(practiceForm, exerciseDefinition));


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
    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }

    private class MyActionListener implements ActionListener {

        private final PracticeForm practiceForm;
        private final ExerciseDefinition exerciseDefinition;

        public MyActionListener(PracticeForm practiceForm, ExerciseDefinition exerciseDefinition) {
            this.practiceForm = practiceForm;
            this.exerciseDefinition = exerciseDefinition;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateCurrentState();
            Timer timer = (Timer) e.getSource();

            if (hasExerciseChanged()) {
                timer.stop();
            }
            if (isTimeUp()) {
                finishTimer(timer);
            }

        }

        private void updateCurrentState() {
            currentTime += INTERVAL;
            practiceForm.updateProgressBar(currentTime);
        }

        private boolean hasExerciseChanged() {
            return practiceForm.getSelectedExercise() != exerciseDefinition;
        }

        private void finishTimer(Timer timer) {
            SoundUtil.playSound(SoundFile.DONE);
            timer.stop();
            exerciseDefinition.getTodaysExercises().setDone();
            practiceForm.selectNextExcercise();
            practiceForm.resetTimer();
        }


        private boolean isTimeUp() {
            return currentTime > exerciseDefinition.getMiliSeconds();
        }


    }
}
