import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ExcerciseDefTest {

    @Test
    public void getTodaysExercises() throws Exception {

        ExerciseDefinition def = new ExerciseDefinition();
        def.createInstancesForEntireWeek();
        assertThat(def.getPlannedInstances().size(), is(7));
        assertThat(DateTimeComparator.getDateOnlyInstance().compare(def.getTodaysExercises().getDay(), DateTime.now()), is(0));

    }
}
