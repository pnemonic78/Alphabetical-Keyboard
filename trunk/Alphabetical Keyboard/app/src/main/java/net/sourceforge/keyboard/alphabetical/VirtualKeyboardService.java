/*
 * Copyright (c) 2015, Alphabetical Keyboard.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.keyboard.alphabetical;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

/**
 * Service for a virtual keyboard.
 */
public class VirtualKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private static final int SHIFT_NONE = VirtualKeyboard.SHIFT_NONE;
    private static final int SHIFT_SHIFT = VirtualKeyboard.SHIFT_SHIFT;
    private static final int SHIFT_CAPS = VirtualKeyboard.SHIFT_CAPS;

    private KeyboardView keyboardView;
    private VirtualKeyboard keyboard;
    private VirtualKeyboard keyboardShowing;

    private int shiftState;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();
        keyboard = new VirtualKeyboard(this, R.xml.kbd_en);
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setKeyboard(keyboard);
        return keyboardView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        keyboardShowing = keyboard;

        // Update the label on the enter key, depending on what the application
        // says it will do.
        keyboardShowing.setImeOptions(attribute.imeOptions);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        // Apply the selected keyboard to the input view.
        keyboardView.setKeyboard(keyboardShowing);
        keyboardView.closing();
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        }
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }

    private void handleShift() {
        if (keyboardView == null) {
            return;
        }

        Keyboard currentKeyboard = keyboardView.getKeyboard();
        if (keyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            keyboard.setShifted(shiftState);
            keyboardView.invalidateAllKeys();
        }
    }

    private void checkToggleCapsLock() {
        shiftState = (shiftState + 1) % 3;
    }
}
