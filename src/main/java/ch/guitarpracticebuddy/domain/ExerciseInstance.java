package ch.guitarpracticebuddy.domain;

import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ExerciseInstance {

    @GeneratedValue
    @Id
    private int id;

    @Getter
    private DateTime day;
    private boolean done;
    private int actualBpm;

    public ExerciseInstance(DateTime day) {
        this.day = day;
    }

    public boolean isForToday() {
        return DateTimeComparator.getDateOnlyInstance().compare(day, null) == 0;
    }

    public void setDone() {
        this.done = true;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isInInterval(Interval interval) {
        return interval.contains(getDay());
    }

    public void setDone(boolean selected) {
        this.done = selected;
    }
}
