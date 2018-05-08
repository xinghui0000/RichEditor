package com.trs.richeditor;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.trs.richeditortools.ExtraItem;
import com.trs.richeditortools.REToolbarView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RichEditor richEditor = findViewById(R.id.editor);
        REToolbarView toolbarView = findViewById(R.id.retoolbar);
        toolbarView.setRichEditor(richEditor);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        List<ExtraItem> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(new ExtraItem() {
                @Override
                public Drawable srcDrawable() {
                    return ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_bold);
                }

                @Override
                public String name() {
                    return "素材";
                }

                @Override
                public View.OnClickListener clickListener() {
                    return null;
                }
            });
        }
        toolbarView.setExtraItems(items);
    }
}
