package ch.guitarpracticebuddy.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
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
    private String code;
    @Setter
    private String title = "New exercise";
    @Setter
    private String description;
    @Setter
    private int minutes;
    @Setter
    private int bpm;
    @Setter
    private Rating rating = Rating.BEGINNER;
    private List<Tag> tags = new ArrayList<>();
    @Setter
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ExerciseAttachment> attachments = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ExerciseInstance> plannedInstances = new ArrayList<>();
    private List<ExerciseInstance> pastInstances = new ArrayList<>();

    public static String createAttachmentString(List<String> paths) {
        return Joiner.on("\n")
                .skipNulls()
                .join(paths);
    }

    public ExerciseInstance getTodaysExercises() {
        return selectFirst(plannedInstances,
                having(on(ExerciseInstance.class).isForToday(), equalTo(true)));
    }

    public void createInstancesForEntireWeek(Interval interval) {
        for (LocalDate localDate : new LocalDateRange(interval)) {
            ExerciseInstance instance = new ExerciseInstance(localDate.toDateTimeAtCurrentTime());
            instance.setExerciseDefinition(this);
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

    public int getTargetBpm() {
        return this.bpm;
    }

    public void setTargetBpm(int bpm) {
        this.bpm = bpm;
    }

    public double getTargetBpmAsDouble() {
        return this.bpm;
    }

    public void setTargetBpmAsDouble(double bpm) {
        this.bpm = (int) bpm;
    }

    public Rating getRating() {
        if (rating == null) {
            return Rating.BEGINNER;
        }
        return rating;
    }

    public String getAttachmentsAsString() {
        return createAttachmentString(collect(getAttachments(), on(ExerciseAttachment.class).getFilePath()));
    }

    public void setAttachmentsAsString(String content) {
        List<ExerciseAttachment> attachments = new ArrayList<>();

        for (String filePath : Splitter.on("\n").omitEmptyStrings().split(content)) {
            attachments.add(new ExerciseAttachment(filePath));
        }
        setAttachments(attachments);
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
