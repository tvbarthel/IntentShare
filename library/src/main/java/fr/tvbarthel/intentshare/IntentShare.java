package fr.tvbarthel.intentshare;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link IntentShare} is designed to enhance the sharing experience by allowing to share
 * different content according to the application the user will choose to share with.
 */
public final class IntentShare implements Parcelable {

    /**
     * Parcelable.
     */
    public static final Parcelable.Creator<IntentShare> CREATOR = new Parcelable.Creator<IntentShare>() {
        public IntentShare createFromParcel(Parcel source) {
            return new IntentShare(source);
        }

        public IntentShare[] newArray(int size) {
            return new IntentShare[size];
        }
    };

    /**
     * Facebook package.
     */
    public static final String FACEBOOK = "com.facebook.katana";

    /**
     * Twitter package.
     */
    public static final String TWITTER = "com.twitter.android";

    /**
     * Text which will be shared by default.
     */
    String text;

    /**
     * Uri of the image add as {@link android.content.Intent#EXTRA_STREAM}
     */
    Uri imageUri;

    /**
     * Text used as mail body if the targeted application is a mail one.
     */
    String mailBody;

    /**
     * Text used as mail subject if the targeted application is a mail one.
     */
    String mailSubject;

    /**
     * Specific extra provider for some package.
     */
    ArrayList<ExtraProvider> extraProviders;

    /**
     * Keep a track on package with a specific
     * {@link fr.tvbarthel.intentshare.IntentShare.ExtraProvider} in order to warn the user when
     * two provider are added for the same package.
     */
    List<String> packageWithExtraProvider;


    /**
     * Icon loader used to load icons.
     */
    IconLoader iconLoader;

    /**
     * Title that will be displayed in the chooser.
     */
    String chooserTitle;

    /**
     * Context used to start the activity used to choose a target one.
     */
    private Context context;
    private IntentShareListener listener;

    /**
     * {@link IntentShare} is designed to enhance the sharing experience by allowing to share
     * different content according to the application the user will choose to share with.
     *
     * @param context context used send the {@link android.content.Intent}
     */
    private IntentShare(Context context) {
        this.context = context;
        extraProviders = new ArrayList<>();
        packageWithExtraProvider = new ArrayList<>();
        this.listener = null;
        this.iconLoader = new AsyncIconLoader();
        this.chooserTitle = context.getString(R.string.isl_default_sharing_label);
    }

    /**
     * {@link IntentShare} is designed to enhance the sharing experience by allowing to share
     * different content according to the application the user will choose to share with.
     *
     * @param in parcel.
     */
    protected IntentShare(Parcel in) {
        this.text = in.readString();
        this.imageUri = in.readParcelable(Uri.class.getClassLoader());
        this.mailBody = in.readString();
        this.mailSubject = in.readString();
        this.extraProviders = in.createTypedArrayList(ExtraProvider.CREATOR);
        this.iconLoader = in.readParcelable(IconLoader.class.getClassLoader());
        this.chooserTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeParcelable(this.imageUri, 0);
        dest.writeString(this.mailBody);
        dest.writeString(this.mailSubject);
        dest.writeTypedList(this.extraProviders);
        dest.writeParcelable(this.iconLoader, flags);
        dest.writeString(this.chooserTitle);
    }

    /**
     * {@link IntentShare} is designed to enhance the sharing experience by allowing to share
     * different content according to the application the user will choose to share with.
     *
     * @param context context from which the sharing is initiated.
     * @return new instance.
     */
    @NonNull
    public static IntentShare with(@NonNull Context context) {
        return new IntentShare(context);
    }

    /**
     * Title that will be displayed in the chooser.
     * <p/>
     * Will be displayed on a single line.
     *
     * @param title title that will be displayed in the chooser.
     * @return current {@link IntentShare} for method chaining.
     */
    public IntentShare chooserTitle(@NonNull String title) {
        this.chooserTitle = title;
        return this;
    }

