package com.android.inputmethod.latin;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.github.inputmethod.alphabetical.BuildConfig;

import io.fabric.sdk.android.Fabric;

public class LatinApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }
}
