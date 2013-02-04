import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ExcerciseDefTest {

    @Test
    public void getTodaysExercises() throws Exception {

        PracticeWeek practiceWeek = new PracticeWeek(DateTime.now().toLocalDate(), DateTime.now().plusDays(6).toLocalDate());
        ExerciseDefinition def = new ExerciseDefinition();
        practiceWeek.activate(def);
        assertThat(def.getPlannedInstances().size(), is(7));
        assertThat(DateTimeComparator.getDateOnlyInstance().compare(def.getTodaysExercises().getDay(), DateTime.now()), is(0));

    }
}
