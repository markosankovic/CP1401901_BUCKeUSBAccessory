package com.synapticon.buckeusbaccessory.lighteffects;

import java.util.Random;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation D
 *
 * Back: All LEDs flash fast with random color, 2 times per second; Front: 2 LED
 * groups alternating flash; last 5s.
 */
public class LEDAnimationPattern2D extends LEDAnimation {

    private final Random random;

    private boolean rearFlash = true;
    private final byte[][] rearColors;
    private int rearPrevColorInd = -1;

    private boolean frontIsLeft = true;

    public LEDAnimationPattern2D(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 500, 500);

        random = new Random();
        this.rearColors = new byte[][]{
            {(byte) 255, 76, (byte) 238},
            {0, 51, (byte) 243},
            {0, (byte) 243, 16},
            {(byte) 254, (byte) 233, 0}
        };
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        if (rearFlash) {
            int ind = random.nextInt(4);
            while (ind == rearPrevColorInd) {
                ind = random.nextInt(4);
            }
            byte[] color = rearColors[rearPrevColorInd = ind];

            for (int i = 0; i < 159; i += 3) {
                bytes[i] = color[0];
                bytes[i + 1] = color[1];
                bytes[i + 2] = color[2];
            }
        }
        rearFlash = !rearFlash;
        setRearLEDBytes(bytes);
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
