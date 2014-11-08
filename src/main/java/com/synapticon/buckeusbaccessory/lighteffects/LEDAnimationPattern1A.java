package com.synapticon.buckeusbaccessory.lighteffects;

/**
 * LIGHT EFFECTS - PATTERN 1 / LED Animation A
 *
 * Back: All LEDs flash slow, 1 time per second; Front: All LEDs flash slow, 1
 * time per second; last 4s.
 */
public class LEDAnimationPattern1A extends LEDAnimation {

    private boolean flashFrontLED = true;
    private boolean flashRearLED = true;

    public LEDAnimationPattern1A(int duration, LEDUpdater ledUpdater) {
        super(ledUpdater, duration, 1000, 1000);
    }

    @Override
    protected void animateFrontLED() {
        byte[] bytes = new byte[getFrontLEDBytes().length];
        if (flashFrontLED) {
            for (int i = 0; i < getFrontLEDBytes().length; i += 3) {
                bytes[i] = (byte) 255;
                bytes[i + 1] = (byte) 204;
                bytes[i + 2] = (byte) 0;
            }
        }
        setFrontLEDBytes(bytes);
        flashFrontLED = !flashFrontLED;
    }

    @Override
    protected void animateRearLED() {
        byte[] bytes = new byte[getRearLEDBytes().length];
        if (flashRearLED) {
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
        setRearLEDBytes(bytes);
        flashRearLED = !flashRearLED;
    }
}
