package com.synapticon.buckeusbaccessory.lighteffects;

public class LEDEffectsPattern1 {

    LEDAnimation firstLEDAnimation;

    public LEDEffectsPattern1(LEDUpdater ledUpdater) {
        LEDAnimation animation1 = new LEDAnimationPattern1A(ledUpdater, 4000);
        LEDAnimation animation2 = new LEDAnimationPattern1B(ledUpdater, 6000);
        LEDAnimation animation3 = new LEDAnimationPattern1C(ledUpdater, 5000);

        animation1.setNext(animation2);
        animation2.setNext(animation3);
        animation3.setNext(animation1);

        firstLEDAnimation = animation1;
    }

    public void start() {
        firstLEDAnimation.start(); // start from the first LED animation and follow the chain
    }

    public void stop() {
        firstLEDAnimation.stop();
    }
}
