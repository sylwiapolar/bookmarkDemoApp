package com.semanticsquare.thrillio.constants;

public enum KidFriendlyStatus {
    APPROVED("approved"),
    REJECTED("rejected"),
    UNKNOWN("unknown");

    private KidFriendlyStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;
}
