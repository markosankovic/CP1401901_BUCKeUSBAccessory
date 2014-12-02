package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 3 / LED Animation C
 * <p/>
 * Back: 3 LED groups alternating flash with random color; Front: All LEDs flash
 * slow, 1 time per second; last 6s.
 */
public class LEDAnimationPattern3C extends LEDAnimation {

    private boolean frontFlash = true;

    private int rearFlashGroup = 0;

    public LEDAnimationPattern3C(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 500, 1000);
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        if (rearFlashGroup == 0) {
            for (int i = 0; i < 51; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }
        } else if (rearFlashGroup == 1) {
            for (int i = 51; i < 108; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }
        } else if (rearFlashGroup == 2) {
            for (int i = 108; i < 159; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }
        }

        rearFlashGroup = rearFlashGroup == 2 ? 0 : rearFlashGroup + 1;

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
