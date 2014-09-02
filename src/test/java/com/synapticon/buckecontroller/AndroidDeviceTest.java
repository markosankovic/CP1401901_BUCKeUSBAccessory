package com.synapticon.buckecontroller;

import javax.usb.UsbException;
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

        int times = 10;
        while (true) {
            if (times == 0) {
                break;
            }
            int syncSubmit = androidDevice.getWritePipe().syncSubmit("A0000".getBytes());
        }
        times--;
        Thread.sleep(1000);
    }
}
