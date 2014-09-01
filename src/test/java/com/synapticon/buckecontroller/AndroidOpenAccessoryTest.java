package com.synapticon.buckecontroller;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class AndroidOpenAccessoryTest {

    @Test
    public void testSwitchDevice() throws UsbException {
        AndroidOpenAccessory openAccessory = new AndroidOpenAccessory();
        UsbDevice device = openAccessory.switchDevice();
        assertNotNull(device);
    }
}
