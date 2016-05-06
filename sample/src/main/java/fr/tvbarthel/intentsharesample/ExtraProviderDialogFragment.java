package fr.tvbarthel.intentsharesample;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;

import fr.tvbarthel.intentshare.IntentShare;

/**
 * Simple dialog used to edit an extra provider.
 */
public class ExtraProviderDialogFragment extends DialogFragment {

    private static final String TAG = ExtraProviderDialogFragment.class.getSimpleName();
    private static final String EXTRA_PROVIDER = "args_extra_provider";
    private static final String EXTRA_POSITION = "args_extra_position";

    /**
     * Dummy callback used when fragment isn't attached.
     */
    private static Callback dummyCallback = new Callback() {
        @Override
        public void onExtraProviderChanged(int position, ExtraProviderWrapper extraProviderWrapper) {

        }

        @Override
        public void onExtraProviderCreated(ExtraProviderWrapper extraProviderWrapper) {

        }
    };

    /**
     * Current callback object.
     */
    private Callback callback = dummyCallback;

    /**
     * Extra provider.
     */
    private ExtraProviderWrapper extraProvider;

    /**
     * position of the extra provider.
     */
    private int extraProviderPosition;

    /**
     * View components.
     */
    private TextInputLayout appNameInputLayout;
    private TextInputLayout packageNameInputLayout;
    private EditText textEditText;
    private EditText subjectEditText;
    private CheckBox disableTextCheckBox;
    private CheckBox disableSubjectCheckBox;
    private CheckBox disableImageCheckBox;

    /**
     * Animation used to display emphasis on non valid field.
     */
    private Animation wiggle;
    private ScrollView scrollView;

    /**
     * Retrieve a new instnace to create a new extra provider.
     *
     * @return well instantiated instance.
     */
    public static ExtraProviderDialogFragment newInstance() {
        return newInstance(-1, null);
    }

