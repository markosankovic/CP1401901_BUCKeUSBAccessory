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
    private int rearLeftPosition = 77;
    private int rearRightPosition = 78;

    private boolean frontFlash;

    public LEDAnimationPattern1C(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 200, 500);

        random = new Random();
        rearBytes = new byte[getRearLEDBytes().length];
    }

    @Override
    protected void animateRearLED() {

        int r = random.nextInt(255);
        int g = random.nextInt(25) + 51;
        int b = random.nextInt(5) + 238;

        rearBytes[rearLeftPosition] = (byte) b;
        rearBytes[rearLeftPosition - 1] = (byte) g;
        rearBytes[rearLeftPosition - 2] = (byte) r;

        rearBytes[rearRightPosition] = (byte) r;
        rearBytes[rearRightPosition + 1] = (byte) g;
        rearBytes[rearRightPosition + 2] = (byte) b;

        rearLeftPosition -= 3;
        rearRightPosition += 3;

        setRearLEDBytes(rearBytes);

        if (rearLeftPosition == 2) {
            rearLeftPosition = 77;
            rearRightPosition = 78;
            rearBytes = new byte[getRearLEDBytes().length];
        }
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
