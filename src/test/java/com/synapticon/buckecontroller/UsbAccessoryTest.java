package com.synapticon.buckecontroller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.usb4java.Device;

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

    @Ignore
    @Test
    public void testFindDevice() {
        UsbAccessory usbAccessory = new UsbAccessory();
        Device device;
        device = usbAccessory.findDevice(0x18d1, 0xd002);
        assertNotNull("Android device (Nexus 4) is not found", device);
    }

    @Ignore
    @Test
    public void testStartAccessoryMode() {
        UsbAccessory usbAccessory = new UsbAccessory();
        Device device = usbAccessory.startAccessoryMode();
    }

    @Test
    public void testSwitchDevice() {
        UsbAccessory usbAccessory = new UsbAccessory();
        usbAccessory.switchDevice();
    }
    
    @Ignore
    @Test
    public void testIntToShortConversion() {
        System.out.println(0xd002);
        System.out.println((short) 0xd002);

        System.out.println(0x2d01);
        System.out.println((short) 0x2d01);
    }

}
