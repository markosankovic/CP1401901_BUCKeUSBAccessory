package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation B
 *
 * Back: Single block scroll back and forth slowly; Front: Even and odd blocks
 * alternately flash; last 6s.
 */
public class LEDAnimationPattern1B extends LEDAnimation {

    private int rearLightPosition;
    private boolean rearLightDirection;
    private int rearBrakePosition;
    private boolean rearBrakeDirection;

    private boolean frontEven = true;

    public LEDAnimationPattern1B(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 50, 500);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        bytes[0 + rearLightPosition] = (byte) 255;
        bytes[1 + rearLightPosition] = (byte) 204;
        bytes[2 + rearLightPosition] = (byte) 0;

        bytes[51 + rearBrakePosition] = (byte) 225;
        bytes[52 + rearBrakePosition] = (byte) 17;
        bytes[53 + rearBrakePosition] = (byte) 22;

        bytes[108 + rearLightPosition] = (byte) 255;
        bytes[109 + rearLightPosition] = (byte) 204;
        bytes[110 + rearLightPosition] = (byte) 0;

        rearLightDirection = rearLightPosition == 48 || rearLightPosition == 0 ? !rearLightDirection : rearLightDirection;
        rearLightPosition += rearLightDirection ? 3 : -3;

        rearBrakeDirection = rearBrakePosition == 54 || rearBrakePosition == 0 ? !rearBrakeDirection : rearBrakeDirection;
        rearBrakePosition += rearBrakeDirection ? 3 : -3;

        setRearLEDBytes(bytes);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];

        for (int i = frontEven ? 0 : 3; i < bytes.length; i += 6) {
            bytes[i] = (byte) 255;
            bytes[i + 1] = (byte) 204;
            bytes[i + 2] = (byte) 0;
        }

        frontEven = !frontEven;
        setFrontLEDBytes(bytes);
    }
}
