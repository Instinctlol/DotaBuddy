package jwwu.com.dotabuddy.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Instinctlol on 30.09.2015.
 */
public class SquareProgressBar extends ProgressBar {
    public SquareProgressBar(Context context) {
        super(context);
    }

    public SquareProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
