package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation C
 *
 * Back: 3 LED groups alternating flash with random color; Front: All LEDs flash
 * slow, 1 time per second; last 6s.
 */
public class LEDAnimationPattern2C extends LEDAnimation {

    public LEDAnimationPattern2C(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 1000);
    }

    @Override
    protected void animateFrontLED() {
    }

    @Override
    protected void animateRearLED() {
    }
}
