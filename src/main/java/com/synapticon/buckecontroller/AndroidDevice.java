package com.synapticon.buckecontroller;

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
        writePipe.close();
        readPipe.close();
        iface.release();
    }

    /**
     * Send command to the Android device.
     * 
     * @param command
     * @param target
     * @param payload 
     */
    public void sendCommand(byte command, byte target, byte[] payload) {
        byte[] buffer = AndroidDevice.getCommandBytes(command, target, payload);
        try {
            getWritePipe().syncSubmit(buffer);
        } catch (UsbException e) {
            Logger.getLogger(AndroidDevice.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Combine command and payload into one byte array.
     * 
     * @param command
     * @param payload
     * @return 
     */
    public static byte[] getCommandBytes(byte command, byte target, byte[] payload) {
        byte[] combined = new byte[2 + payload.length];
        combined[0] = command;
        combined[1] = target;
        System.arraycopy(payload, 0, combined, 2, payload.length);
        return combined;
    }
}
