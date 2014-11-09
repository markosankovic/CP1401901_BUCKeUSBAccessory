package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation D
 *
 * Back: All LEDs flash fast with random color, 2 times per second; Front: 2 LED
 * groups alternating flash; last 5s.
 */
public class LEDAnimationPattern2D extends LEDAnimation {

    public LEDAnimationPattern2D(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 1000);
    }

    @Override
    protected void animateFrontLED() {
    }

    @Override
    protected void animateRearLED() {
    }
}
