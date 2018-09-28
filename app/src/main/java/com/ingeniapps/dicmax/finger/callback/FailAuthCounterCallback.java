package com.ingeniapps.dicmax.finger.callback;

import com.ingeniapps.dicmax.finger.view.Fingerprint;

/**
 * Created by Omar on 10/07/2017.
 */

public interface FailAuthCounterCallback {
    void onTryLimitReached(Fingerprint fingerprint);
}
