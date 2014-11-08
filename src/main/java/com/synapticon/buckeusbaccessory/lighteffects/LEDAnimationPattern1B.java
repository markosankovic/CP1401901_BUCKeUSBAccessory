package com.synapticon.buckeusbaccessory.lighteffects;

public class LEDAnimationPattern1B extends LEDAnimation {

    public LEDAnimationPattern1B(int duration, LEDUpdater ledUpdater) {
        super(ledUpdater, duration, 1000, 500);
    }

    @Override
    protected void animateFrontLED() {
        System.out.println("LEDAnimationPattern1B#animateFrontLEDs");
    }

    @Override
    protected void animateRearLED() {
        System.out.println("LEDAnimationPattern1B#animateRearLEDs");
    }
} 
