package com.synapticon.buckecontroller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.usb.UsbException;

public class FXMLController implements Initializable {

    AndroidDevice androidDevice;

    @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            System.out.println("You clicked me!");
            androidDevice.getWritePipe().syncSubmit("Hello World!".getBytes());
            label.setText("Hello World!");
        } catch (UsbException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AndroidOpenAccessory openAccessory = new AndroidOpenAccessory(new IdentifyingInformation(
                    "sankovicmarko.com",
                    "USBAccessoryService",
                    "USBAccessoryServiceDescription",
                    "0.0.1",
                    "httsp://usbaccessoryservice.sankovicmarko.com",
                    "USBAccessoryServiceSerial"
            ), (short) 0x18D1, (short) 0xD002, (short) 0x18D1, (short) 0x2D01);
            androidDevice = new AndroidDevice(openAccessory);
        } catch (UsbException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
