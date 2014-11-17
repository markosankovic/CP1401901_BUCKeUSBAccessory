package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation D
 *
 * Back: Flash like a Equalizer(from middle to both side); Front: 2 LED groups
 * alternating flash; last 5s.
 */
public class LEDAnimationPattern1D extends LEDAnimation {

    private byte[] rearBytes;
    private int rearLeftPosition = 77;
    private int rearRightPosition = 78;

    private boolean frontIsLeft = true;

    public LEDAnimationPattern1D(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 50, 500);

        rearBytes = new byte[getRearLEDBytes().length];
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

    // TODO: Flash like a Equalizer(from middle to both side) 
    @Override
    protected void animateRearLED() {
        rearBytes[rearLeftPosition] = (byte) 253;
        rearBytes[rearLeftPosition - 1] = (byte) 148;
        rearBytes[rearLeftPosition - 2] = (byte) 36;

        rearBytes[rearRightPosition] = (byte) 36;
        rearBytes[rearRightPosition + 1] = (byte) 148;
        rearBytes[rearRightPosition + 2] = (byte) 253;

        rearLeftPosition -= 3;
        rearRightPosition += 3;

        setRearLEDBytes(rearBytes);

        if (rearLeftPosition == -1) {
            rearLeftPosition = 77;
            rearRightPosition = 78;
            rearBytes = new byte[getRearLEDBytes().length];
        }
    }
}
