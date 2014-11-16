package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation A
 *
 * Back: 7 LEDs block scroll left to right than right to left; Front: Single LED
 * scroll back and forth fast; last 5s.
 */
public class LEDAnimationPattern2A extends LEDAnimation {

    private int rearPosition = 0;
    private boolean rearDirection;

    private int frontLeftPosition;
    private boolean frontLeftDirection;
    private int frontRightPosition;
    private boolean frontRightDirection;

    public LEDAnimationPattern2A(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 30, 60);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        rearDirection = rearPosition == 138 || rearPosition == 0 ? !rearDirection : rearDirection;

        for (int i = rearPosition; i < rearPosition + 21; i += 3) {
            bytes[0 + i] = (byte) 36;
            bytes[1 + i] = (byte) 148;
            bytes[2 + i] = (byte) 253;
        }

        rearPosition += rearDirection ? 3 : -3;

        setRearLEDBytes(bytes);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];

        bytes[0 + frontLeftPosition] = (byte) 255;
        bytes[1 + frontLeftPosition] = (byte) 204;
        bytes[2 + frontLeftPosition] = (byte) 0;

        bytes[33 + frontRightPosition] = (byte) 255;
        bytes[34 + frontRightPosition] = (byte) 204;
        bytes[35 + frontRightPosition] = (byte) 0;

        frontLeftDirection = frontLeftPosition == 30 || frontLeftPosition == 0 ? !frontLeftDirection : frontLeftDirection;
        frontLeftPosition += frontLeftDirection ? 3 : -3;

        frontRightDirection = frontRightPosition == 30 || frontRightPosition == 0 ? !frontRightDirection : frontRightDirection;
        frontRightPosition += frontRightDirection ? 3 : -3;

        setFrontLEDBytes(bytes);
    }
}
