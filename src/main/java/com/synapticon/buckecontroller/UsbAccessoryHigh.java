package com.synapticon.buckecontroller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

public class UsbAccessoryHigh {

    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
                return device;
            }
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) {
                    return device;
                }
            }
        }
        return null;
    }

    public void switchDevice() throws UsbException, UnsupportedEncodingException, InterruptedException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub rootUsbHub = usbServices.getRootUsbHub();
        
        UsbDevice device = findDevice(rootUsbHub, (short) 0x18d1, (short) 0xd002);
        
        checkProtocol(device);
        setStrings(device);
        setAccessoryMode(device);
        
        Thread.sleep(1000);
        
        UsbDevice accessory = findDevice(rootUsbHub, (short) 0x18d1, (short) 0x2d01);
        
        assert (accessory != null);
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UsbAccessoryHigh.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkProtocol(UsbDevice device) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) 0xC0,
                (byte ) 51,
                (short) 0,
                (short) 0
        );
        irp.setData(new byte[2]);
        device.syncSubmit(irp);
        
        int protocol = irp.getData()[0];
        
        if (protocol < 2) {
            throw new RuntimeException("Only Android Open Accessory protocol v2 is supported");
        }
    }

    private void setStrings(UsbDevice device) throws UsbException {
        sendString(device, (short) 0, "sankovicmarko.com");
        sendString(device, (short) 1, "USBAccessoryService");
        sendString(device, (short) 2, "USBAccessoryServiceDescription");
        sendString(device, (short) 3, "0.0.1");
        sendString(device, (short) 4, "httsp://usbaccessoryservice.sankovicmarko.com");
        sendString(device, (short) 5, "USBAccessoryServiceSerial"); 
    }
    
    private void sendString(UsbDevice device, short id, String value) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) 0x40,
                (byte ) 52,
                (short) 0,
                (short) id
        );
        irp.setData(value.getBytes());
        device.syncSubmit(irp);
        
        if (irp.getData().length != value.length()) {
            throw new UsbException("sendString control return data length is not equal to the submitted String");
        }
    }
    
    private void setAccessoryMode(UsbDevice device) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) 0x40,
                (byte ) 53,
                (short) 0,
                (short) 0
        );
        irp.setData("".getBytes());
        device.syncSubmit(irp);
    }
}
