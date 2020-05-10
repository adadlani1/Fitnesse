package and.coursework.fitnesse.listeners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/*Touch Listener that checks for gestures and depending on movement of the finger, the swipe
 * loads a specific method*/
public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    protected OnSwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        /*Constants for speed and length of swipe*/
        private static final int SWIPE_THRESHOLD = 200;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                /*Finds difference between the start point and end point*/
                float diffY = motionEvent2.getY() - motionEvent1.getY();
                float diffX = motionEvent2.getX() - motionEvent1.getX();
                /*Finds if the swipe is vertical or horizontal*/
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    /*Checks if the registered swipe is fast and longer than the constants*/
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

}
