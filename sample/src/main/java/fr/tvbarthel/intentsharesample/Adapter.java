package fr.tvbarthel.intentsharesample;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Simple adapter used to display the main content.
 */
class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private static final int VIEW_TYPE_EXTRA_PROVIDER = 3;
    private static final int HEADER_COUNT = 1;
    private static final int FOOTER_COUNT = 1;

    private final ArrayList<ExtraProviderWrapper> extraProviders;

    private HeaderView.Listener internalHeaderListener;
    private FooterView.Listener internalFooterListener;
    private ExtraProviderWrapperView.Listener internalExtraProviderViewListener;
    private Listener listener;

    /**
     * Simple adapter used to display the main content.
     *
     * @param extraProviders extra providers which must be listed for the user.
     */
    public Adapter(ArrayList<ExtraProviderWrapper> extraProviders) {
        this.extraProviders = extraProviders;
        internalHeaderListener = new HeaderView.Listener() {
            @Override
            public void onLearMoreRequested() {
                if (listener != null) {
                    listener.onLearMoreRequested();
                }
            }

            @Override
            public void onDialogTitleChanged(String dialogTitle) {
                if (listener != null) {
                    listener.onDialogTitleChanged(dialogTitle);
                }
            }

            @Override
            public void onSharedTextChanged(String sharedText) {
                if (listener != null) {
                    listener.onSharedTextChanged(sharedText);
                }
            }

            @Override
            public void onFacebookLinkChanged(String currentFacebookLink) {
                if (listener != null) {
                    listener.onFacebookLinkChanged(currentFacebookLink);
                }
            }

            @Override
            public void onTweetChanged(String currentTweet) {
                if (listener != null) {
                    listener.onTweetChanged(currentTweet);
                }
            }

            @Override
            public void onMailSubjectChanged(String currentMailSubject) {
                if (listener != null) {
                    listener.onMailSubjectChanged(currentMailSubject);
                }
            }

            @Override
            public void onMailBodyChanged(String currentMailBody) {
                if (listener != null) {
                    listener.onMailBodyChanged(currentMailBody);
                }
            }
        };
        internalFooterListener = new FooterView.Listener() {
            @Override
            public void onAddExtraProviderRequested() {
                if (listener != null) {
                    listener.onAddExtraProviderRequested();
                }
            }
        };
        internalExtraProviderViewListener = new ExtraProviderWrapperView.Listener() {
            @Override
            public void onExtraProviderDetailRequested(ExtraProviderWrapper wrapper) {
                if (listener != null) {
                    int wrapperPosition = Adapter.this.extraProviders.indexOf(wrapper);
                    if (wrapperPosition == -1) {
                        Log.e("LARGONNE", "wrapper position -1 : " + wrapper);
                    }
                    listener.onExtraProviderDetailRequested(wrapperPosition, wrapper);
                }
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                HeaderView headerView = new HeaderView(parent.getContext());
                headerView.setListener(internalHeaderListener);
                headerView.setLayoutParams(
                        new RecyclerView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );
                return new ViewHolder(headerView);
            case VIEW_TYPE_FOOTER:
                FooterView footerView = new FooterView(parent.getContext());
                footerView.setListener(internalFooterListener);
                footerView.setLayoutParams(
                        new RecyclerView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );
                return new ViewHolder(footerView);
            default:
                ExtraProviderWrapperView extraProviderView
                        = new ExtraProviderWrapperView(parent.getContext());
                extraProviderView.setListener(internalExtraProviderViewListener);
                extraProviderView.setLayoutParams(
                        new RecyclerView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );
                return new ViewHolder(extraProviderView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_EXTRA_PROVIDER) {
            ((ExtraProviderWrapperView) holder.itemView).presentData(extraProviders.get(position - HEADER_COUNT));
        }
    }

    @Override
    public int getItemCount() {
        return extraProviders.size() + HEADER_COUNT + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_EXTRA_PROVIDER;
        }
    }

    /**
     * Listener used to catch adapted views events.
     *
     * @param listener Listener used to catch adapted views events.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Notify the adapter that an extra provider has been inserted at the end of the list.
     */
    public void notifyExtraProviderInserted() {
        notifyItemInserted(extraProviders.size() - 1 + HEADER_COUNT);
    }

    /**
     * Notify the adapter that an extra provider has changed.
     *
     * @param position position of the extra provider in the list.
     */
    public void notifyExtraProviderChanged(int position) {
        notifyItemChanged(position + HEADER_COUNT);
    }

    /**
     * View holder pattern.
     */
    public final class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * View holder pattern.
         *
         * @param itemView view to hold.
         */
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Listener used to catch adapted views events.
     */
    public interface Listener {

        /**
         * Called when the user wants to access to more information about the library.
         */
        void onLearMoreRequested();

        /**
         * Called when the user wants to add an extra provider.
         */
        void onAddExtraProviderRequested();

        /**
         * Called when the user update the dialog title.
         */
        void onDialogTitleChanged(String dialogTitle);

        /**
         * Called when the user update the text to share.
         *
         * @param sharedText text to share.
         */
        void onSharedTextChanged(String sharedText);

        /**
         * Called when the user change the link he wants to share on facebook.
         *
         * @param currentFacebookLink facebook link.
         */
        void onFacebookLinkChanged(String currentFacebookLink);

        /**
         * Called when the user change the content of the tweet.
         *
         * @param currentTweet new tweet body.
         */
        void onTweetChanged(String currentTweet);

        /**
         * Called when the user change the mail subject.
         *
         * @param currentMailSubject new mail subject.
         */
        void onMailSubjectChanged(String currentMailSubject);

        /**
         * Called when the user change the mail body.
         *
         * @param currentMailBody new mail body.
         */
        void onMailBodyChanged(String currentMailBody);

        /**
         * Called when the user wants to access to more detail for a given extra provider.
         */
        void onExtraProviderDetailRequested(int wrapperPosition, ExtraProviderWrapper wrapper);
    }
}
