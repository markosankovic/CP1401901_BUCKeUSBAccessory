package com.synapticon.buckeusbaccessory.lighteffects;

public enum LightEffectPatternType {

    CHRISTMAS("CHRISTMAS"), MONO_BLUE("MONO_BLUE"), MONO_ORANGE("MONO_ORANGE");

    private final String type;

    private LightEffectPatternType(String type) {
        this.type = type;
    }

    public boolean equalsType(String otherType) {
        return (otherType == null) ? false : type.equals(otherType);
    }

    public String toString() {
        return type;
    }
}
