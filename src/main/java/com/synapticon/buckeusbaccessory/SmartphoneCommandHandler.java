package com.synapticon.buckeusbaccessory;

import java.io.ByteArrayOutputStream;

public class SmartphoneCommandHandler {

    private final CommandHandler handler;
    private ReadState state;
    private ByteArrayOutputStream payload;
    private byte command;
    private byte checksum;

    enum ReadState {

        HEADER_0,
        HEADER_1,
        COMMAND,
        PAYLOAD,
        CHECKSUM
    }

    public SmartphoneCommandHandler(CommandHandler handler) {
        this.handler = handler;
        state = ReadState.HEADER_0;
    }

    public void command(byte b) {
        switch (state) {
            case HEADER_0:
                if (b == (byte) 0xAA) {
                    state = ReadState.HEADER_1;
                }
                break;
            case HEADER_1:
                if (b == (byte) 0x55) {
                    state = ReadState.COMMAND;
                } else {
                    state = ReadState.HEADER_0;
                }
                break;
            case COMMAND:
                payload = new ByteArrayOutputStream();
                command = checksum = b; // start checksum with a command byte
                switch (command) {
                    case OnBoardControllerConstants.SOFT_CLOSE_COMMAND:
                        state = ReadState.CHECKSUM;
                    case OnBoardControllerConstants.VERIFY_CODE_COMMAND:
                    case OnBoardControllerConstants.LED_UPDATE_COMMAND:
                        state = ReadState.PAYLOAD;
                        break;
                    default:
                        state = ReadState.HEADER_0; // command not recognized
                }
                break;
            case PAYLOAD:
                payload.write(b); // add byte to payload
                checksum ^= b; // XOR checksum with payload byte
                switch (command) {
                    case OnBoardControllerConstants.VERIFY_CODE_COMMAND:
                        if (payload.size() == 6) { // verify code length
                            state = ReadState.CHECKSUM;
                        }
                        break;
                    case OnBoardControllerConstants.LED_UPDATE_COMMAND:
                        if (payload.size() == 225) { // led length
                            state = ReadState.CHECKSUM;
                        }
                        break;
                }
                break;
            case CHECKSUM:
                if (b == checksum) {
                    switch (command) {
                        case OnBoardControllerConstants.SOFT_CLOSE_COMMAND:
                            handler.handleSoftCloseCommand();
                        case OnBoardControllerConstants.VERIFY_CODE_COMMAND:
                            handler.handleVerifyCodeCommand(payload.toByteArray());
                            break;
                        case OnBoardControllerConstants.LED_UPDATE_COMMAND:
                            handler.handleLEDUpdateCommand(payload.toByteArray());
                            break;
                    }
                } else {
                    handler.handleIncorrectChecksum(b, checksum);
                }
                state = ReadState.HEADER_0; // start from beginning
            default:
                state = ReadState.HEADER_0;
        }
    }
}