    /**
     * Set the {@link IconLoader} used to load target activities icon.
     *
     * @param iconLoader icon loader.
     * @return current {@link IntentShare} for method chaining.
     */
    public IntentShare iconLoader(@NonNull IconLoader iconLoader) {
        this.iconLoader = iconLoader;
        return this;
    }

    /**
     * Text which will be shared.
     * <p/>
     * Will be used as {@link android.content.Intent#EXTRA_TEXT}
     *
     * @param text text to share.
     * @return current {@link IntentShare} for method chaining.
     */
    @NonNull
    public IntentShare text(@NonNull String text) {
        this.text = text;
        return this;
    }

    /**
     * Image which will be set as {@link android.content.Intent#EXTRA_STREAM} if the
     * target application can handle it.
     *
     * @param imageUri Uri of the image.
     * @return current {@link IntentShare} for method chaining.
     */
    @NonNull
    public IntentShare image(@NonNull Uri imageUri) {
        String lastPathSegment = imageUri.getLastPathSegment();
        if (!lastPathSegment.endsWith(".png") && !lastPathSegment.endsWith(".jpg")) {
            throw new IllegalArgumentException("Invalid image uri : only .png and .jpg file supported : "
                    + imageUri);
        } else if (!"content".equals(imageUri.getScheme())) {
            throw new IllegalArgumentException("Invalid image uri : only content scheme supported : "
                    + imageUri);
        } else {
            this.imageUri = imageUri;
        }
        return this;
    }

    /**
     * Text which will be shared by any application marked as mail client
     * (application which can handle "message/rfc822").
     * <p/>
     * Override {@link IntentShare#text(String)} for every mail application.
     *
     * @param mailBody text set as text body for mail application.
     * @return current {@link IntentShare} for method chaining.
     */
    @NonNull
    public IntentShare mailBody(@NonNull String mailBody) {
        this.mailBody = mailBody;
        return this;
    }

    /**
     * Text which will be displayed inside the subject field of any application marked as mail
     * client (applcation which can handle "message/rfc822").
     * <p/>
     * See also : {@link IntentShare#mailBody(String)}
     *
     * @param mailSubject subject of the mail.
     * @return current {@link IntentShare} for method chaining.
     */
    @NonNull
    public IntentShare mailSubject(@NonNull String mailSubject) {
        this.mailSubject = mailSubject;
        return this;
    }

