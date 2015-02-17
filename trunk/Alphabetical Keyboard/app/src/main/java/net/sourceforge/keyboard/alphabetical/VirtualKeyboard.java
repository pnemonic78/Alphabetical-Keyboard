package net.sourceforge.keyboard.alphabetical;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

/**
 * Virtual keyboard.
 */
public class VirtualKeyboard extends Keyboard {

    /**
     * Code for 'change language' key.
     */
    public static final int KEYCODE_LANGUAGE = -7;
    /**
     * Code for a settings/options key.
     */
    public static final int KEYCODE_SETTINGS = -8;
    /**
     * Code for a 'Backspace' key.
     */
    public static final int KEYCODE_BACKSPACE = KEYCODE_DELETE;
    /**
     * Code for a 'Tab' key.
     */
    public static final int KEYCODE_TAB = '\t';
    /**
     * Code for an 'Enter'/action key.
     */
    public static final int KEYCODE_ENTER = KEYCODE_DONE;
    /**
     * Code for a space key.
     */
    public static final int KEYCODE_SPACE = ' ';

    public static final int SHIFT_NONE = 0;
    public static final int SHIFT_SHIFT = 1;
    public static final int SHIFT_CAPS = 2;

    protected final Context context;
    private Key enterKey;
    private Key shiftKey;

    public VirtualKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
        this.context = context;
    }

    public VirtualKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
        this.context = context;
    }

    public VirtualKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        this.context = context;
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
        Key key = super.createKeyFromXml(res, parent, x, y, parser);
        int primaryCode = key.codes[0];
        if (primaryCode == KEYCODE_ENTER) {
            enterKey = key;
        } else if (primaryCode == KEYCODE_SHIFT) {
            shiftKey = key;
        }
        return key;
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    public void setImeOptions(int options) {
        Key key = enterKey;
        if (key == null) {
            return;
        }

        Resources res = context.getResources();

        switch (options & EditorInfo.IME_MASK_ACTION) {
            case EditorInfo.IME_ACTION_DONE:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_done);
                key.iconPreview = null;
                key.label = null;
                break;
            case EditorInfo.IME_ACTION_GO:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_go);
                key.iconPreview = null;
                key.label = null;
                break;
            case EditorInfo.IME_ACTION_NEXT:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_next);
                key.iconPreview = null;
                key.label = null;
                break;
            case EditorInfo.IME_ACTION_PREVIOUS:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_previous);
                key.iconPreview = null;
                key.label = null;
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                key.iconPreview = null;
                key.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_send);
                key.iconPreview = null;
                key.label = null;
                break;
            default:
                key.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                key.iconPreview = null;
                key.label = null;
                break;
        }
    }

    public void setShifted(int shiftState) {
        setShifted(shiftState != SHIFT_NONE);

        Resources res = context.getResources();
        shiftKey.icon = (shiftState == SHIFT_CAPS) ? res.getDrawable(R.drawable.sym_keyboard_shift_locked) : res.getDrawable(R.drawable.sym_keyboard_shift);

    }
}
