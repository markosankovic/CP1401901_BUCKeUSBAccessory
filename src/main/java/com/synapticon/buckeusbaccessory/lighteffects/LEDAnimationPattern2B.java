package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation B
 *
 * Back: Even and odd blocks alternately flash; Front: Flash like a
 * Equalizer(from middle to both side); last 4s.
 */
public class LEDAnimationPattern2B extends LEDAnimation {

    public LEDAnimationPattern2B(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 1000);
    }

    @Override
    protected void animateFrontLED() {
    }

    @Override
    protected void animateRearLED() {
    }
}
