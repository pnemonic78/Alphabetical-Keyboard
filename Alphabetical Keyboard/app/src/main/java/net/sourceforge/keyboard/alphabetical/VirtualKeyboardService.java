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
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

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

    private StringBuilder composing = new StringBuilder();
    private int shiftState;
    private long metaState;

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

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        composing.setLength(0);

        if (!restarting) {
            // Clear shift states.
            metaState = 0L;
        }

        // For all unknown input types, default to the alphabetic
        // keyboard with no special features.
        keyboardShowing = keyboard;
        updateShiftKeyState(attribute);

        // Update the label on the enter key, depending on what the application
        // says it will do.
        keyboardShowing.setImeOptions(attribute.imeOptions);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        composing.setLength(0);
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

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (composing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            composing.setLength(0);
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && keyboardView != null) {
                    if (keyboardView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (composing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;

            default:
                break;
        }

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
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
        }else if (!isAlphabet(primaryCode)) {
            // Handle separator
            if (composing.length() > 0) {
                commitTyped(getCurrentInputConnection());
            }
            handleCharacter(primaryCode, keyCodes);
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (composing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
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

    private void handleBackspace() {
        final int length = composing.length();
        if (length > 1) {
            composing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(composing, 1);
        } else if (length > 0) {
            composing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
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

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (keyboardShowing.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (isAlphabet(primaryCode)) {
            composing.appendCodePoint(primaryCode);
            getCurrentInputConnection().setComposingText(composing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else {
            sendKey(primaryCode);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        keyboardView.closing();
    }

    private void checkToggleCapsLock() {
        shiftState = (shiftState + 1) % 3;
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (composing.length() > 0) {
            inputConnection.commitText(composing, composing.length());
            composing.setLength(0);
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if ((attr != null) && (keyboardView != null) && (keyboard == keyboardView.getKeyboard())) {
            int caps = InputType.TYPE_NULL;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            if ((shiftState == SHIFT_NONE) && (caps != InputType.TYPE_NULL)) {
                shiftState = SHIFT_SHIFT;
                keyboard.setShifted(shiftState);
                keyboardView.invalidateAllKeys();
            } else if ((shiftState == SHIFT_SHIFT) && (caps == InputType.TYPE_NULL)) {
                shiftState = SHIFT_NONE;
                keyboard.setShifted(shiftState);
                keyboardView.invalidateAllKeys();
            }
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        return Character.isLetter(code);
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        InputConnection ic = getCurrentInputConnection();
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        if (keyCode < 0)
            return;

        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp((keyCode - '0') + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf(Character.toChars(keyCode)), 1);
                }
                break;
        }
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        metaState = MetaKeyKeyListener.handleKeyDown(metaState, keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(metaState));
        metaState = MetaKeyKeyListener.adjustMetaAfterKeypress(metaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (composing.length() > 0) {
            char accent = composing.charAt(composing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                composing.setLength(composing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }
}
