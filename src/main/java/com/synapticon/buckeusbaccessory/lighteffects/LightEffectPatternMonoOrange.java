package com.synapticon.buckeusbaccessory.lighteffects;

public class LightEffectPatternMonoOrange implements LightEffectPattern {

    LEDAnimation firstLEDAnimation;

    public LightEffectPatternMonoOrange(LEDUpdater ledUpdater) {
        LEDAnimation animation1 = new LEDAnimationPattern3A(ledUpdater, 5000);
        LEDAnimation animation2 = new LEDAnimationPattern3B(ledUpdater, 4000);
        LEDAnimation animation3 = new LEDAnimationPattern3C(ledUpdater, 6000);
        LEDAnimation animation4 = new LEDAnimationPattern3D(ledUpdater, 5000);

        animation1.setNext(animation2);
        animation2.setNext(animation3);
        animation3.setNext(animation4);
        animation4.setNext(animation1);

        firstLEDAnimation = animation1;
    }

    @Override
    public void start() {
        firstLEDAnimation.start(); // start from the first LED animation and follow the chain
    }

    @Override
    public void stop() {
        firstLEDAnimation.stop();
    }

    @Override
    public LightEffectPatternType getType() {
        return LightEffectPatternType.MONO_ORANGE;
    }

    @Override
    public String getName() {
        return "Mono-Orange";
    }

    @Override
    public byte[] getPattern() {
        return new byte[0];
    }

    @Override
    public String getDescription() {
        return "Light Effects Standard Patterns Mono-Orange Description";
    }
}
