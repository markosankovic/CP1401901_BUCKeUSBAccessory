package com.synapticon.buckeusbaccessory;

import javax.usb.UsbException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class AndroidDeviceTest {

    IdentifyingInformation identifyingInformation = new IdentifyingInformation(
            "sankovicmarko.com",
            "USBAccessoryService",
            "USBAccessoryServiceDescription",
            "0.0.1",
            "httsp://usbaccessoryservice.sankovicmarko.com",
            "USBAccessoryServiceSerial");

    static final short DEVICE_VENDOR_ID = (short) 0x18D1; // Google
    static final short DEVICE_PRODUCT_ID = (short) 0xD002; // Google Nexus 4

    static final short ACCESSORY_VENDOR_ID = (short) 0x18D1;
    static final short ACCESSORY_PRODUCT_ID = (short) 0x2D01;

    @Ignore
    @Test
    public void testWriteToTheDevice() throws InterruptedException, UsbException {

        AndroidDevice androidDevice = new AndroidDevice(new AndroidOpenAccessory(
                identifyingInformation,
                DEVICE_VENDOR_ID, DEVICE_PRODUCT_ID, ACCESSORY_VENDOR_ID, ACCESSORY_PRODUCT_ID)
        );

        int times = 5;
        while (true) {
            if (times == 0) {
                break;
            }
            int syncSubmit = androidDevice.getWritePipe().syncSubmit("A0000".getBytes());
            times--;
            Thread.sleep(1000);
        }

        androidDevice.close();
    }

    @Ignore
    @Test
    public void testReadFromTheDevice() throws InterruptedException, UsbException {

        AndroidDevice androidDevice = new AndroidDevice(new AndroidOpenAccessory(
                identifyingInformation,
                DEVICE_VENDOR_ID, DEVICE_PRODUCT_ID, ACCESSORY_VENDOR_ID, ACCESSORY_PRODUCT_ID)
        );

        int times = 3;
        while (true) {
            if (times == 0) {
                break;
            }

            byte[] data = new byte[16384];
            int received = androidDevice.getReadPipe().syncSubmit(data);
            System.out.println(received + " bytes received");

            times--;
            Thread.sleep(1000);
        }

        androidDevice.close();
    }

    @Ignore
    @Test
    public void testWriteSpeedToTheDevice() throws InterruptedException, UsbException {

        AndroidDevice androidDevice = new AndroidDevice(new AndroidOpenAccessory(
                identifyingInformation,
                DEVICE_VENDOR_ID, DEVICE_PRODUCT_ID, ACCESSORY_VENDOR_ID, ACCESSORY_PRODUCT_ID)
        );

        int times = 20;
        while (true) {
            if (times == 0) {
                break;
            }
            int speed = Utils.randInt(0, 50);
            System.out.println(speed);
            byte[] buffer = new byte[]{(byte) speed};
            androidDevice.sendMessage((short) 0x41, buffer);
            times--;
            Thread.sleep(100);
        }

        androidDevice.close();
    }

    @Ignore
    @Test
    public void testGetCommandBytes() {
        byte[] combined = AndroidDevice.combineMessageAndPayloadBytes((short) 6, new byte[]{9, 12});
        Assert.assertEquals(6, combined[0]);
        Assert.assertEquals(3, combined[1]);
        Assert.assertEquals(9, combined[2]);
        Assert.assertEquals(12, combined[3]);
    }
}
