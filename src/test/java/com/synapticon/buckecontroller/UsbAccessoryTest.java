package com.synapticon.buckecontroller;

import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;

public class UsbAccessoryTest {

    public UsbAccessoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testSwitchDevice() throws UsbException {
        UsbAccessory usbAccessory = new UsbAccessory();
        usbAccessory.switchDevice();
    }

}
