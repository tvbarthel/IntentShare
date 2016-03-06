package fr.tvbarthel.intentshare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Simple RecyclerView on which top padding could be assigned and enable touch on view under.
 */
class BottomRecyclerView extends RecyclerView {

    /**
     * Simple RecyclerView on which top padding could be assigned and enable touch on view under.
     *
     * @param context holding context.
     */
    public BottomRecyclerView(Context context) {
        super(context);
    }

    /**
     * Simple RecyclerView on which top padding could be assigned and enable touch on view under.
     *
     * @param context holding context.
     * @param attrs   attr from xml.
     */
    public BottomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Simple RecyclerView on which top padding could be assigned and enable touch on view under.
     *
     * @param context      holding context.
     * @param attrs        attr from xml.
     * @param defStyleAttr style.
     */
    public BottomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = super.onTouchEvent(ev);
        View child = getChildAt(0);
        if (child == null || ev.getY() < child.getY()) {
            handled = false;
        }
        return handled;
    }
}
