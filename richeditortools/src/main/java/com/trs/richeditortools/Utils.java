package com.trs.richeditortools;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

class Utils {
    static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Show
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
}
