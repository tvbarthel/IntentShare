package fr.tvbarthel.intentsharesample;

import android.os.Parcel;
import android.os.Parcelable;

import fr.tvbarthel.intentshare.IntentShare;

/**
 * Encapsulate data around an {@link ExtraProviderWrapper}
 */
class ExtraProviderWrapper implements Parcelable {

    /**
     * Parcelable.
     */
    public static final Parcelable.Creator<ExtraProviderWrapper> CREATOR
            = new Parcelable.Creator<ExtraProviderWrapper>() {
        @Override
        public ExtraProviderWrapper createFromParcel(Parcel source) {
            return new ExtraProviderWrapper(source);
        }

        @Override
        public ExtraProviderWrapper[] newArray(int size) {
            return new ExtraProviderWrapper[size];
        }
    };

    private String appName;
    private IntentShare.ExtraProvider extraProvider;

    /**
     * Encapsulate data around an {@link ExtraProviderWrapper}
     *
     * @param appName name of the app for which the extra provider has been build.
     */
    public ExtraProviderWrapper(String appName, IntentShare.ExtraProvider extraProvider) {
        this.appName = appName;
        this.extraProvider = extraProvider;
    }

    /**
     * Encapsulate data around an {@link ExtraProviderWrapper}
     *
     * @param in parcel.
     */
    protected ExtraProviderWrapper(Parcel in) {
        this.appName = in.readString();
        this.extraProvider = in.readParcelable(IntentShare.ExtraProvider.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeParcelable(this.extraProvider, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtraProviderWrapper that = (ExtraProviderWrapper) o;

        if (appName != null ? !appName.equals(that.appName) : that.appName != null) {
            return false;
        }
        return !(extraProvider != null ? !extraProvider.equals(that.extraProvider) : that.extraProvider != null);

    }

    @Override
    public int hashCode() {
        int result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (extraProvider != null ? extraProvider.hashCode() : 0);
        return result;
    }

    /**
     * Name of the app for which the extra provider has been build.
     *
     * @return Name of the app for which the extra provider has been build.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Access to the wrapped extra provider.
     *
     * @return wrapped extra provider.
     */
    public IntentShare.ExtraProvider getExtraProvider() {
        return extraProvider;
    }

}
