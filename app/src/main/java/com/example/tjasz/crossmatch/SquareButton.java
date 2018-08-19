package com.example.tjasz.crossmatch;

// https://stackoverflow.com/questions/24416847/how-to-force-gridview-to-generate-square-cells-in-android
import android.content.Context;
import android.util.AttributeSet;

public class SquareButton extends android.support.v7.widget.AppCompatButton {
    public SquareButton(Context context) {
        super(context);
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // This is the key that will make the height equivalent to its width
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
