package com.synapticon.buckeusbaccessory;

public class OnBoardControllerConstants {

    private OnBoardControllerConstants() {
        // restrict instantiation
    }

    // Commands

    public static final byte NOP_COMMAND = 0x00;
    public static final byte SOFT_CLOSE_COMMAND = 0x41;
    public static final byte VERIFY_CODE_COMMAND = 0x42;
    public static final byte RELEASE_DRIVETRAIN_COMMAND = 0x43;
    public static final byte DISABLE_DRIVETRAIN_COMMAND = 0x44;
    public static final byte LED_UPDATE_COMMAND = 0x45;

    // Messages

    public static final byte OBC_STATE_MESSAGE = 0x01;
    public static final byte SOFT_CLOSE_MESSAGE = 0x41;
    public static final byte CODE_VERIFICATION_MESSAGE = 0x42;
    public static final byte RELEASE_DRIVETRAIN_MESSAGE = 0x43;
    public static final byte DISABLE_DRIVETRAIN_MESSAGE = 0x44;

}
