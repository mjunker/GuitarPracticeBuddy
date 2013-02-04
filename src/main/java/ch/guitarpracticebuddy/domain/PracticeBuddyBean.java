package ch.guitarpracticebuddy.domain;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

public class PracticeBuddyBean {

    @Setter
    @Getter
    private List<PracticeWeek> practiceWeeks;

    private List<ExerciseDefinition> exerciseDefinitions;
    private final EntityManager em;

    public PracticeBuddyBean(EntityManager em) {
        this.em = em;
        init();
    }

    private void init() {
        this.practiceWeeks = loadPracticePlans(em);
        this.exerciseDefinitions = loadExcerciseDefs(em);
    }

    private static List<PracticeWeek> loadPracticePlans(EntityManager em) {
        return em.createQuery("SELECT p FROM PracticeWeek p", PracticeWeek.class).getResultList();
    }

    private static List<ExerciseDefinition> loadExcerciseDefs(EntityManager em) {
        return em.createQuery("SELECT e FROM ExerciseDefinition e", ExerciseDefinition.class).getResultList();
    }


    public List<ExerciseDefinition> getExerciseDefinitions() {
        return exerciseDefinitions;
    }

    public PracticeWeek getPracticePlanForToday() {
        for (PracticeWeek practiceWeek : practiceWeeks) {
            if (practiceWeek.isForToday()) {
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

    public List<ExerciseDefinition> getExcercisesForToday() {
        if (getPracticePlanForToday() == null) {
            return Collections.emptyList();
        }

        return select(getPracticePlanForToday().getExerciseDefinitions(),
                having(on(ExerciseDefinition.class).isPlannedForToday(), equalTo(true)));
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
}
