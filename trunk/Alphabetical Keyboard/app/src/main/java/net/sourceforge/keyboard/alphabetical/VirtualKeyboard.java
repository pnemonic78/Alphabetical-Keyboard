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
     * Code for a 'Shift'/'Caps Lock' key.
     */
    public static final int KEY_SHIFT = -1;
    /**
     * Code for a 'show symbols' key.
     */
    public static final int KEY_SYMBOLS = -2;
    /**
     * Code for 'change language' key.
     */
    public static final int KEY_LANGUAGE = -3;
    /**
     * Code for a settings/options key.
     */
    public static final int KEY_SETTINGS = -4;
    /**
     * Code for a 'Backspace'/'Delete' key.
     */
    public static final int KEY_BACKSPACE = 8;
    /**
     * Code for a 'Tab' key.
     */
    public static final int KEY_TAB = '\t';
    /**
     * Code for an 'Enter'/action key.
     */
    public static final int KEY_ENTER = '\n';
    /**
     * Code for an 'Enter'/action key.
     */
    public static final int KEY_SPACE = ' ';

    private Key enterKey;

    public VirtualKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public VirtualKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public VirtualKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
        Key key = super.createKeyFromXml(res, parent, x, y, parser);
        if (key.codes[0] == KEY_ENTER) {
            enterKey = key;
        }
        return key;
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    public void setImeOptions(Resources res, int options) {
        Key key = enterKey;
        if (key == null) {
            return;
        }

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
}
