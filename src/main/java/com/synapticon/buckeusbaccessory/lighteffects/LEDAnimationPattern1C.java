package com.synapticon.buckeusbaccessory.lighteffects;

import java.util.Random;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation C
 *
 * Back: Filling out from the middle and change the color(random); Front: flash
 * quickly with the same color change, 2 times per second; last 5s.
 */
public class LEDAnimationPattern1C extends LEDAnimation {

    private final Random random;

    private byte[] rearBytes;
    private int rearCurrent = 0;

    private boolean frontFlash;

    public LEDAnimationPattern1C(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 100, 500);

        random = new Random();
        rearBytes = new byte[getRearLEDBytes().length];
    }

    @Override
    protected void animateRearLED() {
        if (rearCurrent == 27) {
            rearCurrent = 0;
            rearBytes = new byte[getRearLEDBytes().length];
        }

        byte r = (byte) (random.nextInt(255));
        byte g = (byte) (random.nextInt(25) + 51);
        byte b = (byte) (random.nextInt(5) + 238);

        int rearInd = (26 - rearCurrent) * 3;

        for (int i = 0; i < rearCurrent * 6 + 3; i += 3) {
            rearBytes[rearInd + i] = r;
            rearBytes[rearInd + i + 1] = g;
            rearBytes[rearInd + i + 2] = b;
        }

        rearCurrent++;

        setRearLEDBytes(rearBytes);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];
        if (frontFlash) {
            int r = random.nextInt(254);
            int g = (random.nextInt(10) + 233);
            int b = random.nextInt(16);
            for (int i = 0; i < getFrontLEDBytes().length; i += 3) {
                bytes[i] = (byte) r;
                bytes[i + 1] = (byte) g;
                bytes[i + 2] = (byte) b;
            }
        }

        frontFlash = !frontFlash;
        setFrontLEDBytes(bytes);
    }
}
