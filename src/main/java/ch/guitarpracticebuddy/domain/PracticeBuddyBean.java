package ch.guitarpracticebuddy.domain;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

public class PracticeBuddyBean {

    private final EntityManager em;
    @Setter
    private List<Tag> tags;
    @Setter
    @Getter
    private List<PracticeWeek> practiceWeeks;
    private List<ExerciseDefinition> exerciseDefinitions;
    private static PracticeBuddyBean INSTANCE;

    public static void init(EntityManager em) {
        INSTANCE = new PracticeBuddyBean(em);
    }

    public static PracticeBuddyBean getInstance() {
        return INSTANCE;
    }

    public PracticeBuddyBean(EntityManager em) {
        this.em = em;
        init();
    }

    private static List<PracticeWeek> loadPracticePlans(EntityManager em) {
        return em.createQuery("SELECT p FROM PracticeWeek p ORDER BY p.dateFrom", PracticeWeek.class).getResultList();
    }

    private static List<ExerciseDefinition> loadExcerciseDefs(EntityManager em) {
        return em.createQuery("SELECT e FROM ExerciseDefinition e ORDER BY e.title", ExerciseDefinition.class).getResultList();
    }

    private void init() {
        this.practiceWeeks = loadPracticePlans(em);
        this.exerciseDefinitions = loadExcerciseDefs(em);
        this.tags = loadTags();
    }

    private List<Tag> loadTags() {
        return em.createQuery("SELECT t FROM Tag t ORDER BY t.name", Tag.class).getResultList();
    }

    public List<ExerciseDefinition> getExerciseDefinitions(List<Tag> selectedTags, List<Rating> selectedRatings) {

        List<ExerciseDefinition> result = new ArrayList<>();
        for (ExerciseDefinition exerciseDefinition : exerciseDefinitions) {
            if ((selectedTags.isEmpty() || exerciseDefinition.getTags().containsAll(selectedTags)) && (selectedRatings.isEmpty()
                    || selectedRatings.contains(exerciseDefinition.getRating()))) {
                result.add(exerciseDefinition);
            }
        }
        return result;

    }

    public PracticeWeek getPracticePlanForDay(LocalDate today) {
        for (PracticeWeek practiceWeek : practiceWeeks) {
            if (practiceWeek.isForDay(today)) {
                return practiceWeek;
            }
        }
        return null;
    }

    public void deleteExerciseDefinition(ExerciseDefinition exerciseDefinition) {
        this.exerciseDefinitions.remove(exerciseDefinition);
        for (PracticeWeek practiceWeek : practiceWeeks) {
            practiceWeek.deleteExerciseDef(exerciseDefinition);
        }
        this.em.remove(exerciseDefinition);

    }

    public List<ExerciseDefinition> getExcercisesForDay(LocalDate today) {
        if (getPracticePlanForDay(today) == null) {
            return Collections.emptyList();
        }

        return select(getPracticePlanForDay(today).getExerciseDefinitions(),
                having(on(ExerciseDefinition.class).isPlannedFor(today), equalTo(true)));
    }

    public void deletePracticeWeek(PracticeWeek practiceWeek) {
        practiceWeek.clearAllExerciseInstances();
        em.remove(practiceWeek);
        this.practiceWeeks.remove(practiceWeek);
    }

    public ExerciseDefinition createNewExerciseDefinition() {
        ExerciseDefinition exerciseDefinition = new ExerciseDefinition();
        em.persist(exerciseDefinition);
        this.exerciseDefinitions.add(exerciseDefinition);
        return exerciseDefinition;
    }

    public PracticeWeek createNewPracticePlan(LocalDate startDate, LocalDate endDate) {
        PracticeWeek practiceWeek = new PracticeWeek(startDate, endDate);
        em.persist(practiceWeek);
        this.practiceWeeks.add(practiceWeek);
        return practiceWeek;
    }

    public void deleteTag(Tag selectedValue) {
        this.tags.remove(selectedValue);
        for (ExerciseDefinition exerciseDefinition : exerciseDefinitions) {
            exerciseDefinition.removeTag(selectedValue);
        }
        em.remove(selectedValue);
    }

    public Tag addTag(String value) {
        if (!Strings.isNullOrEmpty(value)) {
            Tag tag = new Tag(value);
            this.tags.add(tag);
            this.em.persist(tag);
            return tag;

        }
        return null;

    }

    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }

}
