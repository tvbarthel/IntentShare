package fr.tvbarthel.intentsharesample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Footer view of the list.
 */
class FooterView extends FrameLayout {

    private Listener listener;

    /**
     * Footer view of the list.
     *
     * @param context holding context.
     */
    public FooterView(Context context) {
        this(context, null);
    }

    /**
     * Footer view of the list.
     *
     * @param context holding context.
     * @param attrs   attrs from xml.
     */
    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Footer view of the list.
     *
     * @param context      holding context.
     * @param attrs        attrs from xml.
     * @param defStyleAttr style.
     */
    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Listener used to catch view events.
     *
     * @param listener Listener used to catch view events.
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
        LayoutInflater.from(context).inflate(R.layout.footer_view, this);
        findViewById(R.id.footer_view_add).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddExtraProviderRequested();
                }
            }
        });
    }

    /**
     * Listener used to catch view events.
     */
    public interface Listener {
        /**
         * Called when the user wants to add an extra provider.
         */
        void onAddExtraProviderRequested();
    }

}
