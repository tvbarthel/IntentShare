package fr.tvbarthel.intentshare;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Simple view used to display the label above the target activity list.
 */
class TargetActivityHeaderView extends TextView {

    /**
     * view height
     */
    private int height;

    /**
     * Simple view used to display the label above the target activity list.
     *
     * @param context holding context.
     */
    public TargetActivityHeaderView(Context context) {
        this(context, null);
    }

    /**
     * Simple view used to display the label above the target activity list.
     *
     * @param context holding context.
     * @param attrs   attr from xml.
     */
    public TargetActivityHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Simple view used to display the label above the target activity list.
     *
     * @param context      holding context.
     * @param attrs        attr from xml.
     * @param defStyleAttr style.
     */
    public TargetActivityHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            initialize(context);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    /**
     * Set the label which going to be displayed inside the header.
     *
     * @param label label to display inside the header.
     */
    public void setModel(String label) {
        setText(label);
    }

    /**
     * Initiliaze internal component
     *
     * @param context holding context.
     */
    private void initialize(Context context) {
        setBackgroundColor(
                ContextCompat.getColor(
                        context,
                        R.color.isl_target_activity_header_view_background
                )
        );
        Resources resources = context.getResources();
        height = resources.getDimensionPixelSize(R.dimen.isl_target_activity_view_height);
        setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimensionPixelSize(R.dimen.isl_target_activity_header_view_font_size)
        );
        setTextColor(
                ContextCompat.getColor(
                        context,
                        R.color.isl_target_activity_header_view_text_color
                )
        );
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.END);
        int padding = resources.getDimensionPixelSize(R.dimen.isl_default_padding);
        setPadding(padding, padding, padding, padding);
    }
}
