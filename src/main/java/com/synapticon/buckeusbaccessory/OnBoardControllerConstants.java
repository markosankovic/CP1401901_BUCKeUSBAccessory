package com.synapticon.buckeusbaccessory;

public class OnBoardControllerConstants {

    private OnBoardControllerConstants() {
        // restrict instantiation
    }

    // Commands

    public static final short NOP_COMMAND = 0x00;
    public static final short SOFT_CLOSE_COMMAND = 0x41;
    public static final short VERIFY_CODE_COMMAND = 0x42;
    public static final short RELEASE_DRIVETRAIN_COMMAND = 0x43;
    public static final short DISABLE_DRIVETRAIN_COMMAND = 0x44;
    public static final short LED_UPDATE_COMMAND = 0x45;

    // Messages

    public static final short OBC_STATE_MESSAGE = 0x01;
    public static final short SOFT_CLOSE_MESSAGE = 0x41;
    public static final short CODE_VERIFICATION_MESSAGE = 0x42;
    public static final short RELEASE_DRIVETRAIN_MESSAGE = 0x43;
    public static final short DISABLE_DRIVETRAIN_MESSAGE = 0x44;

}
