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
    
    private boolean flashFrontLED;

    private byte[] rearBytes;
    private int leftPosition = 77;
    private int rightPosition = 78;

    public LEDAnimationPattern1C(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 500, 200);

        random = new Random();
        rearBytes = new byte[getRearLEDBytes().length];
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];
        if (flashFrontLED) {
            int r = random.nextInt(254);
            int g = (random.nextInt(10) + 233);
            int b = random.nextInt(16);
            for (int i = 0; i < getFrontLEDBytes().length; i += 3) {
                bytes[i] = (byte) r;
                bytes[i + 1] = (byte) g;
                bytes[i + 2] = (byte) b;
            }
        }

        flashFrontLED = !flashFrontLED;
        setFrontLEDBytes(bytes);
    }

    @Override
    protected void animateRearLED() {
        rearBytes[leftPosition] = (byte) (random.nextInt(5) + 238);
        rearBytes[leftPosition - 1] = (byte) (random.nextInt(25) + 51);
        rearBytes[leftPosition - 2] = (byte) random.nextInt(255);

        rearBytes[rightPosition] = (byte) random.nextInt(255);
        rearBytes[rightPosition + 1] = (byte) (random.nextInt(25) + 51);
        rearBytes[rightPosition + 2] = (byte) (random.nextInt(5) + 238);

        leftPosition -= 3;
        rightPosition += 3;

        setRearLEDBytes(rearBytes);

        if (leftPosition == 2) {
            leftPosition = 77;
            rightPosition = 78;
            rearBytes = new byte[getRearLEDBytes().length];
        }
    }
}
