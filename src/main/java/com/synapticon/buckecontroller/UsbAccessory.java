package com.synapticon.buckecontroller;

import java.nio.ByteBuffer;
import java.util.logging.Logger;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class UsbAccessory {

    Context context;

    private final static Logger LOGGER = Logger.getLogger(UsbAccessory.class.getName());

    public static final int VID_ANDROID_DEVICE = 0x18d1;
    public static final int PID_ANDROID_DEVICE = 0xd002;

    public static final int VID_ANDROID_ACCESSORY = 0x18d1;
    public static final int PID_ANDROID_ACCESSORY = 0x2d01;

    public UsbAccessory() {
        context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }
    }

    public Device getAccessory() {
        LOGGER.info("Looking for Android Accessory");
        LOGGER.info(String.format("VID %d - PID %d", VID_ANDROID_ACCESSORY, PID_ANDROID_ACCESSORY));
        return findDevice(VID_ANDROID_ACCESSORY, PID_ANDROID_ACCESSORY);
    }

    public Device getAndroidDevice() {
        LOGGER.info("Looking for Android Device");
        LOGGER.info(String.format("VID %d - PID %d", VID_ANDROID_DEVICE, PID_ANDROID_DEVICE));
        // DeviceHandle deviceHandle = LibUsb.openDeviceWithVidPid(null, (short) VID_ANDROID_DEVICE, (short) PID_ANDROID_DEVICE);
        return findDevice(VID_ANDROID_DEVICE, PID_ANDROID_DEVICE);
    }

    public void switchDevice() {
        Device device = getAndroidDevice();
        int interfaceNumber = 0;

        DeviceHandle handle = new DeviceHandle();
        int result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to open USB device", result);
        }
        
        try {
            result = LibUsb.claimInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }
            try {
                System.out.println("Use interface here");
            } finally {
                result = LibUsb.releaseInterface(handle, interfaceNumber);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to release interface", result);
                }
            }
        } finally {
            LibUsb.close(handle);
        }
    }

    public Device startAccessoryMode() {
        Device device = getAccessory();
        if (device == null) {
            LOGGER.info("Android accessory not found");
            LOGGER.info("Try to start accessory mode");
            switchDevice();
            device = getAccessory();
            if (device == null) {
                throw new RuntimeException("Unable to start accessory mode");
            }
        }
        LOGGER.info("Accessory mode started");
        return device;
    }

    private void setAccessoryMode(Device device) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Find USB device by Vendor ID and Product ID.
     *
     * @param vendorId
     * @param productId
     * @return
     */
    public Device findDevice(int vendorId, int productId) {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                System.out.println("descriptor.idVendor(): " + descriptor.idVendor());
                System.out.println("vendorId: " + vendorId);
                System.out.println("descriptor.idProduct(): " + descriptor.idProduct());
                System.out.println("productId: " + productId);
                // Nexus 4 device has product id: 0xd002 (53250 decimal).
                // That's more than short type can hold (16bits).
                // Conversion of 0xd002 int to short is -12286 and that's what gets compared here.
                // Do a research on why usb4java uses short for product id.
                if (descriptor.idVendor() == (short) vendorId && descriptor.idProduct() == (short) productId) {
                    return device;
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

}