    /**
     * Retrieve a new instance for editing a given extra provider.
     *
     * @param position             position of the extra provider inside the list.
     * @param extraProviderWrapper extra provider to edit.
     * @return well instantiated instance.
     */
    public static ExtraProviderDialogFragment newInstance(
            int position,
            ExtraProviderWrapper extraProviderWrapper) {
        ExtraProviderDialogFragment extraProviderDialogFragment = new ExtraProviderDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PROVIDER, extraProviderWrapper);
        args.putInt(EXTRA_POSITION, position);
        extraProviderDialogFragment.setArguments(args);
        return extraProviderDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wiggle = AnimationUtils.loadAnimation(getContext(), R.anim.wiggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callback)) {
            throw new IllegalStateException("Holding activity must implement the dialog callback.");
        }

        callback = ((Callback) activity);

        extraProviderPosition = -1;
        extraProvider = null;

        Bundle args = getArguments();
        if (args == null || !args.containsKey(EXTRA_POSITION) || !args.containsKey(EXTRA_PROVIDER)) {
            throw new IllegalStateException("Required args missing, used new instance pattern.");
        }
        extraProviderPosition = args.getInt(EXTRA_POSITION);
        extraProvider = args.getParcelable(EXTRA_PROVIDER);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = dummyCallback;
    }

    /**
     * Display the dialog to the user.
     *
     * @param fragmentManager fragment manager used to show the dialog.
     */
    public void show(FragmentManager fragmentManager) {
        this.show(fragmentManager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_extra_provider, null);

        scrollView = ((ScrollView) dialogView.findViewById(R.id.fragment_dialog_extra_provider_scroll));
        appNameInputLayout = ((TextInputLayout) dialogView.findViewById(R.id.fragment_dialog_extra_provider_app_name_input));
        packageNameInputLayout = ((TextInputLayout) dialogView.findViewById(R.id.fragment_dialog_extra_provider_package_name_input));
        textEditText = ((EditText) dialogView.findViewById(R.id.fragment_dialog_extra_provider_text));
        subjectEditText = ((EditText) dialogView.findViewById(R.id.fragment_dialog_extra_provider_subject));
        disableTextCheckBox = ((CheckBox) dialogView.findViewById(R.id.fragment_dialog_extra_provider_default_text_check));
        disableSubjectCheckBox = ((CheckBox) dialogView.findViewById(R.id.fragment_dialog_extra_provider_default_subject_check));
        disableImageCheckBox = ((CheckBox) dialogView.findViewById(R.id.fragment_dialog_extra_provider_default_image_check));

        dialogView.findViewById(R.id.fragment_dialog_extra_provider_ok).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processForm();
                    }
                }
        );

        initializeView();


        return new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();
    }

    /**
     * Initialize the dialog based on the passed extra provider.
     */
    private void initializeView() {
        if (extraProvider != null) {
            IntentShare.ExtraProvider wrappedProvider = this.extraProvider.getExtraProvider();
            appNameInputLayout.getEditText().setText(extraProvider.getAppName());
            packageNameInputLayout.getEditText().setText(wrappedProvider.getPackageName());
            textEditText.setText(wrappedProvider.getOverriddenText());
            subjectEditText.setText(wrappedProvider.getOverriddenSubject());
            disableTextCheckBox.setChecked(wrappedProvider.isTextDisabled());
            disableSubjectCheckBox.setChecked(wrappedProvider.isSubjectDisabled());
            disableImageCheckBox.setChecked(wrappedProvider.isImageDisabled());
        }
    }

    /**
     * Process the current form data.
     */
    private void processForm() {
        if (isFormValid()) {
            IntentShare.ExtraProvider extraProvider = new IntentShare.ExtraProvider(
                    packageNameInputLayout.getEditText().getText().toString()
            );

            String text = textEditText.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                extraProvider.overrideText(text);
            }

            String subject = subjectEditText.getText().toString();
            if (!TextUtils.isEmpty(subject)) {
                extraProvider.overrideSubject(subject);
            }

            if (disableTextCheckBox.isChecked()) {
                extraProvider.disableText();
            }

            if (disableSubjectCheckBox.isChecked()) {
                extraProvider.disableSubject();
            }

            if (disableImageCheckBox.isChecked()) {
                extraProvider.disableImage();
            }

            if (extraProviderPosition != -1) {
                callback.onExtraProviderChanged(
                        extraProviderPosition,
                        new ExtraProviderWrapper(
                                appNameInputLayout.getEditText().getText().toString(),
                                extraProvider
                        )
                );
            } else {
                callback.onExtraProviderCreated(new ExtraProviderWrapper(
                                appNameInputLayout.getEditText().getText().toString(),
                                extraProvider
                        )
                );
            }
            dismiss();
        }
    }

    /**
     * Used to check if the form is well field.
     *
     * @return true if the farm is valid, false otherwise.
     */
    private boolean isFormValid() {
        return isEditTextValid(appNameInputLayout) && isEditTextValid(packageNameInputLayout);
    }

    /**
     * Used to check if an {@link EditText} is not empty.
     *
     * @param inputLayout layout containing an edit text which musn't be empty.
     * @return true if the edit text is not empty.
     */
    private boolean isEditTextValid(@NonNull TextInputLayout inputLayout) {
        EditText editText = inputLayout.getEditText();
        String text = editText.getText().toString();
        boolean empty = TextUtils.isEmpty(text);
        if (empty) {
            inputLayout.startAnimation(wiggle);
            scrollView.smoothScrollTo(0, inputLayout.getTop());

        }
        return !empty;
    }

    /**
     * Callback used to catch fragment events.
     */
    public interface Callback {
        /**
         * Called when the extra provider at the given position index has changed.
         *
         * @param position             position passed.
         * @param extraProviderWrapper extra provider wrapper.
         */
        void onExtraProviderChanged(int position, ExtraProviderWrapper extraProviderWrapper);

        /**
         * Called when the user create a new extra provider.
         *
         * @param extraProviderWrapper new extra provider created.
         */
        void onExtraProviderCreated(ExtraProviderWrapper extraProviderWrapper);
    }
}
