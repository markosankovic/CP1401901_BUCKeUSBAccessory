package com.synapticon.buckeusbaccessory;

import com.synapticon.buckeusbaccessory.AndroidOpenAccessory;
import com.synapticon.buckeusbaccessory.IdentifyingInformation;
import javax.usb.UsbDevice;
import javax.usb.UsbException;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

public class AndroidOpenAccessoryTest {

    @Ignore
    @Test
    public void testSwitchDevice() throws UsbException {
        // Initialize the Google Nexus 4 (0xD002)
        AndroidOpenAccessory openAccessory = new AndroidOpenAccessory(new IdentifyingInformation(
                "sankovicmarko.com",
                "USBAccessoryService",
                "USBAccessoryServiceDescription",
                "0.0.1",
                "httsp://usbaccessoryservice.sankovicmarko.com",
                "USBAccessoryServiceSerial"
        ), (short) 0x18D1, (short) 0xD002, (short) 0x18D1, (short) 0x2D01);
        UsbDevice device = openAccessory.switchDevice();
        assertNotNull(device);
    }
}
