package com.semanticsquare.thrillio.constants;

public enum Gender {
    MALE(0),
    FEMALE(1),
    TRANSGENDER(2);

    public int getI() {
        return value;
    }

    private int value;

    private Gender(int value) {
        this.value = value;
    }

}
