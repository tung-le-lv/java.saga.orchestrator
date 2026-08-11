package com.openmind.shared.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class for "smart enums": type-safe enumerations with behavior, following the same
 * pattern as the original .NET {@code Enumeration} base class. Concrete subclasses expose
 * their values as {@code public static final} fields.
 */
public abstract class Enumeration implements Comparable<Enumeration> {

    private int id;
    private String name;

    // Required for MongoDB deserialization (no-arg constructor + protected setters below)
    protected Enumeration() {
        this.name = "";
    }

    protected Enumeration(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static <T extends Enumeration> List<T> getAll(Class<T> type) {
        List<T> values = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && type.isAssignableFrom(field.getType())) {
                try {
                    values.add(type.cast(field.get(null)));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Unable to read enumeration field " + field.getName() + " on " + type, e);
                }
            }
        }
        return values;
    }

    public static <T extends Enumeration> T fromValue(Class<T> type, int value) {
        return getAll(type).stream()
                .filter(item -> item.getId() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'" + value + "' is not a valid value in " + type));
    }

    public static <T extends Enumeration> T fromDisplayName(Class<T> type, String displayName) {
        return getAll(type).stream()
                .filter(item -> item.getName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'" + displayName + "' is not a valid display name in " + type));
    }

    @Override
    public int compareTo(Enumeration other) {
        return Integer.compare(this.id, other == null ? 0 : other.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Enumeration other)) {
            return false;
        }
        return getClass() == obj.getClass() && this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
