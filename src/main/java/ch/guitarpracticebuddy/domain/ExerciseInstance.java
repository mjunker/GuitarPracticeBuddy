package ch.guitarpracticebuddy.domain;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;

import javax.persistence.*;

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
    @Getter
    private ExerciseDefinition exerciseDefinition;
    @Transient
    private FloatProperty practiceTimeProperty;
    @Transient
    private IntegerProperty bpmProperty;
    @Transient
    private ObjectProperty statusProperty;

    public ExerciseInstance(DateTime day) {
        this.day = day;
    }

    public boolean isForToday() {
        return DateTimeComparator.getDateOnlyInstance().compare(day, null) == 0;
    }

    public String getTitle() {
        return exerciseDefinition.getTitle();
    }

    public int getClickIntervalInMs() {

        if (getBpm() == 0) {
            return Integer.MAX_VALUE;
        }
        return Math.round(60.00f / getBpm() * 1000);
    }

    public void setDone() {
        setStatus(ExerciseStatus.DONE);
    }

    public void setStatus(ExerciseStatus status) {
        this.status = status;
        statusProperty().set(status);
    }

    public ExerciseStatus getStatus() {
        return status;
    }

    public boolean isDone() {
        return this.status == ExerciseStatus.DONE;
    }

    public void skip() {
        setStatus(ExerciseStatus.SKIPPED);
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
        setBpm(achievedBpm);

    }

    public int getPracticedTime() {
        return practicedTime;
    }

    public void setPracticedTime(int practicedTime) {
        this.practicedTime = practicedTime;
        practiceTimeProperty().set(this.practicedTime);
    }

    public int getBpm() {
        return this.actualBpm;

    }

    public void setBpm(int bpm) {
        this.actualBpm = bpm;
        this.bpmProperty().setValue(bpm);

    }

    public NumberBinding practiceTimeProgressProperty() {
        return practiceTimeProperty().divide(exerciseDefinition.milisecondsProperty());
    }

    public FloatProperty practiceTimeProperty() {
        if (this.practiceTimeProperty == null) {
            this.practiceTimeProperty = new SimpleFloatProperty(this, "practiceTime");
            this.practiceTimeProperty.set(getPracticedTime());
        }
        return this.practiceTimeProperty;
    }

    public ObjectProperty<ExerciseStatus> statusProperty() {
        if (this.statusProperty == null) {
            this.statusProperty = new SimpleObjectProperty<ExerciseStatus>(this, "status") {
                @Override
                public void set(ExerciseStatus o) {
                    super.set(o);
                    status = o;
                }
            };
            this.statusProperty.set(status);
        }
        return this.statusProperty;
    }

    public Property<Number> bpmProperty() {
        if (this.bpmProperty == null) {
            this.bpmProperty = new SimpleIntegerProperty(this, "bpm") {
                @Override
                public void set(int i) {
                    super.set(i);
                    actualBpm = i;
                }
            };
            this.bpmProperty.set(getBpm());
        }
        return this.bpmProperty;
    }

    public Property<String> titleProperty() {
        return exerciseDefinition.titleProperty();
    }

    public void reset() {
        setPracticedTime(0);
        setStatus(ExerciseStatus.PLANNED);
    }
}
