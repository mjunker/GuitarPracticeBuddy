package ch.guitarpracticebuddy.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Tag {

    @Column(unique = true, nullable = false)
    private String name;

    public Tag(String value) {
        name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
