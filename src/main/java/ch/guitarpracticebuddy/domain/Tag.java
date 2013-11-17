package ch.guitarpracticebuddy.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@Getter
public class Tag {

    @Transient
    private StringProperty nameProperty;

    @Column(unique = true, nullable = false)
    private String name;

    public Tag(String value) {
        name = value;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        nameProperty().set(name);
    }

    public StringProperty nameProperty() {
        if (this.nameProperty == null) {
            this.nameProperty = new SimpleStringProperty(this, "title") {
                @Override
                public void set(String s) {
                    super.set(s);
                    name = s;
                }
            };
            this.nameProperty.set(getName());
        }
        return nameProperty;
    }
}
