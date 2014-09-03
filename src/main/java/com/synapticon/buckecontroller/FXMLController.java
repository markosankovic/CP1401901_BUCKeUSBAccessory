package com.synapticon.buckecontroller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javax.usb.UsbException;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;

public class FXMLController implements Initializable {

    static final Logger logger = Logger.getLogger(FXMLController.class.getName());

    AndroidDevice androidDevice;

    IdentifyingInformation identifyingInformation = new IdentifyingInformation(
            "sankovicmarko.com",
            "USBAccessoryService",
            "USBAccessoryServiceDescription",
            "0.0.1",
            "httsp://usbaccessoryservice.sankovicmarko.com",
            "USBAccessoryServiceSerial");

    public static final short DEVICE_VENDOR_ID = (short) 0x18D1; // Google
    public static final short DEVICE_PRODUCT_ID = (short) 0xD002; // Google Nexus 4

    public static final short ACCESSORY_VENDOR_ID = (short) 0x18D1;
    public static final short ACCESSORY_PRODUCT_ID = (short) 0x2D01;

    Thread readThread;
    Thread drivingModeThread;

    UsbDeviceListener usbDeviceListener = new UsbDeviceListener() {
        @Override
        public void usbDeviceDetached(UsbDeviceEvent ude) {
            logger.log(Level.INFO, "USB device is detached");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    connectButton.setDisable(false);
                }
            });

            FXMLController.this.androidDevice = null;
        }

        @Override
        public void errorEventOccurred(UsbDeviceErrorEvent udee) {
        }

        @Override
        public void dataEventOccurred(UsbDeviceDataEvent udde) {
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handleConnect(); // try to connect to Android device on initialize
    }

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private Button connectButton;

    @FXML
    private ToggleButton modeToggleButton;
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) connectButton.getScene().getWindow();
        closeAndroidDevice();
        stage.close();
    }

    private void closeAndroidDevice() {
        if (androidDevice != null) {
            try {
                androidDevice.close();
            } catch (UsbException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void handleConnect() {
        try {

            // Get Android device (switch Android device to USB accessory mode)
            androidDevice = new AndroidDevice(new AndroidOpenAccessory(
                    identifyingInformation,
                    DEVICE_VENDOR_ID, DEVICE_PRODUCT_ID, ACCESSORY_VENDOR_ID, ACCESSORY_PRODUCT_ID)
            );

            // Add USB device listener
            androidDevice.getDevice().addUsbDeviceListener(usbDeviceListener);

            // Start new reading thread
            readThread = new Thread(new ReadRunnable());
            readThread.start();

            // Disable connect button
            connectButton.setDisable(true);

            logger.log(Level.INFO, "Connected to USB device");
            
        } catch (UsbException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleModeChange(ActionEvent event) {
        if (modeToggleButton.isSelected()) {
             modeToggleButton.setText("DRIVING MODE");
        } else {
            modeToggleButton.setText("STANDSTILL MODE");
        }
    }
    
    @FXML
    private void handleSendRandomSpeed(ActionEvent event) {
        logger.log(Level.INFO, "Handle Send Random Speed");
        androidDevice.sendCommand((byte) 0x41, (byte) 0, new byte[]{(byte) Utils.randInt(0, 50)});
    }

    @FXML
    private void handleSendSmartphoneConnected(ActionEvent event) {
        logger.log(Level.INFO, "Handle Send Smartphone Connected");
    }

    protected class ReadRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] data = new byte[16384];
                    int received = androidDevice.getReadPipe().syncSubmit(data);

                    if (data[0] == 0x18) {
                        logger.log(Level.INFO, "Close request initiated. Send 0x19 to the Android device.");
                        androidDevice.sendCommand((byte) 0x19, (byte) 0, new byte[]{});
                    }
                    logger.log(Level.INFO, "{0} bytes received", received);
                } catch (UsbException ex) {
                    logger.log(Level.WARNING, ex.getMessage());
                    break;
                }
            }
        }
    }

    protected class DrivingModeRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    androidDevice.sendCommand((byte) 0x41, (byte) 0, new byte[]{(byte) Utils.randInt(0, 50)});
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, null, ex);
                    break;
                }
            }
        }
    }

}
