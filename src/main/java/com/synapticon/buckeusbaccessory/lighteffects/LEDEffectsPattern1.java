package com.synapticon.buckeusbaccessory.lighteffects;

public class LEDEffectsPattern1 {

    LEDAnimation firstLEDAnimation;

    public LEDEffectsPattern1(LEDUpdater ledUpdater) {
        LEDAnimation animation1 = new LEDAnimationPattern1A(4000, ledUpdater);
        animation1.setNext(animation1);
        firstLEDAnimation = animation1;
    }

    public void start() {
        firstLEDAnimation.start(); // start from the first LED animation and follow the chain
    }

    public void stop() {
        firstLEDAnimation.stop();
    }
}
