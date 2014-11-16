package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation A
 *
 * Back: All LEDs flash slow, 1 time per second; Front: All LEDs flash slow, 1
 * time per second; last 4s.
 */
public class LEDAnimationPattern1A extends LEDAnimation {

    private boolean rearFlash = true;
    private boolean frontFlash = true;

    public LEDAnimationPattern1A(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 1000);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];
        if (rearFlash) {
            for (int i = 0; i < 51; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }

            for (int i = 51; i < 108; i += 3) {
                bytes[i] = (byte) 225;
                bytes[i + 1] = (byte) 17;
                bytes[i + 2] = (byte) 22;
            }

            for (int i = 108; i < 159; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }
        }
        rearFlash = !rearFlash;
        setRearLEDBytes(bytes);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];
        if (frontFlash) {
            for (int i = 0; i < getFrontLEDBytes().length; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }
        }
        frontFlash = !frontFlash;
        setFrontLEDBytes(bytes);
    }
}
