package ch.guitarpracticebuddy.javafx;

public enum StyleClass {

    DONE("done"),
    SKIP("skip"),
    PLANNED("planned"),
    PRACTICE_LIST("practiceList");

    private final String styleClassName;

    StyleClass(String styleClassName) {
        this.styleClassName = styleClassName;
    }

    public String getStyle() {
        return styleClassName;
    }
}
