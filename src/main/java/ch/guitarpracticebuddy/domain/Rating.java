package ch.guitarpracticebuddy.domain;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/14/13
 * Time: 9:23 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Rating {

    BEGINNER(0),
    ADVANCING(1),
    GOOD(2),
    MASTER(3);

    private final int level;

    Rating(int i) {
        level = i;
    }

    public int getLevel() {
        return level;
    }

}
