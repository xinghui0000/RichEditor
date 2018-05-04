package com.trs.richeditortools;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface ExtraItem {
    Drawable srcDrawable();

    String name();

    View.OnClickListener clickListener();
}
