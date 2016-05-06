package fr.tvbarthel.intentsharesample;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * View used to display the header.
 */
class HeaderView extends LinearLayout {

    private OnClickListener internalClickListener;
    private Listener listener;
    private String currentDialogTitle;
    private String currentSharedText;
    private String currentFacebookLink;
    private String currentTweet;
    private EditText tweetEditText;
    private String currentMailSubject;
    private String currentMailBody;

    /**
     * View used to display the header.
     *
     * @param context holding context.
     */
    public HeaderView(Context context) {
        this(context, null);
    }

    /**
     * View used to display the header.
     *
     * @param context holding context.
     * @param attrs   attrs from xml.
     */
    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * View used to display the header.
     *
     * @param context      holding context.
     * @param attrs        attrs from xml.
     * @param defStyleAttr style.
     */
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Listener used to catch view events.
     *
     * @param listener listener used to catch view events.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
        listener.onDialogTitleChanged(currentDialogTitle);
        listener.onSharedTextChanged(currentSharedText);
        listener.onFacebookLinkChanged(currentFacebookLink);
        listener.onTweetChanged(currentTweet);
        listener.onMailBodyChanged(currentMailBody);
        listener.onMailSubjectChanged(currentMailSubject);
    }

    /**
     * Initialize internal component.
     *
     * @param context holding context.
     */
    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.header_view, this);

        setOrientation(VERTICAL);

        Resources resources = context.getResources();
        int horizontalPadding = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int verticalPadding = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin);
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, 0);

        internalClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.main_header_view_learn_more_button:
                        if (listener != null) {
                            listener.onLearMoreRequested();
                        }
                        break;

                }
            }
        };

        findViewById(R.id.main_header_view_learn_more_button).setOnClickListener(internalClickListener);

        initializeDialogTitle(context);
        initializeSharedText(context);
        initializeFacebookLink(context);
        initializeTweetLink(context);
        initializeMail(context);


    }

    private void initializeMail(Context context) {
        EditText editSubject = (EditText) findViewById(R.id.main_header_view_mail_subject);
        currentMailSubject = context.getString(R.string.article_title);
        editSubject.setText(currentMailSubject);
        editSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    currentMailSubject = s.toString();
                    listener.onMailSubjectChanged(currentMailSubject);
                }
            }
        });
        EditText editBody = (EditText) findViewById(R.id.main_header_view_mail_body);
        currentMailBody = context.getString(R.string.article);
        editBody.setText(currentMailBody);
        editBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    currentMailBody = s.toString();
                    listener.onMailBodyChanged(currentMailBody);
                }
            }
        });
    }

    private void initializeTweetLink(Context context) {
        tweetEditText = (EditText) findViewById(R.id.main_header_view_twitter);
        currentTweet = context.getString(R.string.article_tweet);
        tweetEditText.setText(currentTweet);
        tweetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 140) {
                    tweetEditText.setText(s.subSequence(0, 139));
                    tweetEditText.setSelection(139);
                }
                if (listener != null) {
                    currentTweet = s.toString();
                    listener.onTweetChanged(currentTweet);
                }
            }
        });
    }

    private void initializeFacebookLink(Context context) {
        EditText editText = (EditText) findViewById(R.id.main_header_view_facebook);
        currentFacebookLink = context.getString(R.string.article_url);
        editText.setText(currentFacebookLink);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    currentFacebookLink = s.toString();
                    listener.onFacebookLinkChanged(currentFacebookLink);
                }
            }
        });
    }

    private void initializeSharedText(Context context) {
        EditText sharedText = (EditText) findViewById(R.id.main_header_view_input_text);
        currentSharedText = context.getString(R.string.default_shared_text);
        sharedText.setText(currentSharedText);
        sharedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    currentSharedText = s.toString();
                    listener.onSharedTextChanged(currentSharedText);
                }
            }
        });
    }

    private void initializeDialogTitle(Context context) {
        EditText dialogTitle = (EditText) findViewById(R.id.main_header_view_input_dialog_title);
        currentDialogTitle = context.getString(R.string.default_dialog_title);
        dialogTitle.setText(currentDialogTitle);
        dialogTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    currentDialogTitle = s.toString();
                    listener.onDialogTitleChanged(currentDialogTitle);
                }
            }
        });
    }

    /**
     * Listener used to catch view events.
     */
    public interface Listener {

        /**
         * Called when the user wants to access to more information about the library.
         */
        void onLearMoreRequested();

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
    }
}
