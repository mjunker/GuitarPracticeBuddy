package ch.guitarpracticebuddy.domain;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/14/13
 * Time: 9:23 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Rating {

    PERFECT(3), GOOD(2), ADVANCING(1), HARD(0);

    private final int level;

    Rating(int i) {
        level = i;
    }

    public int getLevel() {
        return level;
    }

    public static Rating fromValue(int value) {
        for (Rating rating : Rating.values()) {
            if (rating.getLevel() == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("No Rating found for value=" + value);
    }
}
