package fr.tvbarthel.intentshare;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simple view used to display a {@link TargetActivity}.
 */
class TargetActivityView extends FrameLayout {

    private ImageView icon;
    private TextView label;
    private TargetActivity model;
    private OnClickListener mInternalClickListener;
    private Listener listener;
    private int height;
    private IconLoader loader;

    /**
     * Simple view used to display a {@link TargetActivity}.
     *
     * @param context holding context.
     * @param loader  loader used to load {@link TargetActivity} icon.
     */
    public TargetActivityView(Context context, IconLoader loader) {
        super(context);
        if (!isInEditMode()) {
            initialize(context);
        }
        this.loader = loader;
    }

    /**
     * Simple view used to display a {@link TargetActivity}.
     *
     * @param context holding context.
     * @param attrs   attr from xml.
     */
    public TargetActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initialize(context);
        }
    }

    /**
     * Simple view used to display a {@link TargetActivity}.
     *
     * @param context      holding context.
     * @param attrs        attr from xml.
     * @param defStyleAttr style.
     */
    public TargetActivityView(Context context, AttributeSet attrs, int defStyleAttr) {
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
     * Set the view model.
     *
     * @param model view model.
     */
    public void setModel(TargetActivity model) {
        this.model = model;
        if (this.model != null) {
            label.setText(model.getActivityLabel());
        }
    }

    /**
     * Load the target activity icon.
     */
    public void loadIcon() {
        loader.load(model.getIconUri(), icon);
    }

    /**
     * Cancel the loading of the target activity icon.
     */
    public void cancelIconLoading() {
        loader.cancel(icon);
    }

    /**
     * Set a listener used to catch view events.
     *
     * @param listener listener to register.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Initialize internal component.
     *
     * @param context holding context.
     */
    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.isl_target_activity_view, this);

        Resources resources = context.getResources();

        height = resources.getDimensionPixelSize(R.dimen.isl_target_activity_view_height);

        int padding = resources.getDimensionPixelSize(R.dimen.isl_default_padding);
        setPadding(padding, padding, padding, padding);

        setForeground(
                ContextCompat.getDrawable(
                        context,
                        StyledAttributesUtils.getSelectableItemBackground(context)
                )
        );

        setBackgroundColor(
                ContextCompat.getColor(
                        context,
                        R.color.isl_target_activity_view_background
                )
        );

        icon = ((ImageView) findViewById(R.id.target_activity_view_icon));
        label = ((TextView) findViewById(R.id.target_activity_view_label));

        mInternalClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTargetActivitySelected(model);
                }
            }
        };
        setOnClickListener(mInternalClickListener);
    }

    /**
     * Listener used to catch view events.
     */
    public interface Listener {

        /**
         * Called when the user has chosen a target activity for his share intent.
         *
         * @param targetActivity chosen target activity.
         */
        void onTargetActivitySelected(TargetActivity targetActivity);
    }

}
