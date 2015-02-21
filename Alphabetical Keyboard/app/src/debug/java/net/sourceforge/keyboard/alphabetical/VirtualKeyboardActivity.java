package net.sourceforge.keyboard.alphabetical;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

/**
 * Activity to show a keyboard view.
 */
public class VirtualKeyboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Keyboard kbd = new VirtualKeyboard(this, R.xml.kbd_en);
        KeyboardView view = new KeyboardView(this, null);
        view.setKeyboard(kbd);
        setContentView(view);
    }
}
