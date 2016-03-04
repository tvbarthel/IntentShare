package fr.tvbarthel.intentshare;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

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
     * Link used as facebook content when the target application is the facebook one.
     */
    Uri facebookLink;

    /**
     * Text used as tweet content when the target application is the Twitter one.
     */
    String tweet;

    /**
     * Context used to start the activity used to choose a target one.
     */
    private Context context;

    /**
     * {@link IntentShare} is designed to enhance the sharing experience by allowing to share
     * different content according to the application the user will choose to share with.
     *
     * @param context context used send the {@link android.content.Intent}
     */
    private IntentShare(Context context) {
        this.context = context;
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
        this.facebookLink = in.readParcelable(Uri.class.getClassLoader());
        this.tweet = in.readString();
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
        dest.writeParcelable(this.facebookLink, 0);
        dest.writeString(this.tweet);
    }

    /**
     * {@link IntentShare} is designed to enhance the sharing experience by allowing to share
     * different content according to the application the user will choose to share with.
     *
     * @param context context from which the sharing is initiated.
     * @return new instance.
     */
    public static IntentShare with(Context context) {
        return new IntentShare(context);
    }

    /**
     * Text which will be shared.
     * <p/>
     * Will be used as {@link android.content.Intent#EXTRA_TEXT}
     *
     * @param text text to share.
     * @return current {@link IntentShare} for method chaining.
     */
    public IntentShare text(String text) {
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
    public IntentShare image(Uri imageUri) {
        // TODO check if image uri back a image file.
        this.imageUri = imageUri;
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
    public IntentShare mailBody(String mailBody) {
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
    public IntentShare mailSubject(String mailSubject) {
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
    public IntentShare facebookBody(Uri link) {
        // TODO check link validity.
        facebookLink = link;
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
    public IntentShare twitterBody(String tweet) {
        // TODO check tweet validity.
        this.tweet = tweet;
        return this;
    }

    /**
     * Deliver the intent to the system.
     * <p/>
     * This will lead to the display of every applications that can handle the build intent.
     * Target activity field will be then filled according to the params.
     */
    public void deliver() {
        // TODO check params.
        TargetChooserActivity.start(context, this);
    }
}
