package fr.tvbarthel.intentsharesample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * View used to display an {@link ExtraProviderWrapper} list entry.
 */
class ExtraProviderWrapperView extends FrameLayout {

    private TextView labelView;
    private Listener listener;
    private ExtraProviderWrapper extraProviderWrapper;

    /**
     * View used to display an extra provider list entry.
     *
     * @param context holding context.
     */
    public ExtraProviderWrapperView(Context context) {
        this(context, null);
    }

    /**
     * View used to display an extra provider list entry.
     *
     * @param context holding context.
     * @param attrs   attrs from xml.
     */
    public ExtraProviderWrapperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * View used to display an extra provider list entry.
     *
     * @param context      holding context.
     * @param attrs        attrs from xml.
     * @param defStyleAttr style.
     */
    public ExtraProviderWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Set the extra provider which should be presented to the user.
     *
     * @param extraProviderWrapper wrapper to display to the user.
     */
    public void presentData(ExtraProviderWrapper extraProviderWrapper) {
        this.extraProviderWrapper = extraProviderWrapper;
        labelView.setText(extraProviderWrapper.getAppName());
    }

    /**
     * Set a listener to catch view events.
     *
     * @param listener listener used to catch view events.
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
        LayoutInflater.from(context).inflate(R.layout.extra_provider_view, this);
        labelView = ((TextView) findViewById(R.id.extra_provider_view_label));
        labelView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExtraProviderDetailRequested(extraProviderWrapper);
                }
            }
        });
    }

    /**
     * Interface used to catch view events.
     */
    public interface Listener {

        /**
         * Called when the user wants to access to more detail for a given extra provider.
         */
        void onExtraProviderDetailRequested(ExtraProviderWrapper wrapper);
    }
}
