package com.synapticon.buckecontroller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

/**
 * AndroidOpenAccessory.
 *
 * @see https://source.android.com/accessories/protocol.html
 */
public class AndroidOpenAccessory {

    private final IdentifyingInformation identifyingInformation;
    private final short deviceVendorId;
    private final short deviceProductId;
    private final short accessoryVendorId;
    private final short accessoryProductId;

    /**
     * AndroidOpenAccessory.
     *
     * @param identifyingInformation
     * @param deviceVendorId
     * @param deviceProductId
     * @param accessoryVendorId
     * @param accessoryProductId
     */
    public AndroidOpenAccessory(IdentifyingInformation identifyingInformation, short deviceVendorId, short deviceProductId, short accessoryVendorId, short accessoryProductId) {
        this.identifyingInformation = identifyingInformation;
        this.deviceVendorId = deviceVendorId;
        this.deviceProductId = deviceProductId;
        this.accessoryVendorId = accessoryVendorId;
        this.accessoryProductId = accessoryProductId;
    }

    /**
     * Attempt to Start in Accessory Mode.
     *
     * If the vendor and product IDs do not correspond to an Android-powered
     * device in accessory mode, the accessory cannot discern whether the device
     * supports accessory mode and is not in that state, or if the device does
     * not support accessory mode at all. This is because devices that support
     * accessory mode but aren't in it initially report the device's
     * manufacturer vendor ID and product ID, and not the special Android Open
     * Accessory ones. In either case, the accessory should try to start the
     * device into accessory mode to figure out if the device supports it.
     *
     * @return
     * @throws UsbException
     */
    public UsbDevice switchDevice() throws UsbException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub rootUsbHub = usbServices.getRootUsbHub();

        UsbDevice device = findDevice(rootUsbHub, deviceVendorId, deviceProductId);

        // Check if the attached device supports Android accessory mode and is already in accessory mode.
        if (device == null) {
            UsbDevice accessory = findDevice(rootUsbHub, accessoryVendorId, accessoryProductId);
            if (accessory == null) {
                throw new UsbException("The attached device does not support Android accessory mode");
            }
            return accessory;
        }
        
        // The attached device supports Android accessory mode, but it is not in accessory mode.
        checkProtocol(device);
        sendIdentifyingStringInformationToTheDevice(device);
        requestTheDeviceStartUpInAccessoryMode(device);

        try {
            Thread.sleep(1000); // Give time for USB device to re-introduce itself on the bus in accessory mode.
        } catch (InterruptedException ex) {
            Logger.getLogger(AndroidOpenAccessory.class.getName()).log(Level.SEVERE, null, ex);
        }

        UsbDevice accessory = findDevice(rootUsbHub, accessoryVendorId, accessoryProductId);

        return accessory;
    }

    /**
     * Find device by vendor id and product id.
     *
     * @param hub
     * @param vendorId
     * @param productId
     * @return
     */
    private UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {

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

    /**
     * Figure out if the device supports the Android accessory protocol.
     *
     * Send a 51 control request ("Get Protocol") to figure out if the device
     * supports the Android accessory protocol. A non-zero number is returned if
     * the protocol is supported, which represents the version of the protocol
     * that the device supports. This request is a control request on endpoint
     * 0.
     *
     * @param device
     * @throws UsbException
     */
    private void checkProtocol(UsbDevice device) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) (byte) (UsbConst.REQUESTTYPE_DIRECTION_IN | UsbConst.REQUESTTYPE_TYPE_VENDOR),
                (byte) 51,
                (short) 0,
                (short) 0
        );
        irp.setData(new byte[2]);
        device.syncSubmit(irp);

        int protocol = irp.getData()[0];

        if (protocol < 1) {
            throw new RuntimeException("Could not read device protocol version");
        }
    }

    /**
     * Send identifying string information to the device.
     *
     * If the device returns a proper protocol version, send identifying string
     * information to the device. This information allows the device to figure
     * out an appropriate application for this accessory and also present the
     * user with a URL if an appropriate application does not exist. These
     * requests are control requests on endpoint 0 (for each string ID)
     *
     * @param device
     * @throws UsbException
     */
    private void sendIdentifyingStringInformationToTheDevice(UsbDevice device) throws UsbException {
        sendString(device, (short) 0, identifyingInformation.getManufacturerName());
        sendString(device, (short) 1, identifyingInformation.getModelName());
        sendString(device, (short) 2, identifyingInformation.getDescription());
        sendString(device, (short) 3, identifyingInformation.getVersion());
        sendString(device, (short) 4, identifyingInformation.getURI());
        sendString(device, (short) 5, identifyingInformation.getSerialNumber());
    }

    private void sendString(UsbDevice device, short id, String value) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) (UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_VENDOR),
                (byte) 52,
                (short) 0,
                (short) id
        );
        irp.setData(value.getBytes());
        device.syncSubmit(irp);

        if (irp.getData().length != value.length()) {
            throw new UsbException();
        }
    }

    /**
     * Request the device start up in accessory mode.
     *
     * When the identifying strings are sent, request the device start up in
     * accessory mode. This request is a control request on endpoint 0. After
     * sending the final control request, the connected USB device should
     * re-introduce itself on the bus in accessory mode and the accessory can
     * re-enumerate the connected devices.
     *
     * @param device
     * @throws UsbException
     */
    private void requestTheDeviceStartUpInAccessoryMode(UsbDevice device) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) (UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_VENDOR),
                (byte) 53,
                (short) 0,
                (short) 0
        );
        device.syncSubmit(irp);
    }
}
