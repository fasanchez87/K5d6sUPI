package com.ingeniapps.dicmax.finger.callback;

/**
 * Created by Omar on 10/07/2017.
 */

public interface PasswordCallback {
    void onPasswordSucceeded();
    boolean onPasswordCheck(String password);
    void onPasswordCancel();
}