package com.synapticon.buckeusbaccessory;

public interface CommandHandler {

    void handleSoftCloseCommand();

    void handleVerifyCodeCommand(byte[] bytes);

    void handleLEDUpdateCommand(byte[] bytes);
}
