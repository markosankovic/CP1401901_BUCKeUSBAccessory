package com.synapticon.buckecontroller;

import java.io.UnsupportedEncodingException;
import javax.usb.UsbException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UsbAccessoryHighTest {

    public UsbAccessoryHighTest() {
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
    public void testSomeMethod() throws UsbException, UnsupportedEncodingException {
        UsbAccessoryHigh usbAccessoryHigh = new UsbAccessoryHigh();
        usbAccessoryHigh.switchDevice();
    }

}
