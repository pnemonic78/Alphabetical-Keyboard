package com.android.inputmethod.latin;

import static androidx.core.view.ViewKt.updatePadding;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LatinImeActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();
        final View view = getWindow().getDecorView().findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                updatePadding(v,
                    bars.left,
                    bars.top,
                    bars.right,
                    bars.bottom
                );
                return WindowInsetsCompat.CONSUMED;
            }
        });
    }
}
