package com.synapticon.buckeusbaccessory.lighteffects;

import java.util.Random;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation D
 *
 * Back: Flash like a Equalizer(from middle to both side); Front: 2 LED groups
 * alternating flash; last 5s.
 */
public class LEDAnimationPattern1D extends LEDAnimation {

    private final Random random;

    private final byte[] rearBytes;
    private int rearTarget = 0;
    private int rearCurrent = 0;
    private boolean rearDirection;

    private boolean frontIsLeft = true;

    public LEDAnimationPattern1D(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 15, 500);

        random = new Random();
        rearBytes = new byte[getRearLEDBytes().length];
    }

    @Override
    protected void animateRearLED() {
        if (rearCurrent == rearTarget) {
            rearTarget = random.nextInt(26);
            rearDirection = rearCurrent < rearTarget;
        }

        int rearLeftInd = (25 - rearCurrent) * 3;
        int rearRightInd = (26 + rearCurrent) * 3;

        rearBytes[rearLeftInd] = rearDirection ? (byte) 36 : 0;
        rearBytes[rearLeftInd + 1] = rearDirection ? (byte) 148 : 0;
        rearBytes[rearLeftInd + 2] = rearDirection ? (byte) 253 : 0;

        rearBytes[rearRightInd] = rearDirection ? (byte) 36 : 0;
        rearBytes[rearRightInd + 1] = rearDirection ? (byte) 148 : 0;
        rearBytes[rearRightInd + 2] = rearDirection ? (byte) 253 : 0;

        if (rearCurrent != rearTarget) { // case when random is the same as previous, prevents IndexOutOfBounds
            rearCurrent += rearDirection ? 1 : -1;
        }
        setRearLEDBytes(rearBytes);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];

        int m = getFrontLEDBytes().length / 2;
        int i = frontIsLeft ? 0 : m;
        int j = frontIsLeft ? m : getFrontLEDBytes().length;

        for (; i < j; i += 3) {
            bytes[i] = (byte) 247;
            bytes[i + 1] = (byte) 0;
            bytes[i + 2] = (byte) 0;
        }

        frontIsLeft = !frontIsLeft; // alternate

        setFrontLEDBytes(bytes);
    }
}
