package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseStatus;
import ch.guitarpracticebuddy.domain.Rating;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/19/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Texts {

    private static Map<Enum, String> map = new HashMap<>();

    static {
        map.put(ExerciseStatus.DONE, "Done");
        map.put(ExerciseStatus.PLANNED, "Planned");
        map.put(ExerciseStatus.SKIPPED, "Skipped");
        map.put(Rating.BEGINNER, "Rookie");
        map.put(Rating.ADVANCING, "Intermediate");
        map.put(Rating.GOOD, "Experienced");
        map.put(Rating.MASTER, "Master");
    }

    public static <T extends Enum<T>> String getText(Enum<T> enumValue) {
        return map.get(enumValue);
    }

    public static <T extends Enum> T fromText(String text, Class<T> enumClass) {
        for (Map.Entry<Enum, String> enumStringEntry : map.entrySet()) {
            if (enumStringEntry.getKey().getClass().equals(enumClass)
                    && enumStringEntry.getValue().equals(text)) {
                return (T) enumStringEntry.getKey();
            }
        }
        return null;
    }
}
