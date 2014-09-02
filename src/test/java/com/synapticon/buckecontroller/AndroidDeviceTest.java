package com.synapticon.buckecontroller;

import javax.usb.UsbException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class AndroidDeviceTest {

    @Ignore
    @Test
    public void testWriteToTheDevice() throws InterruptedException, UsbException {
        // Initialize the Google Nexus 4 (0xD002)
        AndroidOpenAccessory openAccessory = new AndroidOpenAccessory(new IdentifyingInformation(
                "sankovicmarko.com",
                "USBAccessoryService",
                "USBAccessoryServiceDescription",
                "0.0.1",
                "httsp://usbaccessoryservice.sankovicmarko.com",
                "USBAccessoryServiceSerial"
        ), (short) 0x18D1, (short) 0xD002, (short) 0x18D1, (short) 0x2D01);

        AndroidDevice androidDevice = new AndroidDevice(openAccessory);

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
        // Initialize the Google Nexus 4 (0xD002)
        AndroidOpenAccessory openAccessory = new AndroidOpenAccessory(new IdentifyingInformation(
                "sankovicmarko.com",
                "USBAccessoryService",
                "USBAccessoryServiceDescription",
                "0.0.1",
                "httsp://usbaccessoryservice.sankovicmarko.com",
                "USBAccessoryServiceSerial"
        ), (short) 0x18D1, (short) 0xD002, (short) 0x18D1, (short) 0x2D01);

        AndroidDevice androidDevice = new AndroidDevice(openAccessory);

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
    public void testGetCommandBytes() {
        byte[] combined = AndroidDevice.getCommandBytes((byte) 6, (byte) 3, new byte[]{9, 12});
        Assert.assertEquals(6, combined[0]);
        Assert.assertEquals(3, combined[1]);
        Assert.assertEquals(9, combined[2]);
        Assert.assertEquals(12, combined[3]);
    }
}
