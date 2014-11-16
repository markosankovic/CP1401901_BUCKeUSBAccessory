package com.synapticon.buckeusbaccessory.lighteffects;

public enum LightEffectPatternType {

    DIAMOND("DIAMOND"), RUBY("RUBY");

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
