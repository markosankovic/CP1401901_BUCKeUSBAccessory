package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation A
 *
 * Back: 7 LEDs block scroll left to right than right to left; Front: Single LED
 * scroll back and forth fast; last 5s.
 */
public class LEDAnimationPattern2A extends LEDAnimation {

    public LEDAnimationPattern2A(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 1000);
    }

    @Override
    protected void animateFrontLED() {
    }

    @Override
    protected void animateRearLED() {
    }
}
