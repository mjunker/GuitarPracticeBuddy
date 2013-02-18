package ch.guitarpracticebuddy.domain;

import com.google.common.base.Preconditions;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Entity
public class PracticeWeek {

    @Id
    @GeneratedValue
    @Getter
    private int id;

    @Getter
    private LocalDate dateFrom;

    @Getter
    private LocalDate dateTo;

    @Getter
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<ExerciseDefinition> exerciseDefinitions = new ArrayList<ExerciseDefinition>();

    public PracticeWeek(LocalDate start, LocalDate end) {
        this.dateFrom = start;
        this.dateTo = end;
        assertValidWeek();
    }

    public boolean activate(ExerciseDefinition exerciseDefinition) {
        Preconditions.checkNotNull(exerciseDefinition);
        if (!exerciseDefinitions.contains(exerciseDefinition)) {
            exerciseDefinitions.add(exerciseDefinition);
            exerciseDefinition.createInstancesForEntireWeek(getInterval());
            return true;
        }
        return false;

    }

    private void assertValidWeek() {
        long days = getInterval().toDuration().getStandardDays();
        if (days != 6) {
            throw new IllegalStateException("practice week has to be exactly 7 days, instead was " + days);
        }
    }

    public boolean isForToday() {
        return getInterval().contains(DateTime.now());
    }

    public Interval getInterval() {
        return new Interval(dateFrom.toDateTimeAtStartOfDay(), dateTo.toDateTimeAtStartOfDay().plusDays(1).minusMillis(1));
    }

    public List<ExerciseInstance> getExerciseInstances(ExerciseDefinition exerciseDefinition) {

        return select(exerciseDefinition.getPlannedInstances(), having(on(ExerciseInstance.class).isInInterval(getInterval()), equalTo(true)));
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
        assertValidWeek();
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
        assertValidWeek();
    }

    public void deleteExerciseDef(ExerciseDefinition exerciseDef) {

        exerciseDef.deleteInstances(getExerciseInstances(exerciseDef));
        this.exerciseDefinitions.remove(exerciseDef);

    }

    public void clearAllExerciseInstances() {
        for (ExerciseDefinition exerciseDefinition : new ArrayList<ExerciseDefinition>(exerciseDefinitions)) {
            deleteExerciseDef(exerciseDefinition);
        }
    }

    public int calculateTotalMinutes() {

        int sum = 0;
        for (ExerciseDefinition exerciseDefinition : exerciseDefinitions) {
            sum += exerciseDefinition.getMinutes();
        }
        return sum;
    }

    public StringProperty titleProperty() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
