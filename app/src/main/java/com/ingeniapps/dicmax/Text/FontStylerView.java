package com.ingeniapps.dicmax.Text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ingeniapps.dicmax.R;

import java.util.HashMap;

/**
 * Created by Sanny on 2/18/2017.
 */

public class FontStylerView extends android.support.v7.widget.AppCompatTextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public static String fontName ="";
    public static boolean fontType =false;

    /* public FontStylerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }*/

    public FontStylerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //applyStyledFont(context, attrs);
        //applyCustomFont(context, attrs);
    }

    public FontStylerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FontStylerView );
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.FontStylerView_fontName) {
                fontName = a.getString(attr);
                applyStyledFont(fontName, context, attrs);
            }

            if (attr == R.styleable.FontStylerView_fancyText) {
                fontType = a.getBoolean(attr, false);
            //                fancyText();
            }
        }
        a.recycle();
    }

    private void applyStyledFont(String fontName, Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        Typeface customFont = selectTypeface(context, textStyle, fontName);
        setTypeface(customFont);
    }

    /*private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        Typeface customFont = selectTypeface(context, textStyle);
        setTypeface(customFont);
    }*/

    private Typeface selectTypeface(Context context, int textStyle, String fName) {
        switch (textStyle) {
            case Typeface.BOLD: // bold
                return FontCache.getTypeface(fName+".ttf", context);
              //  return FontCache.getTypeface("Lato-Bold.ttf", context);
            case Typeface.ITALIC: // medium
                return FontCache.getTypeface(fName+".ttf", context);
              //  return FontCache.getTypeface("Lato-Black.ttf", context);
            case Typeface.BOLD_ITALIC: // bold italic
              //  return FontCache.getTypeface("SourceSansPro-BoldItalic.ttf", context);
                return FontCache.getTypeface(fName+".ttf", context);
            case Typeface.NORMAL: // regular
                return FontCache.getTypeface(fName+".ttf", context);

            default:
                return FontCache.getTypeface(fName+".ttf", context);
              //  return FontCache.getTypeface("HelveticaNeue.ttf", context);
        }
    }

    private static class FontCache {
        private static HashMap<String, Typeface> fontCache = new HashMap<>();
        static Typeface getTypeface(String fontname, Context context) {
            Typeface typeface = fontCache.get(fontname);
            if (typeface == null) {
                try {
                    typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontname);
                } catch (Exception e) {
                    return null;
                }
                fontCache.put(fontname, typeface);
            }
            return typeface;
        }
    }
}