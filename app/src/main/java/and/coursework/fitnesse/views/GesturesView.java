package and.coursework.fitnesse.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GesturesView extends View {
    public GesturesView(Context context) {
        super(context);
    }

    public GesturesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GesturesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GesturesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        performClick();
        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
