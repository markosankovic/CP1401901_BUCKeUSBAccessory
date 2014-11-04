package com.synapticon.buckeusbaccessory;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
            "Synapticon",
            "BUCKe USB Accessory",
            "JavaFX application that simulates the On-Board Controller",
            "0.0.1",
            "http://bucke.synapticon.com/",
            "0");

    static final short DEVICE_VENDOR_ID = (short) 0x0bb4; // HTC Corporation
    static final short DEVICE_PRODUCT_ID = (short) 0x0f63; // HTC One mini

    static final short ACCESSORY_VENDOR_ID = (short) 0x18D1;
    static final short ACCESSORY_PRODUCT_ID = (short) 0x2D01;

    byte states = 0x21;

    Thread readThread;
    Thread stateMessageThread;

    UsbDeviceListener usbDeviceListener = new UsbDeviceListener() {
        @Override
        public void usbDeviceDetached(UsbDeviceEvent ude) {
            logger.log(Level.INFO, "USB device is detached");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    switchToUSBAccessoryModeButton.setDisable(false);
                }
            });

            FXMLController.this.androidDevice = null;
        }

        @Override
        public void errorEventOccurred(UsbDeviceErrorEvent udee) {
            logger.log(Level.INFO, udee.getUsbException().getMessage());
        }

        @Override
        public void dataEventOccurred(UsbDeviceDataEvent udde) {
        }
    };

    @FXML
    TextField codeTextField;
    String code = "qwerty";

    @FXML
    void handleCodeTextChanged(ActionEvent event) {
        logger.log(Level.INFO, "Code text changed: " + codeTextField.getText());
        code = codeTextField.getText();
    }

    @FXML
    ComboBox sendIntervalComboBox;

    @FXML
    Slider speedSlider;
    short speed = 0;

    @FXML
    Slider batteryPowerSlider;
    short batteryPower = 0;

    @FXML
    Slider batteryStateOfChargeSlider;
    byte batteryStateOfCharge = 80;

    @FXML
    Slider remainingDistanceSlider;
    short remainingDistance = 150;

    @FXML
    Slider totalDistanceSlider;
    int totalDistance = 950;

    @FXML
    Slider remainingBoostSlider;
    byte remainingBoost = 20;

    @FXML
    TextArea logTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Set initial code
        code = codeTextField.getText();

        // Send Interval
        ObservableList<Integer> intervals = FXCollections.observableArrayList(5, 10, 15, 20, 50, 100, 200, 500, 1000, 3000);
        sendIntervalComboBox.setItems(intervals);

        // Speed Slider
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                speed = newValue.shortValue();
                logger.log(Level.INFO, String.format("New speed value: %s", String.valueOf(speed)));
            }
        });

        // Battery Power Slider
        batteryPowerSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                batteryPower = newValue.shortValue();
                logger.log(Level.INFO, String.format("New battery power value: %s", String.valueOf(batteryPower)));
            }
        });

        // Battery State of Charge Slider
        batteryStateOfChargeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                batteryStateOfCharge = newValue.byteValue();
                logger.log(Level.INFO, String.format("New battery state of charge value: %s", String.valueOf(batteryStateOfCharge)));
            }
        });

        // Remaining Distance Slider
        remainingDistanceSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                remainingDistance = newValue.byteValue();
                logger.log(Level.INFO, String.format("New remaining distance value: %s", String.valueOf(remainingDistance)));
            }
        });

        // Total Distance Slider
        totalDistanceSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                totalDistance = newValue.intValue();
                logger.log(Level.INFO, String.format("New total distance value: %s", String.valueOf(totalDistance)));
            }
        });

        // Remaining Boost Slider
        remainingBoostSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                remainingBoost = newValue.byteValue();
                logger.log(Level.INFO, String.format("New remaining boost value: %s", String.valueOf(remainingBoost)));
            }
        });

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
    Button switchToUSBAccessoryModeButton;

    @FXML
    void onClose() {
        logger.log(Level.INFO, "Handle Close");
        Stage stage = (Stage) switchToUSBAccessoryModeButton.getScene().getWindow();
        closeAndroidDevice();
        stage.close();
    }

    void closeAndroidDevice() {
        if (androidDevice != null) {
            try {
                androidDevice.sendMessage(OnBoardControllerConstants.SOFT_CLOSE_MESSAGE, new byte[]{});
                androidDevice.close();
            } catch (UsbException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void onSmartphoneConnected(ActionEvent event) {
        states = (byte) (states ^ 0x01);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void onHeadlightToggle(ActionEvent event) {
        states = (byte) (states ^ 0x02);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void onCameraToggle(ActionEvent event) {
        states = (byte) (states ^ 0x04);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void onLeftTurnSignalToggle(ActionEvent event) {
        states = (byte) (states ^ 0x08);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void onRightTurnSignalToggle(ActionEvent event) {
        states = (byte) (states ^ 0x10);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void onGasThrottleIdleStateToggle(ActionEvent event) {
        states = (byte) (states ^ 0x20);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void onSwitchToUSBAccessoryMode() {
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

            // Start new state message thread
            stateMessageThread = new Thread(new StateMessageRunnable());
            stateMessageThread.start();

            // Disable connect button
            switchToUSBAccessoryModeButton.setDisable(true);

            logger.log(Level.INFO, "Connected to USB device");
        } catch (UsbException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    class ReadRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] data = new byte[16384];
                    int received = androidDevice.getReadPipe().syncSubmit(data);
                    // logger.log(Level.INFO, String.format("Bytes received: %d", received));

                    short command = Utils.twoBytesToShort(data[0], data[1]);

                    switch (command) {
                        case OnBoardControllerConstants.SOFT_CLOSE_COMMAND:
                            logger.log(Level.INFO, "Close request initiated. Send 0x19 to the attached device.");
                            androidDevice.sendMessage(OnBoardControllerConstants.SOFT_CLOSE_MESSAGE, new byte[]{});
                            break;
                        case OnBoardControllerConstants.VERIFY_CODE_COMMAND:
                            if (received > 2) {
                                byte[] codeBytes = Arrays.copyOfRange(data, 2, received);
                                String verificationCode = new String(codeBytes);
                                logger.log(Level.INFO, String.format("Code verification requested: " + verificationCode));
                                Thread.sleep(500);
                                if (code.equals(verificationCode)) {
                                    logger.log(Level.INFO, "Code is successfully verified. Notify the attached device.");
                                    androidDevice.sendMessage(OnBoardControllerConstants.CODE_VERIFICATION_MESSAGE, new byte[]{0});
                                } else {
                                    logger.log(Level.INFO, "Codes don't match. Send error to the attached device.");
                                    androidDevice.sendMessage(OnBoardControllerConstants.CODE_VERIFICATION_MESSAGE, new byte[]{1});
                                }
                            } else {
                                logger.log(Level.WARNING, "Code is empty.");
                                androidDevice.sendMessage(OnBoardControllerConstants.CODE_VERIFICATION_MESSAGE, new byte[]{2});
                            }
                            break;
                        case OnBoardControllerConstants.RELEASE_DRIVETRAIN_COMMAND:
                            logger.log(Level.INFO, "Release drivetrain");
                            break;
                        case OnBoardControllerConstants.DISABLE_DRIVETRAIN_COMMAND:
                            logger.log(Level.INFO, "Disable drivetrain");
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

    class StateMessageRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    buffer.put(states);
                    buffer.putShort(speed); // SPEED
                    buffer.putShort(batteryPower); // BATTERY_POWER
                    buffer.put(batteryStateOfCharge); // BATTERY_STATE_OF_CHARGE
                    buffer.put((byte) remainingDistance); // REMAINING_DISTANCE
                    buffer.putShort((short) totalDistance);
                    buffer.put((byte) remainingBoost);

                    androidDevice.sendMessage(OnBoardControllerConstants.OBC_STATE_MESSAGE, buffer.array());

                    Integer interval = (Integer) sendIntervalComboBox.getSelectionModel().getSelectedItem();

                    if (interval == null) {
                        interval = (Integer) sendIntervalComboBox.getItems().get(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sendIntervalComboBox.setValue(sendIntervalComboBox.getItems().get(1));
                            }
                        });
                    }

                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    logger.log(Level.INFO, "Driving mode thread is interrupted.");
                    break;
                }
            }
        }
    }
}
