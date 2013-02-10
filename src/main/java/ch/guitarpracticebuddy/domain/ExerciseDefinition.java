package ch.guitarpracticebuddy.domain;

import lombok.Getter;
import lombok.Setter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Getter
@Entity
public class ExerciseDefinition {

    @Id
    @GeneratedValue
    private int id;

    @Setter
    private String title = "New exercise";

    @Setter
    private String description;

    @Setter
    private int minutes;

    @Setter
    private int bpm;

    private List<Tag> tags = new ArrayList<Tag>();

    @Setter
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ExerciseAttachment> attachments = new ArrayList<ExerciseAttachment>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ExerciseInstance> plannedInstances = new ArrayList<ExerciseInstance>();
    private List<ExerciseInstance> pastInstances = new ArrayList<ExerciseInstance>();

    public ExerciseInstance getTodaysExercises() {
        return selectFirst(plannedInstances,
                having(on(ExerciseInstance.class).isForToday(), equalTo(true)));
    }

    public void deleteInstance(DateTime date) {
        plannedInstances.removeAll(select(plannedInstances,
                having(on(ExerciseInstance.class).getDay(), equalTo(date))));
    }

    public void createInstancesForEntireWeek(Interval interval) {
        for (LocalDate localDate : new LocalDateRange(interval)) {
            ExerciseInstance instance = new ExerciseInstance(localDate.toDateTimeAtCurrentTime());
            plannedInstances.add(instance);
        }
    }

    public List<ExerciseInstance> getPlannedInstances() {
        return plannedInstances;
    }

    public boolean isPlannedForToday() {
        return getTodaysExercises() != null;
    }

    public int getSeconds() {
        return getMinutes() * 60;
    }

    public int getMiliSeconds() {
        return getSeconds() * 1000;
    }

    public int getClickIntervalInMs() {
        if (getBpm() == 0) {
            return Integer.MAX_VALUE;
        }
        return Math.round(60.00f / getBpm() * 1000);
    }

    public void tag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void deleteInstances(Iterable<ExerciseInstance> exerciseInstances) {
        for (ExerciseInstance exerciseInstance : exerciseInstances) {
            if (!exerciseInstance.isDone()) {
                plannedInstances.remove(exerciseInstance);
            }
        }
    }

    public int getBpm() {

        List<ExerciseInstance> instancesNotInFuture = select(plannedInstances, having(on(ExerciseInstance.class).getDay(), new IsNotInFuture()));
        ExerciseInstance exerciseInstanceWithMaxBpm = selectMax(instancesNotInFuture, on(ExerciseInstance.class).getDay());
        if (exerciseInstanceWithMaxBpm != null && exerciseInstanceWithMaxBpm.getBpm() > 0) {
            return exerciseInstanceWithMaxBpm.getBpm();
        }
        return bpm;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    private static class IsNotInFuture extends BaseMatcher<DateTime> {
        @Override
        public boolean matches(Object o) {
            DateTime dateTime = (DateTime) o;
            return dateTime.isBefore(DateTime.now().withTimeAtStartOfDay().plusDays(1));
        }

        @Override
        public void describeTo(Description description) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
