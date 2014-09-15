package com.synapticon.buckeusbaccessory;

import java.net.URL;
import java.nio.ByteBuffer;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
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
            "Synapticon",
            "BUCKe USB Accessory",
            "JavaFX application that simulates the On-Board Controller",
            "0.0.1",
            "http://bucke.synapticon.com/",
            "0");

    // static final short DEVICE_VENDOR_ID = (short) 0x18D1; // Google
    // static final short DEVICE_PRODUCT_ID = (short) 0xD002; // Google Nexus 4
    static final short DEVICE_VENDOR_ID = (short) 0x0bb4; // HTC Corporation
    static final short DEVICE_PRODUCT_ID = (short) 0x0f63; // HTC One mini

    static final short ACCESSORY_VENDOR_ID = (short) 0x18D1;
    static final short ACCESSORY_PRODUCT_ID = (short) 0x2D01;

    Thread readThread;
    Thread drivingModeThread;

    byte states = 0x00;

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
    Slider speedSlider;
    byte speed = 0x05;

    @FXML
    Slider recuperationStateSlider;
    short recuperationState = 0x00;

    @FXML
    Slider remainingDistanceSlider;
    short remainingDistance = 0x00;

    @FXML
    Slider powerConsumptionSlider;
    short powerConsumption = 0x00;

    @FXML
    ComboBox sendingIntervalComboBox;
    int sendingInterval;

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

        // Speed Slider
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                byte newSpeed = newValue.byteValue();
                if (speed != newSpeed) {
                    speed = newSpeed;
                    logger.log(Level.INFO, String.format("New speed value: %s", String.valueOf(speed)));
                }
            }
        });

        // Recuperation State Slider
        recuperationStateSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                short newRecuperationState = newValue.shortValue();
                if (recuperationState != newRecuperationState) {
                    recuperationState = newRecuperationState;
                    logger.log(Level.INFO, String.format("New recuperation state value: %s", String.valueOf(recuperationState)));
                }
            }
        });

        // Remaining Distance Slider
        remainingDistanceSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                short newRemainingDistance = newValue.shortValue();
                if (remainingDistance != newRemainingDistance) {
                    remainingDistance = newRemainingDistance;
                    logger.log(Level.INFO, String.format("New remaining distance value: %s", String.valueOf(remainingDistance)));
                }
            }
        });

        // Power Consumption Slider
        powerConsumptionSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                short newPowerConsumption = newValue.shortValue();
                if (powerConsumption != newPowerConsumption) {
                    powerConsumption = newPowerConsumption;
                    logger.log(Level.INFO, String.format("New power consumption value: %s", String.valueOf(powerConsumption)));
                }
            }
        });

        // Sending Interval
        ObservableList<Integer> intervals = FXCollections.observableArrayList(10, 50, 100, 200, 500, 1000, 3000);
        sendingIntervalComboBox.setItems(intervals);

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
    ToggleButton simulateDrivingToggle;

    @FXML
    void handleClose() {
        logger.log(Level.INFO, "Handle Close");
        Stage stage = (Stage) connectButton.getScene().getWindow();
        closeAndroidDevice();
        stage.close();
    }

    @FXML
    void handleBoostToggle(ActionEvent event) {
        states = (byte) (states ^ 0x01);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void handleOverheatingToggle(ActionEvent event) {
        states = (byte) (states ^ 0x02);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void handleLowBeamToggle(ActionEvent event) {
        states = (byte) (states ^ 0x04);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void handleHighBeamToggle(ActionEvent event) {
        states = (byte) (states ^ 0x08);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void handleLeftTurnSignalToggle(ActionEvent event) {
        states = (byte) (states ^ 0x10);
        logger.log(Level.INFO, String.format("States: %d", states));
    }

    @FXML
    void handleRightTurnSignalToggle(ActionEvent event) {
        states = (byte) (states ^ 0x20);
        logger.log(Level.INFO, String.format("States: %d", states));
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
            modeToggleButton.setDisable(false);
        } else {
            modeToggleButton.setText("STANDSTILL");
            modeToggleButton.setDisable(true);
            drivingModeThread.interrupt();
            drivingModeThread = null;
        }
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

                    short command = Utils.twoBytesToShort(data[0], data[1]);

                    switch (command) {
                        case OnBoardControllerConstants.REQUEST_CLOSE_COMMAND:
                            logger.log(Level.INFO, "Close request initiated. Send 0x19 to the attached device.");
                            androidDevice.sendMessage(OnBoardControllerConstants.CLOSE_MESSAGE, new byte[]{});
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
                        case OnBoardControllerConstants.SWITCH_TO_DRIVING_MODE_COMMAND:
                            logger.log(Level.INFO, "Switch to driving mode request.");

                            drivingModeThread = new Thread(new DrivingModeRunnable());
                            drivingModeThread.start();

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    modeToggleButton.setText("DRIVING MODE");
                                    modeToggleButton.setDisable(false);
                                    modeToggleButton.setSelected(true);
                                }
                            });

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
                    ByteBuffer buffer = ByteBuffer.allocate(8);

                    buffer.put(states);
                    buffer.put(speed);
                    buffer.putShort(recuperationState);
                    buffer.putShort(remainingDistance);
                    buffer.putShort(powerConsumption);

                    androidDevice.sendMessage(OnBoardControllerConstants.OBC_STATE_MESSAGE, buffer.array());

                    Integer interval = (Integer) sendingIntervalComboBox.getSelectionModel().getSelectedItem();

                    if (interval == null) {
                        interval = (Integer) sendingIntervalComboBox.getItems().get(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sendingIntervalComboBox.setValue(sendingIntervalComboBox.getItems().get(1));
                            }
                        });
                    }

                    Thread.sleep(interval);

                } catch (InterruptedException ex) {
                    logger.log(Level.INFO, "Driving mode thread is interrupted.");
                    break;
                }
            }
        }
    }

    class SimulateDrivingModeRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {

                    ByteBuffer buffer = ByteBuffer.allocate(8);

                    buffer.put(states);
                    buffer.put(speed);
                    buffer.putShort(recuperationState);
                    buffer.putShort(remainingDistance);
                    buffer.putShort(powerConsumption);

                    androidDevice.sendMessage(OnBoardControllerConstants.OBC_STATE_MESSAGE, buffer.array());

                    Integer interval = (Integer) sendingIntervalComboBox.getSelectionModel().getSelectedItem();

                    if (interval == null) {
                        interval = (Integer) sendingIntervalComboBox.getItems().get(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sendingIntervalComboBox.setValue(sendingIntervalComboBox.getItems().get(1));
                            }
                        });
                    }

                    Thread.sleep(interval);

                } catch (InterruptedException ex) {
                    logger.log(Level.INFO, "Driving mode thread is interrupted.");
                    break;
                }
            }
        }
    }

}
