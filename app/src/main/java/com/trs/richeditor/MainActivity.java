package com.trs.richeditor;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
        richEditor.setPlaceholder("ddfadfa");
        toolbarView.setRichEditor(richEditor);

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
