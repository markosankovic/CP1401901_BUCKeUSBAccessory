package com.synapticon.buckeusbaccessory.lighteffects;

import java.util.Random;

/**
 * LIGHT EFFECTS - PATTERN 3 / LED Animation B
 * <p/>
 * Back: Even and odd blocks alternately flash; Front: Flash like a
 * Equalizer(from middle to both side); last 4s.
 */
public class LEDAnimationPattern3B extends LEDAnimation {

    private final Random random;

    private boolean rearEven = true;

    private final byte[] frontBytes;
    private int frontTarget;
    private int frontCurrent;
    private boolean frontDirection;

    public LEDAnimationPattern3B(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 500, 15);

        random = new Random();
        frontBytes = new byte[getFrontLEDBytes().length];
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        for (int i = rearEven ? 0 : 3; i < bytes.length; i += 6) {
            bytes[i] = (byte) 255;
            bytes[i + 1] = (byte) 204;
            bytes[i + 2] = (byte) 0;
        }

        rearEven = !rearEven;
        setRearLEDBytes(bytes);
    }

    @Override
    protected void animateFrontLED() {
        if (frontCurrent == frontTarget) {
            frontTarget = random.nextInt(11);
            frontDirection = frontCurrent < frontTarget;
        }

        int rearLeftInd = (10 - frontCurrent) * 3;
        int rearRightInd = (11 + frontCurrent) * 3;

        frontBytes[rearLeftInd] = frontDirection ? (byte) 255 : 0;
        frontBytes[rearLeftInd + 1] = frontDirection ? (byte) 204 : 0;
        frontBytes[rearLeftInd + 2] = frontDirection ? (byte) 0 : 0;

        frontBytes[rearRightInd] = frontDirection ? (byte) 255 : 0;
        frontBytes[rearRightInd + 1] = frontDirection ? (byte) 204 : 0;
        frontBytes[rearRightInd + 2] = frontDirection ? (byte) 0 : 0;

        if (frontCurrent != frontTarget) { // case when random is the same as previous, prevents IndexOutOfBounds
            frontCurrent += frontDirection ? 1 : -1;
        }
        setFrontLEDBytes(frontBytes);
    }
}