package com.synapticon.buckeusbaccessory.lighteffects;

public interface LightEffectPattern {

    /**
     * Starts the light effect pattern animations.
     */
    void start();

    /**
     * Stops the light effect pattern animations.
     */
    void stop();

    /**
     * Type is saved as String in SharedPreferences.
     * <p/>
     * Use {@link LightEffectPatternFactory} to get an instance of LightEffect by type.
     *
     * @return Type of this light effect.
     */
    LightEffectPatternType getType();

    /**
     * @return Name for light effects list view.
     */
    String getName();

    /**
     * @return Pattern for the On-Board Controller.
     */
    byte[] getPattern();

    /**
     * @return Description for light effects list view.
     */
    String getDescription();
}
