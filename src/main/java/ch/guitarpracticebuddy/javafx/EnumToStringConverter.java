package ch.guitarpracticebuddy.javafx;

import javafx.util.StringConverter;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/19/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
class EnumToStringConverter<T extends Enum<T>> extends StringConverter<T> {

    private final Class<T> enumType;

    EnumToStringConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public String toString(T rating) {
        return Texts.getText(rating);
    }

    @Override
    public T fromString(String s) {
        return Texts.fromText(s, enumType);
    }
}
