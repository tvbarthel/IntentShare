package fr.tvbarthel.intentshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Simple listener used to catch which activity the user choose.
 */
public abstract class IntentShareListener {

    /**
     * Action used to inform that the user as complete the sharing.
     */
    private static final String ACTION_ON_COMPLETED = "fr.tvbarthel.intentshare.oncompleted";

    /**
     * Action used to inform that the user as cancel the sharing.
     */
    private static final String ACTION_ON_CANCELED = "fr.tvbarthel.intentshare.oncanceled";

    /**
     * Bundle key used to pass the package name.
     */
    private static final String EXTRA_PACKAGE_NAME = "fr.tvbarthel.intentshare.package";

    /**
     * Internal receiver.
     */
    private final InternalReceiver internalReceiver;

    /**
     * Simple listener used to catch which activity the user chosen.
     */
    public IntentShareListener() {

        internalReceiver = new InternalReceiver() {
            @Override
            public void onCanceled() {
                super.onCanceled();
                IntentShareListener.this.onCanceled();
            }

            @Override
            public void onCompleted(String packageName) {
                super.onCompleted(packageName);
                IntentShareListener.this.onCompleted(packageName);
            }
        };
    }

    /**
     * Called when the user chose an activity to complete the sharing.
     *
     * @param packageName package name of the selected activity.
     */
    public abstract void onCompleted(String packageName);

    /**
     * Called when the user cancel the sharing without selecting any target activity.
     */
    public abstract void onCanceled();

    /**
     * Notify all registered listener that sharing has been completed.
     * <p/>
     * private package.
     *
     * @param context     context used to send the broadcast.
     * @param packageName package name of the selected target activity.
     */
    static void notifySharingCompleted(Context context, String packageName) {
        Intent intent = new Intent(ACTION_ON_COMPLETED);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Notify all registered listener that sharing has been canceled
     * <p/>
     * private package.
     *
     * @param context context used to send the broadcast.
     */
    static void notifySharingCanceled(Context context) {
        Intent intent = new Intent(ACTION_ON_CANCELED);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Used to register the internal listener.
     * <p/>
     * private package.
     *
     * @param context context used to register the listener.
     */
    void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ON_COMPLETED);
        filter.addAction(ACTION_ON_CANCELED);
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(internalReceiver, filter);
    }

    /**
     * Receiver used internally to catch event.
     */
    private static class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_ON_COMPLETED:
                    onCompleted(intent.getStringExtra(EXTRA_PACKAGE_NAME));
                    break;
                case ACTION_ON_CANCELED:
                    onCanceled();
                    break;
                default:
                    throw new IllegalArgumentException("Action unknown : " + intent.getAction());
            }

            // unregister receiver.
            LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(this);
        }

        /**
         * Called when the user chose an activity to complete the sharing.
         *
         * @param packageName package name of the selected activity.
         */
        public void onCompleted(String packageName) {

        }

        /**
         * Called when the user cancel the sharing without selecting any target activity.
         */
        public void onCanceled() {

        }
    }
}
