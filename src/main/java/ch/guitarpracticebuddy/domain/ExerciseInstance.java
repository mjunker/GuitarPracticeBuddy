package ch.guitarpracticebuddy.domain;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ExerciseInstance {

    @GeneratedValue
    @Id
    private int id;
    @Getter
    private DateTime day;
    private ExerciseStatus status = ExerciseStatus.PLANNED;
    private int actualBpm;
    private int practicedTime;

    @ManyToOne
    @Setter
    private ExerciseDefinition exerciseDefinition;

    public ExerciseInstance(DateTime day) {
        this.day = day;
    }

    public boolean isForToday() {
        return DateTimeComparator.getDateOnlyInstance().compare(day, null) == 0;
    }

    public void setDone() {
        this.status = ExerciseStatus.DONE;
    }

    public boolean isDone() {
        return this.status == ExerciseStatus.DONE;
    }

    public void setDone(boolean selected) {
        if (selected) {
            setDone();
        } else {
            status = ExerciseStatus.PLANNED;
        }
    }

    public void skip() {
        this.status = ExerciseStatus.SKIPPED;
    }

    public boolean isInInterval(Interval interval) {
        return interval.contains(getDay());
    }

    public boolean isSkipped() {
        return status == ExerciseStatus.SKIPPED;
    }

    public void finish(int achievedBpm, int practicedTime) {
        setDone();
        setPracticedTime(practicedTime);
        this.actualBpm = achievedBpm;

    }

    public int getPracticedTime() {
        return practicedTime;
    }

    public void setPracticedTime(int practicedTime) {
        this.practicedTime = practicedTime;
    }

    public int getBpm() {
        return this.actualBpm;

    }

    public void setBpm(int bpm) {
        this.actualBpm = bpm;

    }

    public double getBpmAsDouble() {
        return this.getBpm();
    }

    public void setBpmAsDouble(double bpm) {
        this.actualBpm = (int) bpm;
    }

    public double getPracticeTimeProgress() {
        return ((float) practicedTime) / exerciseDefinition.getMiliSeconds();
    }

    public void setPracticeTimeProgress(double progress) {
    }
}
