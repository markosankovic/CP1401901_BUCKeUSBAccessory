package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation D
 *
 * Back: Flash like a Equalizer(from middle to both side); Front: 2 LED groups
 * alternating flash; last 5s.
 */
public class LEDAnimationPattern1D extends LEDAnimation {

    private boolean isLeft = true;

    private byte[] rearBytes;
    private int leftPosition = 77;
    private int rightPosition = 78;

    public LEDAnimationPattern1D(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 800, 60);

        rearBytes = new byte[getRearLEDBytes().length];
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];

        int m = getFrontLEDBytes().length / 2;
        int i = isLeft ? 0 : m;
        int j = isLeft ? m : getFrontLEDBytes().length;

        for (; i < j; i += 3) {
            bytes[i] = (byte) 247;
            bytes[i + 1] = (byte) 0;
            bytes[i + 2] = (byte) 0;
        }

        isLeft = !isLeft; // alternate

        setFrontLEDBytes(bytes);
    }

    // TODO: Flash like a Equalizer(from middle to both side) 
    @Override
    protected void animateRearLED() {
        rearBytes[leftPosition] = (byte) 253;
        rearBytes[leftPosition - 1] = (byte) 148;
        rearBytes[leftPosition - 2] = (byte) 36;

        rearBytes[rightPosition] = (byte) 36;
        rearBytes[rightPosition + 1] = (byte) 148;
        rearBytes[rightPosition + 2] = (byte) 253;

        leftPosition -= 3;
        rightPosition += 3;

        setRearLEDBytes(rearBytes);

        if (leftPosition == -1) {
            leftPosition = 77;
            rightPosition = 78;
            rearBytes = new byte[getRearLEDBytes().length];
        }
    }
}
