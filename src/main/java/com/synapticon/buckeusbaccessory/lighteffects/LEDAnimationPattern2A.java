package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation A
 *
 * Back: 7 LEDs block scroll left to right than right to left; Front: Single LED
 * scroll back and forth fast; last 5s.
 */
public class LEDAnimationPattern2A extends LEDAnimation {

    private int leftPosition;
    private boolean leftDirection;
    private int rightPosition;
    private boolean rightDirection;

    private int position = 0;
    private boolean direction;

    public LEDAnimationPattern2A(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 60, 30);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];

        bytes[0 + leftPosition] = (byte) 255;
        bytes[1 + leftPosition] = (byte) 204;
        bytes[2 + leftPosition] = (byte) 0;

        bytes[33 + rightPosition] = (byte) 255;
        bytes[34 + rightPosition] = (byte) 204;
        bytes[35 + rightPosition] = (byte) 0;

        leftDirection = leftPosition == 30 || leftPosition == 0 ? !leftDirection : leftDirection;
        System.out.println(leftDirection);
        leftPosition += leftDirection ? 3 : -3;

        rightDirection = rightPosition == 30 || rightPosition == 0 ? !rightDirection : rightDirection;
        rightPosition += rightDirection ? 3 : -3;

        setFrontLEDBytes(bytes);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        direction = position == 138 || position == 0 ? !direction : direction;

        for (int i = position; i < position + 21; i += 3) {
            bytes[0 + i] = (byte) 36;
            bytes[1 + i] = (byte) 148;
            bytes[2 + i] = (byte) 253;
        }

        position += direction ? 3 : -3;

        setRearLEDBytes(bytes);
    }
}
