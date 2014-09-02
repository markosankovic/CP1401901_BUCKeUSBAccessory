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
    Thread readThread;
    Thread speedThread;

    @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        androidDevice.sendCommand((byte) 0x41, (byte) 0, new byte[]{(byte) Utils.randInt(0, 50)});
        label.setText("Hello World!");
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

            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            byte[] data = new byte[16384];
                            int received = androidDevice.getReadPipe().syncSubmit(data);

                            if (data[0] == 0x18) {
                                System.out.println("Close request initiated. Send 0x19 to the Android device.");
                                androidDevice.sendCommand((byte) 0x19, (byte) 0, new byte[]{});
                            }

                            System.out.println(received + " bytes received");
                            Thread.sleep(1000);
                        } catch (InterruptedException | UsbException ex) {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            break;
                        }
                    }
                }
            });

            readThread.start();

            speedThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            androidDevice.sendCommand((byte) 0x41, (byte) 0, new byte[]{(byte) Utils.randInt(0, 50)});
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            break;
                        }
                    }
                }
            });

            // speedThread.start();

        } catch (UsbException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
