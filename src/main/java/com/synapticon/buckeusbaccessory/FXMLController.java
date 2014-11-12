package com.synapticon.buckeusbaccessory;

import com.synapticon.buckeusbaccessory.lighteffects.LightEffectPattern1;
import com.synapticon.buckeusbaccessory.lighteffects.LEDUpdater;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.usb.UsbException;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;

public class FXMLController implements Initializable, LEDUpdater {

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
    AnchorPane anchorPane;

    @FXML
    HBox frontLightsHBox;

    @FXML
    HBox rearLightsHBox;

    @FXML
    TextField codeTextField;
    String code = "qwerty";
    private LightEffectPattern1 lightEffectPattern;

    @FXML
    void handleCodeTextChanged(ActionEvent event) {
        logger.log(Level.INFO, "Code text changed: {0}", codeTextField.getText());
        code = codeTextField.getText();
    }

    @FXML
    ChoiceBox sendIntervalChoiceBox;

    @FXML
    ChoiceBox vehicleStateChoiceBox;

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
        sendIntervalChoiceBox.setItems(FXCollections.observableArrayList(5, 10, 15, 20, 50, 100, 200, 500, 1000, 3000));
        sendIntervalChoiceBox.getSelectionModel().selectFirst();

        // Vehicle State
        ObservableList<VehicleState> vehicleStateObservableList = FXCollections.observableArrayList();
        vehicleStateObservableList.add(new VehicleState("Standby (No drive release)", (byte) 0x00));
        vehicleStateObservableList.add(new VehicleState("Standstill", (byte) 0x01));
        vehicleStateObservableList.add(new VehicleState("Recuperation", (byte) 0x02));
        vehicleStateObservableList.add(new VehicleState("Sailing", (byte) 0x03));
        vehicleStateObservableList.add(new VehicleState("Driving", (byte) 0x04));
        vehicleStateObservableList.add(new VehicleState("Boost", (byte) 0x05));
        vehicleStateChoiceBox.setItems(vehicleStateObservableList);
        vehicleStateChoiceBox.getSelectionModel().select(0);

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

        drawLEDs();

        // lightEffectPattern = new LightEffectPattern1(this);
        // lightEffectPattern.start();
    }

    void drawLEDs() {

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.1);

        for (int i = 0; i < 22; i++) {
            Rectangle rect = new Rectangle(12.0, 12.0, Color.TRANSPARENT);
            rect.setStroke(Color.rgb(47, 47, 47));
            rect.setEffect(bloom);
            frontLightsHBox.getChildren().add(rect);
        }

        for (int i = 0; i < 53; i++) {
            Rectangle rect = new Rectangle(12.0, 12.0, Color.TRANSPARENT);
            rect.setStroke(Color.rgb(47, 47, 47));
            rect.setEffect(bloom);
            rearLightsHBox.getChildren().add(rect);
        }
    }

    @FXML
    Button switchToUSBAccessoryModeButton;

    @FXML
    void onClose() {
        logger.log(Level.INFO, "Handle Close");
        Stage stage = (Stage) switchToUSBAccessoryModeButton.getScene().getWindow();
        closeAndroidDevice();
        if (lightEffectPattern != null) {
            lightEffectPattern.stop();
        }
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
            AndroidOpenAccessory androidOpenAccessory = new AndroidOpenAccessory(identifyingInformation, DEVICE_VENDOR_ID, DEVICE_PRODUCT_ID, ACCESSORY_VENDOR_ID, ACCESSORY_PRODUCT_ID);
            androidDevice = new AndroidDevice(androidOpenAccessory.switchDevice());

            // Add USB device listener
            androidDevice.getUsbDevice().addUsbDeviceListener(usbDeviceListener);

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

    @Override
    public void updateLED(byte[] bytes) {

        byte[] frontBytes = Arrays.copyOfRange(bytes, 0, 66);
        byte[] rearBytes = Arrays.copyOfRange(bytes, 66, 225);

        for (int i = 0, j = 0; i < frontBytes.length && j < frontLightsHBox.getChildren().size(); i += 3, j++) {
            int r = frontBytes[i] & 0xFF;
            int g = frontBytes[i + 1] & 0xFF;
            int b = frontBytes[i + 2] & 0xFF;
            final Color color = Color.rgb(r, g, b);
            final Rectangle rect = (Rectangle) frontLightsHBox.getChildren().get(j);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    rect.setFill(color);
                }
            });
        }

        for (int i = 0, j = 0; i < rearBytes.length && j < rearLightsHBox.getChildren().size(); i += 3, j++) {
            int r = rearBytes[i] & 0xFF;
            int g = rearBytes[i + 1] & 0xFF;
            int b = rearBytes[i + 2] & 0xFF;
            final Color color = Color.rgb(r, g, b);
            final Rectangle rect = (Rectangle) rearLightsHBox.getChildren().get(j);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    rect.setFill(color);
                }
            });
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
                        case OnBoardControllerConstants.LED_UPDATE_COMMAND:
                            byte[] bytes = Arrays.copyOfRange(data, 2, received);
                            updateLED(bytes);
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
                    // Prepare data for the smartphone
                    ByteBuffer buffer = ByteBuffer.allocate(11);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    buffer.put(states); // smartphone connected, headlight on, camera on, left turn signal on, right turn signal on, gas throttle idle
                    VehicleState vehicleState = (VehicleState) vehicleStateChoiceBox.getSelectionModel().getSelectedItem();
                    buffer.put(vehicleState.getValue()); // Standby, Standstill, Recuperation, Sailing, Drive, Boost
                    buffer.putShort(speed); // SPEED
                    buffer.putShort(batteryPower); // BATTERY_POWER
                    buffer.put(batteryStateOfCharge); // BATTERY_STATE_OF_CHARGE
                    buffer.put((byte) remainingDistance); // REMAINING_DISTANCE
                    buffer.putShort((short) totalDistance);
                    buffer.put((byte) remainingBoost);

                    // Send data
                    androidDevice.sendMessage(OnBoardControllerConstants.OBC_STATE_MESSAGE, buffer.array());

                    // In the specified interval
                    Integer interval = (Integer) sendIntervalChoiceBox.getSelectionModel().getSelectedItem();
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    logger.log(Level.INFO, "Driving mode thread is interrupted.");
                    break;
                }
            }
        }
    }
}
