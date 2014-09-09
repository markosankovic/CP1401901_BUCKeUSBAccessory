package com.synapticon.buckeusbaccessory;

public class OnBoardControllerConstants {

    private OnBoardControllerConstants() {
        // restrict instantiation
    }

    // Commands

    public static final short NOP_COMMAND = 0x00;
    public static final short REQUEST_CLOSE_COMMAND = 0x41;
    public static final short VERIFY_CODE_COMMAND = 0x42;
    public static final short SWITCH_TO_DRIVING_MODE_COMMAND = 0x43;

    // Messages

    public static final short OBC_STATE_MESSAGE = 0x01;
    public static final short CLOSE_MESSAGE = 0x41;
    public static final short CODE_VERIFICATION_MESSAGE = 0x42;
    public static final short SWITCH_TO_DRIVING_MODE_MESSAGE = 0x43;

}
