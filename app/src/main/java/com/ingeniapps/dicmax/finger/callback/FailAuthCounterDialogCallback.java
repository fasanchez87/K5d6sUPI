package com.ingeniapps.dicmax.finger.callback;


import com.ingeniapps.dicmax.finger.dialog.FingerprintDialog;

public interface FailAuthCounterDialogCallback {
    void onTryLimitReached(FingerprintDialog fingerprintDialog);
}
