package com.synapticon.buckeusbaccessory;

public class OnBoardControllerConstants {

    private OnBoardControllerConstants() {
        // restrict instantiation
    }

    // Commands
    public static final byte VERIFY_CODE_COMMAND = 0x42;
    public static final byte RELEASE_DRIVETRAIN_COMMAND = 0x43;
    public static final byte DISABLE_DRIVETRAIN_COMMAND = 0x44;
    public static final byte LED_UPDATE_COMMAND = 0x45;

    // Messages
    public static final byte OBC_STATE_MESSAGE = 0x01;
    public static final byte OBC_RESPONSE_MESSAGE = 0x02;
    public static final byte OBC_TURN_SCREEN_MESSAGE = 0x06;

}
