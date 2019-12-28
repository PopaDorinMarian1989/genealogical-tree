package org.fasttrackit.genealogicaltree.core;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;


@Value
@AllArgsConstructor
public final class Person {
    @NonNull
    String id;
    @NonNull
    String name;
    int age;
    boolean isGenderMale;
    private boolean genderMale;

    public Person(String id, String name, String age, String isGenderMale) {
        this(id, name, Integer.parseInt(age), Boolean.parseBoolean(isGenderMale));
    }

    public Person(String id, String name, int parseInt, boolean parseBoolean) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isGenderMale() {
        return genderMale;
    }

    public void setGenderMale(boolean genderMale) {
        this.genderMale = genderMale;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}