    /**
     * Link which will be used as single shared content if target application is the facebook
     * since facebook app don't handle {@link android.content.Intent#EXTRA_TEXT}
     * <p/>
     * Override {@link IntentShare#text(String)} for the Facebook application.
     *
     * @param link link to the content.
     * @return current {@link IntentShare} for method chaining.
     */
    @NonNull
    public IntentShare facebookBody(@NonNull Uri link) {
        String scheme = link.getScheme();
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new IllegalArgumentException("Invalid facebook link : un handled scheme : "
                    + scheme);
        } else if (packageWithExtraProvider.contains(IntentShare.FACEBOOK)) {
            throw new IllegalArgumentException("Facebook link can only be set once.");
        } else {
            packageWithExtraProvider.add(IntentShare.FACEBOOK);
            extraProviders.add(
                    new ExtraProvider(IntentShare.FACEBOOK)
                            .overrideText(link.toString())
                            .disableImage()
                            .disableSubject()
            );
        }
        return this;
    }

    /**
     * Text which will be used as tweet body if target application is the Twitter one.
     * <p/>
     * Override {@link IntentShare#text(String)} for the Twitter application.
     *
     * @param tweet tweet body.
     * @return current {@link IntentShare} for method chaining.
     */
    @NonNull
    public IntentShare twitterBody(@NonNull String tweet) {
        if (tweet.length() > 140) {
            throw new IllegalArgumentException("Invalid tweet content : "
                    + "exceed the 140-Character limit : " + tweet);
        } else if (packageWithExtraProvider.contains(IntentShare.TWITTER)) {
            throw new IllegalArgumentException("Twitter body can only be set once.");
        } else {
            packageWithExtraProvider.add(IntentShare.TWITTER);
            extraProviders.add(
                    new ExtraProvider(TWITTER)
                            .overrideText(tweet)
            );
        }
        return this;
    }

    /**
     * Allow to set a listener to be notified on chosen target activity.
     * <p/>
     * Listener will be automatically unregister once a result is delivered.
     *
     * @param listener listener to register.
     * @return current {@link IntentShare} for method chaining.
     */
    public IntentShare listener(@NonNull IntentShareListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Allow to add a specific intent for a given
     *
     * @param extraProvider extra provider for a given package;
     * @return current {@link IntentShare} for method chaining.
     */
    public IntentShare addExtraProvider(@NonNull ExtraProvider extraProvider) {
        if (extraProvider == null) {
            throw new IllegalArgumentException("Extra provider can't be null");
        } else if (packageWithExtraProvider.contains(extraProvider.packageName)) {
            throw new IllegalArgumentException("Extra provider already provided for the package : "
                    + extraProvider.packageName);
        }
        extraProviders.add(extraProvider);
        return this;
    }

    /**
     * Deliver the intent to the system.
     * <p/>
     * This will lead to the display of every applications that can handle the build intent.
     * Target activity field will be then filled according to the params.
     */
    public void deliver() {
        if (this.listener != null) {
            this.listener.register(context);
        }
        TargetChooserActivity.start(context, this);
    }

    /**
     * Used to provide specific extras for a given package name
     */
    public static class ExtraProvider implements Parcelable {

        /**
         * Parcelable.
         */
        public static final Creator<ExtraProvider> CREATOR = new Creator<ExtraProvider>() {
            @Override
            public ExtraProvider createFromParcel(Parcel source) {
                return new ExtraProvider(source);
            }

            @Override
            public ExtraProvider[] newArray(int size) {
                return new ExtraProvider[size];
            }
        };
        /**
         * Package for which specific extras must be provided.
         */
        String packageName;

        /**
         * Specific text to provide.
         */
        String overriddenText;

        /**
         * Specific mail subject to provide.
         */
        String overriddenSubject;

        /**
         * Specific image to provide.
         */
        Uri overriddenImage;

        /**
         * Used to know if default text must be removed.
         */
        boolean textDisabled;

        /**
         * Used to know if default subject must be removed.
         */
        boolean subjectDisabled;

        /**
         * Used to know if default image must be removed.
         */
        boolean imageDisabled;

        /**
         * Used to provide specific extras for a given package name.
         * <p/>
         * By default, every extras are going to be copied from the {@link IntentShare}.
         * <p/>
         * To override extras :
         * {@link ExtraProvider#overrideText(String)}
         * {@link ExtraProvider#overrideSubject(String)}
         * {@link ExtraProvider#overrideImage(Uri)}
         *
         * @param packageName package for which the extras are going to be applied;
         */
        public ExtraProvider(String packageName) {
            this.packageName = packageName;
            this.overriddenText = null;
            this.overriddenSubject = null;
            this.overriddenImage = null;
            this.textDisabled = false;
            this.subjectDisabled = false;
            this.imageDisabled = false;
        }

        /**
         * Used to provide specific extras for a given package name.
         * <p/>
         * By default, every extras are going to be copied from the {@link IntentShare}.
         * <p/>
         * To override extras :
         * {@link ExtraProvider#overrideText(String)}
         * {@link ExtraProvider#overrideSubject(String)}
         * {@link ExtraProvider#overrideImage(Uri)}
         *
         * @param in parcel.
         */
        protected ExtraProvider(Parcel in) {
            this.packageName = in.readString();
            this.overriddenText = in.readString();
            this.overriddenSubject = in.readString();
            this.overriddenImage = in.readParcelable(Uri.class.getClassLoader());
            this.textDisabled = in.readByte() != 0;
            this.subjectDisabled = in.readByte() != 0;
            this.imageDisabled = in.readByte() != 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ExtraProvider that = (ExtraProvider) o;

            if (textDisabled != that.textDisabled) {
                return false;
            }
            if (subjectDisabled != that.subjectDisabled) {
                return false;
            }
            if (imageDisabled != that.imageDisabled) {
                return false;
            }
            if (packageName != null ? !packageName.equals(that.packageName)
                    : that.packageName != null) {
                return false;
            }
            if (overriddenText != null ? !overriddenText.equals(that.overriddenText)
                    : that.overriddenText != null) {
                return false;
            }
            if (overriddenSubject != null ? !overriddenSubject.equals(that.overriddenSubject)
                    : that.overriddenSubject != null) {
                return false;
            }
            return !(overriddenImage != null ? !overriddenImage.equals(that.overriddenImage)
                    : that.overriddenImage != null);

        }

        @Override
        public int hashCode() {
            int result = packageName != null ? packageName.hashCode() : 0;
            result = 31 * result + (overriddenText != null ? overriddenText.hashCode() : 0);
            result = 31 * result + (overriddenSubject != null ? overriddenSubject.hashCode() : 0);
            result = 31 * result + (overriddenImage != null ? overriddenImage.hashCode() : 0);
            result = 31 * result + (textDisabled ? 1 : 0);
            result = 31 * result + (subjectDisabled ? 1 : 0);
            result = 31 * result + (imageDisabled ? 1 : 0);
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.packageName);
            dest.writeString(this.overriddenText);
            dest.writeString(this.overriddenSubject);
            dest.writeParcelable(this.overriddenImage, flags);
            dest.writeByte(textDisabled ? (byte) 1 : (byte) 0);
            dest.writeByte(subjectDisabled ? (byte) 1 : (byte) 0);
            dest.writeByte(imageDisabled ? (byte) 1 : (byte) 0);
        }

        /**
         * Provide a specific text for the linked package name.
         *
         * @param text text which will be used as {@link android.content.Intent#EXTRA_TEXT}
         * @return {@link ExtraProvider} for chaining.
         */
        public ExtraProvider overrideText(String text) {
            this.overriddenText = text;
            return this;
        }

        /**
         * Provide a specific subject for the linked package name.
         *
         * @param subject subject which will be used as {@link android.content.Intent#EXTRA_SUBJECT}
         * @return {@link ExtraProvider} for chaining.
         */
        public ExtraProvider overrideSubject(String subject) {
            this.overriddenSubject = subject;
            return this;
        }

        /**
         * Provide a specific image for the linked package name.
         *
         * @param image image which will be used as {@link android.content.Intent#EXTRA_STREAM}
         * @return {@link ExtraProvider} for chaining.
         */
        public ExtraProvider overrideImage(Uri image) {
            this.overriddenImage = image;
            return this;
        }

        /**
         * Disable the extra {@link android.content.Intent#EXTRA_TEXT} for the linked package
         * to avoid a copy from the default {@link IntentShare} text.
         *
         * @return {@link ExtraProvider} for chaining.
         */
        public ExtraProvider disableText() {
            this.textDisabled = true;
            return this;
        }

        /**
         * Disable the extra {@link android.content.Intent#EXTRA_SUBJECT} for the linked package
         * to avoid a copy from the default {@link IntentShare} subject.
         *
         * @return {@link ExtraProvider} for chaining.
         */
        public ExtraProvider disableSubject() {
            this.subjectDisabled = true;
            return this;
        }

        /**
         * Disable the extra {@link android.content.Intent#EXTRA_STREAM} for the linked package
         * to avoid a copy from the default {@link IntentShare} image.
         *
         * @return {@link ExtraProvider} for chaining.
         */
        public ExtraProvider disableImage() {
            this.imageDisabled = true;
            return this;
        }
    }
}
