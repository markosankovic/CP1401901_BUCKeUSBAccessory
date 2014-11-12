package com.synapticon.buckeusbaccessory;

import static com.synapticon.buckeusbaccessory.FXMLController.logger;
import java.util.Random;
import java.util.logging.Level;

public class Utils {

    /**
     * Returns a pseudo-random number between min and max, inclusive. The
     * difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    /**
     * Convert two bytes to short.
     *
     * @param b1
     * @param b2
     * @return
     */
    public static short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    /**
     * Prepend short to byte array and return one byte array.
     *
     * @param s
     * @param a
     * @return
     */
    public static byte[] prependShortToByteArray(short s, byte[] a) {
        byte[] combined = new byte[2 + a.length];
        byte[] b = new byte[2];
        b[0] = (byte) (s >> 8);
        b[1] = (byte) (s & 0xFF);
        System.arraycopy(b, 0, combined, 0, 2);
        System.arraycopy(a, 0, combined, 2, a.length);
        return combined;
    }


}
