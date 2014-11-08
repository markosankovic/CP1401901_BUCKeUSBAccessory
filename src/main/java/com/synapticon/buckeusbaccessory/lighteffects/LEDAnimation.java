package com.synapticon.buckeusbaccessory.lighteffects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * LEDAnimation.
 *
 * Represents one LED animation in a light effect pattern. Every animation lasts
 * for a certain amount of time defined by the duration field. When the duration
 * of the animation expires, this LED animation starts the next one thus forming
 * the chain of animations.
 *
 * Front and rear LED are updated independently and in different periods during
 * the animation.
 */
public abstract class LEDAnimation {

    private final int duration;
    private final LEDUpdater ledUpdater;
    private final int frontLEDPeriod;
    private final int rearLEDPeriod;

    private LEDAnimation next;
    private ScheduledExecutorService animationExecutor;

    private byte[] frontLEDBytes = new byte[66];
    private byte[] rearLEDBytes = new byte[159];

    public LEDAnimation(LEDUpdater ledUpdater, int duration, int frontLEDPeriod, int rearLEDPeriod) {
        this.duration = duration;
        this.ledUpdater = ledUpdater;
        this.frontLEDPeriod = frontLEDPeriod;
        this.rearLEDPeriod = rearLEDPeriod;
    }

    /**
     * @return the duration
     */
    protected int getDuration() {
        return duration;
    }

    /**
     * @return the ledUpdater
     */
    protected LEDUpdater getLedUpdater() {
        return ledUpdater;
    }

    /**
     * @return the next LED animation to start
     */
    protected LEDAnimation getNext() {
        return next;
    }

    /**
     * @param next the next LED animation to set
     */
    public void setNext(LEDAnimation next) {
        this.next = next;
    }

    /**
     * @return the frontLEDBytes
     */
    protected byte[] getFrontLEDBytes() {
        return frontLEDBytes;
    }

    /**
     * @param frontLEDBytes the frontLEDBytes to set
     */
    protected void setFrontLEDBytes(byte[] frontLEDBytes) {
        this.frontLEDBytes = frontLEDBytes;
    }

    /**
     * @return the rearLEDBytes
     */
    protected byte[] getRearLEDBytes() {
        return rearLEDBytes;
    }

    /**
     * @param rearLEDBytes the rearLEDBytes to set
     */
    protected void setRearLEDBytes(byte[] rearLEDBytes) {
        this.rearLEDBytes = rearLEDBytes;
    }

    /**
     * Combines the front and rear LED bytes.
     *
     * Front and rear LED are updated independently and in different time
     * intervals.
     *
     * @return
     */
    protected synchronized byte[] combineFrontAndRearLEDBytes() {
        byte[] combined = new byte[frontLEDBytes.length + rearLEDBytes.length];
        System.arraycopy(frontLEDBytes, 0, combined, 0, frontLEDBytes.length);
        System.arraycopy(rearLEDBytes, 0, combined, frontLEDBytes.length, rearLEDBytes.length);
        return combined;
    }

    /**
     * Starts the animation executor.
     *
     * Animation executor runs for the period of provided duration. During the
     * execution front and rear LED are animated, bytes are combined and sent to
     * the OBC.
     */
    public void start() {
        // Re-initalize animation executor
        animationExecutor = Executors.newSingleThreadScheduledExecutor();

        // After duration stop this animation and go to the next one
        animationExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                animationExecutor.shutdownNow();
                if (next != null) {
                    next.start();
                }

            }
        }, duration, TimeUnit.MILLISECONDS);

        // Animate the front LED
        animationExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                animateFrontLED();
                updateLED();
            }
        }, 0, frontLEDPeriod, TimeUnit.MILLISECONDS);

        // Animate the rear LED
        animationExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                animateRearLED();
                updateLED();
            }
        }, 0, rearLEDPeriod, TimeUnit.MILLISECONDS);
    }

    private void updateLED() {
        ledUpdater.updateLED(combineFrontAndRearLEDBytes());
    }

    /**
     * Stops the animation by shutting down the animation executor.
     *
     * Follow the chain and stop the running animation executor in the next LED
     * animation.
     */
    public void stop() {
        if (animationExecutor.isShutdown() && next != null) {
            next.stop();
        } else if (!animationExecutor.isShutdown()) {
            animationExecutor.shutdownNow();
        }
    }

    /**
     * Animates the front LED.
     *
     * Successive calls to the scheduled animation executor call this method,
     * which updates the front LED bytes. Updated bytes are combined and sent to
     * the OBC.
     */
    abstract protected void animateFrontLED();

    /**
     * Animates the rear LED.
     *
     * Successive calls to the scheduled animation executor call this method,
     * which updates the rear LED bytes. Updated bytes are combined and sent to
     * the OBC.
     */
    abstract protected void animateRearLED();
}
