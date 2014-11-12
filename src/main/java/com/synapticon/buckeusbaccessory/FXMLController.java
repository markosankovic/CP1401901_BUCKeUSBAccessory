package com.synapticon.buckeusbaccessory;

import com.synapticon.buckeusbaccessory.lighteffects.LEDUpdater;
import java.net.URL;
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
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

public class FXMLController implements Initializable, LEDUpdater {

    static final Logger logger = Logger.getLogger(FXMLController.class.getName());

    byte states = 0x21;

    @FXML
    AnchorPane anchorPane;

    @FXML
    HBox frontLightsHBox;

    @FXML
    HBox rearLightsHBox;

    @FXML
    TextField codeTextField;
    String code = "qwerty";

    @FXML
    void handleCodeTextFieldAction(ActionEvent event) {
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

        // Clean the resources on window close
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                anchorPane.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        FXMLController.this.onClose();
                    }
                });
            }
        });

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

    public void onClose() {
        logger.log(Level.INFO, "On close");
    }

    @FXML
    Button openSerialPortButton;

    @FXML
    void handleOpenSerialPortButtonAction(ActionEvent event) {
        logger.log(Level.INFO, "Open serial port action");
    }

    @FXML
    void handleSmartphoneConnectedToggleButtonAction(ActionEvent event) {
        states = (byte) (states ^ 0x01);
        logFlags(states);
    }

    @FXML
    void handleHeadlightToggleButtonAction(ActionEvent event) {
        states = (byte) (states ^ 0x02);
        logFlags(states);
    }

    @FXML
    void handleCameraToggleButtonAction(ActionEvent event) {
        states = (byte) (states ^ 0x04);
        logFlags(states);
    }

    @FXML
    void handleLeftTurnSignalToggleButtonAction(ActionEvent event) {
        states = (byte) (states ^ 0x08);
        logFlags(states);
    }

    @FXML
    void handleRightTurnSignalToggleButtonAction(ActionEvent event) {
        states = (byte) (states ^ 0x10);
        logFlags(states);
    }

    @FXML
    void handleGasThrottleIdleStateToggleButtonAction(ActionEvent event) {
        states = (byte) (states ^ 0x20);
        logFlags(states);
    }

    void logFlags(byte flags) {
        logger.log(Level.INFO, String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0'));
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
}
