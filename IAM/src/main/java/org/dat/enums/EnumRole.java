package org.dat.enums;

public enum EnumRole {
    ADMIN("ADMIN"),
    USER("USER");
    private String description;

    EnumRole(String description) {
        this.description = description;
    }
}
