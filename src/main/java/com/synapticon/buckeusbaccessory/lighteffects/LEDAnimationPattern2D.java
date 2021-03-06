package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation D
 * <p/>
 * Back: All LEDs flash fast with random color, 2 times per second; Front: 2 LED
 * groups alternating flash; last 5s.
 */
public class LEDAnimationPattern2D extends LEDAnimation {

    private boolean rearFlash = true;

    private boolean frontIsLeft = true;

    public LEDAnimationPattern2D(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 500, 600);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        if (rearFlash) {
            for (int i = 0; i < 159; i += 3) {
                bytes[i] = (byte) 36;
                bytes[i + 1] = (byte) 148;
                bytes[i + 2] = (byte) 253;
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
            bytes[i] = (byte) 36;
            bytes[i + 1] = (byte) 148;
            bytes[i + 2] = (byte) 253;
        }

        frontIsLeft = !frontIsLeft; // alternate

        setFrontLEDBytes(bytes);
    }
}
