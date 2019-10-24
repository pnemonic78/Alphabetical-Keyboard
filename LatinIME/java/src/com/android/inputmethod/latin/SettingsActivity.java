/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.inputmethod.latin;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class SettingsActivity extends PreferenceActivity {
    private static final String DEFAULT_FRAGMENT = Settings.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public Intent getIntent() {
        final Intent intent = super.getIntent();
        if (!intent.hasExtra(EXTRA_SHOW_FRAGMENT)) {
            intent.putExtra(EXTRA_SHOW_FRAGMENT, DEFAULT_FRAGMENT);
        }
        intent.putExtra(EXTRA_NO_HEADERS, true);
        return intent;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.startsWith("com.android.inputmethod.latin");
    }
}
