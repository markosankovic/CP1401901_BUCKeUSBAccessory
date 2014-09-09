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
        byte[] buffer = Utils.prependShortToByteArray(message, payload);
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
}
