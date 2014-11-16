package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation B
 *
 * Back: Even and odd blocks alternately flash; Front: Flash like a
 * Equalizer(from middle to both side); last 4s.
 */
public class LEDAnimationPattern2B extends LEDAnimation {

    private boolean rearEven = true;

    public LEDAnimationPattern2B(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 500, 1000);
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
    }
}
