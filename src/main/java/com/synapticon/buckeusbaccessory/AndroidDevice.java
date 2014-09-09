package com.synapticon.buckeusbaccessory;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;

public final class AndroidDevice {

    AndroidOpenAccessory openAccessory;
    private final UsbDevice device;

    UsbInterface iface;
    private final UsbPipe readPipe;
    private final UsbPipe writePipe;

    /**
     * AndroidDevice.
     *
     * Switch device to USB Accessory Mode and open read and write pipes.
     *
     * @param openAccessory
     * @throws UsbException
     */
    public AndroidDevice(AndroidOpenAccessory openAccessory) throws UsbException {
        this.openAccessory = openAccessory;

        device = openAccessory.switchDevice();

        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        iface = configuration.getUsbInterface((byte) 0);
        iface.claim();

        UsbEndpoint readEndpoint = iface.getUsbEndpoint((byte) 0x81);
        readPipe = readEndpoint.getUsbPipe();
        getReadPipe().open();

        UsbEndpoint writeEndpoint = iface.getUsbEndpoint((byte) 0x02);
        writePipe = writeEndpoint.getUsbPipe();
        getWritePipe().open();
    }

    /**
     * @return the device
     */
    public UsbDevice getDevice() {
        return device;
    }

    /**
     * @return the readPipe
     */
    public UsbPipe getReadPipe() {
        return readPipe;
    }

    /**
     * @return the writePipe
     */
    public UsbPipe getWritePipe() {
        return writePipe;
    }

    /**
     * Close read and write pipes and release USB interface.
     *
     * @throws javax.usb.UsbException
     */
    public void close() throws UsbException {
        if (device.isConfigured()) {
            if (writePipe.isOpen()) {
                writePipe.abortAllSubmissions();
                writePipe.close();
            }
            if (readPipe.isOpen()) {
                readPipe.abortAllSubmissions();
                readPipe.close();
            }
            if (iface.isClaimed()) {
                iface.release();
            }
        }
    }

    /**
     * Send message to the Android device.
     *
     * @param message
     * @param payload
     */
    public void sendMessage(short message, byte[] payload) {
        byte[] buffer = AndroidDevice.combineMessageAndPayloadBytes(message, payload);
        try {
            getWritePipe().syncSubmit(buffer);
        } catch (UsbException e) {
            Logger.getLogger(AndroidDevice.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Send message to the Android device with empty payload.
     *
     * @param message
     */
    public void sendMessage(short message) {
        this.sendMessage(message, new byte[]{});
    }

    /**
     * Combine message and payload bytes into one byte array.
     *
     * @param message
     * @param payload
     * @return
     */
    public static byte[] combineMessageAndPayloadBytes(short message, byte[] payload) {
        byte[] combined = new byte[2 + payload.length];
        byte[] messageBytes = new byte[2];
        messageBytes[0] = (byte) (message >> 8);
        messageBytes[1] = (byte) (message & 0xFF);
        System.arraycopy(messageBytes, 0, combined, 0, 2);
        System.arraycopy(payload, 0, combined, 2, payload.length);
        return combined;
    }
}
