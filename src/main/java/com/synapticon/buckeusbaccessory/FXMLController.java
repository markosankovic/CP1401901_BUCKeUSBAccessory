package com.synapticon.buckeusbaccessory;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    static final short DEVICE_VENDOR_ID = (short) 0x18D1; // Google
    static final short DEVICE_PRODUCT_ID = (short) 0xD002; // Google Nexus 4

    static final short ACCESSORY_VENDOR_ID = (short) 0x18D1;
    static final short ACCESSORY_PRODUCT_ID = (short) 0x2D01;

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

    @FXML
    TextField codeTextField;
    String code;

    @FXML
    void handleCodeTextChanged(ActionEvent event) {
        logger.log(Level.INFO, "Code text changed: " + codeTextField.getText());
        code = codeTextField.getText();
    }

    @FXML
    TextArea logTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Try to connect to Android device on initialize
        handleConnect();

        // Set initial code
        code = codeTextField.getText();

        // Setup logger handler (output logs to TextArea)
        logger.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {

                StringBuilder sb = new StringBuilder();
                sb.append(record.getLevel()).append(": ").append(record.getMessage()).append("\n");
                final String message = sb.toString();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (logTextArea != null) {
                                if (logTextArea.getText().length() == 0) {
                                    logTextArea.setText(message);
                                } else {
                                    logTextArea.selectEnd();
                                    logTextArea.insertText(logTextArea.getText().length(), message);
                                }
                            }
                        } catch (final Throwable t) {
                            System.out.println("Unable to append log to text area: " + t.getMessage());
                        }
                    }
                });
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
    }

    @FXML
    MenuItem closeMenuItem;

    @FXML
    Button connectButton;

    @FXML
    ToggleButton modeToggleButton;

    @FXML
    void handleClose() {
        logger.log(Level.INFO, "Handle Close");
        Stage stage = (Stage) connectButton.getScene().getWindow();
        closeAndroidDevice();
        stage.close();
    }

    void closeAndroidDevice() {
        if (androidDevice != null) {
            try {
                androidDevice.close();
            } catch (UsbException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void handleConnect() {
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
            modeToggleButton.setText("STANDSTILL");
        }
    }

    @FXML
    void handleSendRandomSpeed(ActionEvent event) {
        logger.log(Level.INFO, "Handle Send Random Speed");
        androidDevice.sendCommand((byte) 0x41, (byte) 0, new byte[]{(byte) Utils.randInt(0, 50)});
    }

    @FXML
    void handleSendSmartphoneConnected(ActionEvent event) {
        logger.log(Level.INFO, "Handle Send Smartphone Connected");
    }

    class ReadRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] data = new byte[16384];
                    int received = androidDevice.getReadPipe().syncSubmit(data);
                    // logger.log(Level.INFO, String.format("Bytes received: %d", received));

                    switch (data[0]) {
                        case 0x18:
                            logger.log(Level.INFO, "Close request initiated. Send 0x19 to the attached device.");
                            androidDevice.sendCommand((byte) 0x19, (byte) 0, new byte[]{});
                            break;
                        case 0x42:
                            if (received > 2) {
                                byte[] codeBytes = Arrays.copyOfRange(data, 2, received);
                                String verificationCode = new String(codeBytes);
                                logger.log(Level.INFO, String.format("Code verification requested: " + verificationCode));
                                Thread.sleep(500);
                                if (code.equals(verificationCode)) {
                                    logger.log(Level.INFO, "Code is successfully verified. Notify the attached device.");
                                    androidDevice.sendCommand((byte) 0x42, (byte) 0, new byte[]{0});
                                } else {
                                    logger.log(Level.INFO, "Codes don't match. Send error to the attached device.");
                                    androidDevice.sendCommand((byte) 0x42, (byte) 0, new byte[]{1});
                                }
                            } else {
                                logger.log(Level.WARNING, "Code is empty.");
                                androidDevice.sendCommand((byte) 0x42, (byte) 0, new byte[]{2});
                            }
                            break;
                        default:
                            logger.log(Level.WARNING, String.format("No such command: %d", data[0]));
                    }

                } catch (UsbException ex) {
                    logger.log(Level.WARNING, ex.getMessage());
                    break;
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class DrivingModeRunnable implements Runnable {

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
