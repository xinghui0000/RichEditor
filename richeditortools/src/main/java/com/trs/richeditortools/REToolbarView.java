package com.trs.richeditortools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;

import java.util.Arrays;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by lixinghui on 16/6/29.
 */
public class REToolbarView extends FrameLayout implements View.OnClickListener, RichEditor.OnDecorationStateListener{
    private final static ColorStateList sColorStateList;

    static {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected}, // enabled
                new int[] { -android.R.attr.state_selected}
        };

        int[] colors = new int[] {
                Color.parseColor("#359cec"),
                Color.parseColor("#484848")
        };
        sColorStateList = new ColorStateList(states, colors);
    }

    public static final int MATERIALS_DISPLAY_MODE_INLINE = 0;
    public static final int MATERIALS_DISPLAY_MODE_DRAWER = 1;

    private RichEditor mRichEditor = null;

    private ImageButton mBoldView= null;
    private ImageButton mItalicView = null;
    private ImageButton mUnderlineView = null;
    private ImageButton mOrderedListView = null;
    private ImageButton mForeColorImageButton = null;
    private Button mFontSizeImageButton = null;
    private ImageButton mAlignLeftView = null;
    private ImageButton mAlignRightView = null;
    private ImageButton mAlignCenterView = null;
    private ImageButton mAlignFullView = null;
    private ImageButton mMaterialsView = null;
    private GridLayout mMaterialLayout;

    private int mExtraItemDisplayMode;
    private List<ExtraItem> mExtraItems;

    public REToolbarView(Context context) {
        super(context);
        initView(context, null);
    }

    public REToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public REToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void extractAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.REToolbarView);
        mExtraItemDisplayMode = ta.getInt(R.styleable.REToolbarView_reExtraItemDisplayMode, 0);
        ta.recycle();
    }

    private void initView(Context context, AttributeSet set) {
        extractAttrs(context, set);
        View.inflate(context, R.layout.layout_retoolbar, this);
        (mBoldView = findViewById(R.id.retoolbar_bold)).setOnClickListener(this);
        (mItalicView = findViewById(R.id.retoolbar_italic)).setOnClickListener(this);
        (mUnderlineView = findViewById(R.id.retoolbar_under_line)).setOnClickListener(this);
        (mFontSizeImageButton = findViewById(R.id.retoolbar_font_size)).setOnClickListener(this);
        (mForeColorImageButton = findViewById(R.id.retoolbar_font_color)).setOnClickListener(this);
        (mOrderedListView = findViewById(R.id.retoolbar_ordered_list)).setOnClickListener(this);
        (mAlignLeftView = findViewById(R.id.retoolbar_justify_left)).setOnClickListener(this);
        (mAlignRightView = findViewById(R.id.retoolbar_justify_right)).setOnClickListener(this);
        (mAlignCenterView = findViewById(R.id.retoolbar_justify_center)).setOnClickListener(this);
        (mAlignFullView = findViewById(R.id.retoolbar_justify_full)).setOnClickListener(this);
        mMaterialLayout = findViewById(R.id.materials_layout);
        (mMaterialsView = findViewById(R.id.retoolbar_materials)).setOnClickListener(this);


        setTintList(mBoldView.getDrawable(), sColorStateList);
        setTintList(mItalicView.getDrawable(), sColorStateList);
        setTintList(mUnderlineView.getDrawable(), sColorStateList);
        setTintList(mOrderedListView.getDrawable(), sColorStateList);
        setTintList(mAlignLeftView.getDrawable(), sColorStateList);
        setTintList(mAlignRightView.getDrawable(), sColorStateList);
        setTintList(mAlignCenterView.getDrawable(), sColorStateList);
        setTintList(mAlignFullView.getDrawable(), sColorStateList);
        setFontSizeSelect(3);
    }

    private Drawable setTintList(Drawable d, ColorStateList color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTintList(wrappedDrawable, color);
        return wrappedDrawable;
    }

    private ImageButton createInlineItemButton(ExtraItem item) {
        ImageButton button = new AppCompatImageButton(getContext());
        button.setImageDrawable(item.srcDrawable());
        button.setBackgroundColor(Color.TRANSPARENT);
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        button.setPadding(3 * padding, padding, 3 * padding, padding);
        return button;
    }

    private Button createDrawerItemButton(ExtraItem item) {
        Button button = new AppCompatButton(getContext());
        button.setCompoundDrawables(null, item.srcDrawable(), null, null);
        button.setText(item.name());
        button.setGravity(Gravity.CENTER);
        button.setCompoundDrawablePadding((int)getResources().getDimension(R.dimen.drawer_drawable_padding));
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setOnClickListener(item.clickListener());
        return button;
    }

    public void setExtraItems(List<ExtraItem> items) {
        mExtraItems = items;
        mMaterialsView.setVisibility(mExtraItemDisplayMode == MATERIALS_DISPLAY_MODE_DRAWER && mExtraItems != null && !mExtraItems.isEmpty()? VISIBLE : GONE);
        mMaterialLayout.removeAllViews();
        if (mExtraItems != null) {
            ViewGroup vgInline = findViewById(R.id.inline_container);
            int base = 5;
            for (ExtraItem item : mExtraItems) {
                View button = createInlineItemButton(item);
                if (mExtraItemDisplayMode == MATERIALS_DISPLAY_MODE_INLINE) {
                    vgInline.addView(button, base);
                } else if (mExtraItemDisplayMode == MATERIALS_DISPLAY_MODE_DRAWER) {
                    mMaterialLayout.addView(createDrawerItemButton(item));
                }
            }
        }

    }

    public void setRichEditor(final RichEditor mRichEditor) {
        this.mRichEditor = mRichEditor;
        if (mRichEditor != null) {
            mRichEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
                @Override
                public void onStateChangeListener(String text, List<RichEditor.Type> types) {
                    if (types == null)
                        return;
                    boolean isBold = false;
                    boolean isItalic = false;
                    boolean isUnderLine = false;
                    boolean isOrderedList = false;
                    for (RichEditor.Type type : types) {
                        switch (type) {
                            case BOLD:
                                isBold = true;
                                break;
                            case ITALIC:
                                isItalic = true;
                                break;
                            case UNDERLINE:
                                isUnderLine = true;
                                break;
                            case ORDEREDLIST:
                                isOrderedList = true;
                                break;
                            case JUSTIFYCENTER:
                                setAlignSelect(R.id.retoolbar_justify_center);
                                break;
                            case JUSTIFYFULL:
                                setAlignSelect(R.id.retoolbar_justify_full);
                                break;
                            case JUSTIFYLEFT:
                                setAlignSelect(R.id.retoolbar_justify_left);
                                break;
                            case JUSTIFYRIGHT:
                                setAlignSelect(R.id.retoolbar_justify_right);
                                break;
                            case FORECOLOR:
                                //图标跟随变色功能
                                setForeColorSelect(Color.parseColor(type.getValue().toString()));
                                break;
                            case FONTSIZE:
                                setFontSizeSelect(Integer.parseInt(type.getValue().toString()) - 1);
                                break;
                            default:
                                break;
                        }
                    }
                    setBoldSelect(isBold);
                    setItalicSelect(isItalic);
                    setUnderlineSelect(isUnderLine);
                    setOrderedList(isOrderedList);
                }
            });
        }
    }

    @Override
    public void onStateChangeListener(String text, List<RichEditor.Type> types) {

    }

    private void setAlignSelect(@IdRes int id) {
        if (id == R.id.retoolbar_justify_center) {
            mAlignCenterView.setSelected(true);
            mAlignFullView.setSelected(false);
            mAlignLeftView.setSelected(false);
            mAlignRightView.setSelected(false);
        } else if (id == R.id.retoolbar_justify_full) {
            mAlignCenterView.setSelected(false);
            mAlignFullView.setSelected(true);
            mAlignLeftView.setSelected(false);
            mAlignRightView.setSelected(false);
        } else if (id == R.id.retoolbar_justify_left) {
            mAlignCenterView.setSelected(false);
            mAlignFullView.setSelected(false);
            mAlignLeftView.setSelected(true);
            mAlignRightView.setSelected(false);
        } else if (id == R.id.retoolbar_justify_right) {
            mAlignCenterView.setSelected(false);
            mAlignFullView.setSelected(false);
            mAlignLeftView.setSelected(false);
            mAlignRightView.setSelected(true);
        }
    }

    private void setBoldSelect(boolean select) {
        mBoldView.setSelected(select);
    }

    private void setItalicSelect(boolean select) {
        mItalicView.setSelected(select);
    }

    private void setUnderlineSelect(boolean select) {
        mUnderlineView.setSelected(select);
    }

    private void setOrderedList(boolean select) {
        mOrderedListView.setSelected(select);
    }

    private void setForeColorSelect(@ColorInt int color) {
        mForeColorImageButton.setTag(color);
        mForeColorImageButton.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void setFontSizeSelect(int size) {
        mFontSizeImageButton.setTag(size);
        String[] arrFontSizes = getContext().getResources().getStringArray(R.array.arrs_font_size);
        String strFontSize = arrFontSizes[size];
        mFontSizeImageButton.setText(strFontSize.substring(0, 2));
    }

    private void showFontSizeSelectDialog() {
        int size = (Integer) mFontSizeImageButton.getTag();
        Utils.hideKeyboard(getContext());
        new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle(getContext().getString(R.string.prompt_select_font_size))
                .setSingleChoiceItems(R.array.arrs_font_size, size, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setFontSizeSelect(which);
                        if (mRichEditor != null) {
                            mRichEditor.setFontSize(which);
                        }
                        dialog.dismiss();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showKeyboard(getContext());
                            }
                        });
                    }
                })
                .show();
    }

    private void showColorSelectDialog() {
        int selectColor = getResources().getColor(R.color.picker_black);
        if (mForeColorImageButton.getTag() != null) {
            selectColor = (Integer) mForeColorImageButton.getTag();
        }
        int[] colors = getResources().getIntArray(R.array.font_color);
        int iSelectColor = -1;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == selectColor) {
                iSelectColor = i;
                break;
            }
        }
        if (iSelectColor < 0 ) {
            colors = Arrays.copyOf(colors, colors.length +1);
            iSelectColor = colors.length - 1;
            colors[iSelectColor] = selectColor;
        }
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newInstance(R.string.prompt_select_font_color,
                colors, selectColor, 4, colors.length);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                if (mRichEditor != null) {
                    mRichEditor.setTextColor(color);
                }
                //图标跟随变色功能
                setForeColorSelect(color);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showKeyboard(getContext());
                    }
                }, 100);
            }
        });
        Utils.hideKeyboard(getContext());
        colorPickerDialog.show(((Activity)getContext()).getFragmentManager(), "ColorPicker");
    }

    private void onMaterialsButtonClicked(boolean selected) {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (selected) {
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        } else {
            imm.showSoftInputFromInputMethod(this.getWindowToken(), 0);
        }
        mMaterialsView.setSelected(selected);
        mMaterialLayout.setVisibility(selected? VISIBLE : GONE);
    }

    public void hideWhenViewFocused(EditText editText) {
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onMaterialsButtonClicked(false);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.retoolbar_bold) {
            if (mRichEditor != null) {
                mRichEditor.setBold();
            }
            setBoldSelect(!view.isSelected());
        } else if (id == R.id.retoolbar_italic) {
            if (mRichEditor != null) {
                mRichEditor.setItalic();
            }
            setItalicSelect(!view.isSelected());
        } else if (id == R.id.retoolbar_under_line) {
            if (mRichEditor != null) {
                mRichEditor.setUnderline();
            }
            setUnderlineSelect(!view.isSelected());
        } else if (id == R.id.retoolbar_font_size) {
            showFontSizeSelectDialog();
        } else if (id == R.id.retoolbar_ordered_list) {
            if (mRichEditor != null) {
                mRichEditor.setNumbers();
            }
            setOrderedList(!view.isSelected());
        } else if (id == R.id.retoolbar_font_color) {
            showColorSelectDialog();
        } else if (id == R.id.retoolbar_justify_left) {
            if (mRichEditor != null) {
                mRichEditor.setAlignLeft();
            }
            setAlignSelect(R.id.retoolbar_justify_left);
        } else if (id == R.id.retoolbar_justify_right) {
            if (mRichEditor != null) {
                mRichEditor.setAlignRight();
            }
            setAlignSelect(R.id.retoolbar_justify_right);
        } else if (id == R.id.retoolbar_justify_full) {
            if (mRichEditor != null) {
                mRichEditor.setAlignFull();
            }
            setAlignSelect(R.id.retoolbar_justify_full);
        } else if (id == R.id.retoolbar_materials) {
            onMaterialsButtonClicked(!view.isSelected());
        }
    }
}
