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
        if (key.codes[0] == '\n') {
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
        System.out.println("~!@ options& " + Integer.toHexString(options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)));

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
