package com.synapticon.buckeusbaccessory.lighteffects;

public class LightEffectPatternMonoBlue implements LightEffectPattern {

    LEDAnimation firstLEDAnimation;

    public LightEffectPatternMonoBlue(LEDUpdater ledUpdater) {
        LEDAnimation animation1 = new LEDAnimationPattern2A(ledUpdater, 5000);
        LEDAnimation animation2 = new LEDAnimationPattern2B(ledUpdater, 4000);
        LEDAnimation animation3 = new LEDAnimationPattern2C(ledUpdater, 6000);
        LEDAnimation animation4 = new LEDAnimationPattern2D(ledUpdater, 5000);

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
        return LightEffectPatternType.MONO_BLUE;
    }

    @Override
    public String getName() {
        return "Mono-Blue";
    }

    @Override
    public byte[] getPattern() {
        return new byte[0];
    }

    @Override
    public String getDescription() {
        return "Light Effects Standard Patterns Mono-Blue Description";
    }
}
