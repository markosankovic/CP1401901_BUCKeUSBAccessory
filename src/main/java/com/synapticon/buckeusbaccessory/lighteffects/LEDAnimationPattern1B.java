package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation B
 *
 * Back: Single block scroll back and forth slowly; Front: Even and odd blocks
 * alternately flash; last 6s.
 */
public class LEDAnimationPattern1B extends LEDAnimation {

    private boolean even = true;

    private int lightPosition;
    private boolean lightDirection;

    private int brakePosition;
    private boolean brakeDirection;

    public LEDAnimationPattern1B(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 200);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];

        for (int i = even ? 0 : 3; i < bytes.length; i += 6) {
            bytes[i] = (byte) 255;
            bytes[i + 1] = (byte) 204;
            bytes[i + 2] = (byte) 0;
        }

        even = !even;
        setFrontLEDBytes(bytes);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        bytes[0 + lightPosition] = (byte) 255;
        bytes[1 + lightPosition] = (byte) 204;
        bytes[2 + lightPosition] = (byte) 0;

        bytes[51 + brakePosition] = (byte) 225;
        bytes[52 + brakePosition] = (byte) 17;
        bytes[53 + brakePosition] = (byte) 22;

        bytes[108 + lightPosition] = (byte) 255;
        bytes[109 + lightPosition] = (byte) 204;
        bytes[110 + lightPosition] = (byte) 0;

        lightDirection = lightPosition == 42 || lightPosition == 0 ? !lightDirection : lightDirection;
        lightPosition += lightDirection ? 3 : -3;

        brakeDirection = brakePosition == 48 || brakePosition == 0 ? !brakeDirection : brakeDirection;
        brakePosition += brakeDirection ? 3 : -3;

        setRearLEDBytes(bytes);
    }
}
