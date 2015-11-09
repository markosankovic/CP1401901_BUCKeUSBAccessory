package com.synapticon.buckeusbaccessory;

public interface CommandHandler {

    void handleReleaseDrivetrainCommand();

    void handleDisableDrivetrainCommand();

    void handleVerifyCodeCommand(byte[] bytes);

    void handleLEDUpdateCommand(byte[] bytes);

    void handleIncorrectChecksum(byte b, byte checksum);
}
