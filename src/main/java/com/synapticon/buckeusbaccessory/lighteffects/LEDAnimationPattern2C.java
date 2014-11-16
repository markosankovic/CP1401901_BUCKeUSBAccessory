package com.synapticon.buckeusbaccessory.lighteffects;

import java.util.Random;

/**
 * LIGHT EFFECTS - PATTERN 2 / LED Animation C
 *
 * Back: 3 LED groups alternating flash with random color; Front: All LEDs flash
 * slow, 1 time per second; last 6s.
 */
public class LEDAnimationPattern2C extends LEDAnimation {

    private final Random random;
    
    private boolean frontFlash = true;

    private int rearFlashGroup = 0;

    public LEDAnimationPattern2C(LEDUpdater ledUpdater, int duration) {
        super(ledUpdater, duration, 1000, 1000);
        
        random = new Random();
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];

        byte r = (byte) random.nextInt(255);
        byte g = (byte) random.nextInt(255);
        byte b = (byte) random.nextInt(255);
        
        if (rearFlashGroup == 0) {
            for (int i = 0; i < 51; i += 3) {
                bytes[i] = r;
                bytes[i + 1] = g;
                bytes[i + 2] = b;
            }
        } else if (rearFlashGroup == 1) {
            for (int i = 51; i < 108; i += 3) {
                bytes[i] = r;
                bytes[i + 1] = g;
                bytes[i + 2] = b;
            }
        } else if (rearFlashGroup == 2) {
            for (int i = 108; i < 159; i += 3) {
                bytes[i] = r;
                bytes[i + 1] = g;
                bytes[i + 2] = b;
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
                bytes[i] = (byte) 36;
                bytes[i + 1] = (byte) 148;
                bytes[i + 2] = (byte) 253;
            }
        }
        frontFlash = !frontFlash;
        setFrontLEDBytes(bytes);
    }
}
