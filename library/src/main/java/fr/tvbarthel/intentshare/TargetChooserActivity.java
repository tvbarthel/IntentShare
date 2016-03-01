package fr.tvbarthel.intentshare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple activity used to allow the user to choose a target activity for the sharing intent.
 */
public class TargetChooserActivity extends AppCompatActivity implements TargetActivityAdapter.Listener {

    /**
     * Extra key used to pass param to the activity.
     */
    private static final String EXTRA_TEXT = "tca_extra_text";
    private static final String EXTRA_IMAGE_URI = "tca_extra_image_uri";
    private static final String EXTRA_MAIL_BODY = "tca_extra_mail_body";
    private static final String EXTRA_MAIL_SUBJECT = "tca_extra_mail_subject";
    private static final String EXTRA_FACEBOOK_LINK = "tca_extra_facebook_link";
    private static final String EXTRA_TWEET = "tca_extra_tweet";

    /**
     * Recycler view used to display the list of target application.
     */
    private RecyclerView recyclerView;

    /**
     * Adapter used to display target activity inside a list.
     */
    private TargetActivityAdapter adapter;

    /**
     * List used to identify mail activities.
     * Basically, any activity which can handle type 'message/rfc_822'.
     */
    private List<String> mailTargetActivities;

    /**
     * List of target activities.
     */
    private List<TargetActivity> targetActivities;

    /**
     * Values to share retrieved from intent extras.
     */
    private String sharedText;
    private Parcelable imageUri;
    private String mailBody;
    private String mailSubject;
    private Parcelable facebookLink;
    private String tweet;

    /**
     * Simple activity used to allow the user to choose a target activity for the sharing intent.
     *
     * @param context    context used to start the activity.
     * @param intentShare data to share.
     */
    public static void start(Context context, IntentShare intentShare) {
        Intent intent = new Intent(context, TargetChooserActivity.class);
        intent.putExtra(EXTRA_TEXT, intentShare.text);
        intent.putExtra(EXTRA_IMAGE_URI, intentShare.imageUri);
        intent.putExtra(EXTRA_MAIL_BODY, intentShare.mailBody);
        intent.putExtra(EXTRA_MAIL_SUBJECT, intentShare.mailSubject);
        intent.putExtra(EXTRA_FACEBOOK_LINK, intentShare.facebookLink);
        intent.putExtra(EXTRA_TWEET, intentShare.tweet);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractIntentExtras();

        setContentView(R.layout.activity_target_chooser);
        recyclerView = ((RecyclerView) findViewById(R.id.activity_target_chooser_recycler_list));

        targetActivities = new ArrayList<>();
        mailTargetActivities = new ArrayList<>();
        setUpTargetActivities(targetActivities, mailTargetActivities);

        setUpRecyclerView();
    }

    @Override
    public void onBackPressed() {
        recyclerView.animate()
                .translationY(recyclerView.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     super.onAnimationEnd(animation);
                                     TargetChooserActivity.this.finish();
                                     TargetChooserActivity.this.overridePendingTransition(
                                             android.R.anim.fade_in,
                                             android.R.anim.fade_out
                                     );
                                 }
                             }
                );
    }


    @Override
    public void onTargetActivitySelected(TargetActivity targetActivity) {
        String packageName = targetActivity.getPackageName();
        String activityName = targetActivity.getActivityName();
        ComponentName componentName = new ComponentName(
                packageName,
                activityName
        );
        Intent i = new Intent(Intent.ACTION_SEND);

        if (mailTargetActivities.contains(packageName + activityName)) {
            i.putExtra(Intent.EXTRA_TEXT, "mail");
        } else {
            i.putExtra(Intent.EXTRA_TEXT, "coucou");
        }
        i.setType("text/plain");
        i.setComponent(componentName);
        startActivity(i);
        finish();
    }

    private void extractIntentExtras() {
        Bundle extras = getIntent().getExtras();
        sharedText = extras.getString(EXTRA_TEXT);
        imageUri = extras.getParcelable(EXTRA_IMAGE_URI);
        mailBody = extras.getString(EXTRA_MAIL_BODY);
        mailSubject = extras.getString(EXTRA_MAIL_SUBJECT);
        facebookLink = extras.getParcelable(EXTRA_FACEBOOK_LINK);
        tweet = extras.getString(EXTRA_TWEET);
    }

    private void setUpTargetActivities(
            @NonNull List<TargetActivity> targetActivities,
            @NonNull List<String> mailTargetActivities) {

        PackageManager packageManager = getPackageManager();

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.putExtra(Intent.EXTRA_TEXT, "queryText");
        intentShare.setType("text/plain");
        List<ResolveInfo> shareActivities = packageManager.queryIntentActivities(
                intentShare,
                PackageManager.GET_RESOLVED_FILTER
        );

        long start = System.currentTimeMillis();
        for (int i = 0; i < shareActivities.size(); i++) {
            ResolveInfo targetActivity = shareActivities.get(i);
            IntentFilter filter = targetActivity.filter;
            if (filter.hasDataType("text/plain")) {
                targetActivities.add(new TargetActivity(this, targetActivity));
                if (filter.hasDataType("message/rfc822")) {
                    mailTargetActivities.add(
                            targetActivity.activityInfo.packageName
                                    + targetActivity.activityInfo.name
                    );
                }
            }
        }
        Log.e("TargetActivity", " in " + (System.currentTimeMillis() - start + " ms"));
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        adapter = new TargetActivityAdapter(
                targetActivities,
                getString(R.string.default_sharing_label)
        );
        adapter.setListener(this);
        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        recyclerView.setPadding(0, (int) (recyclerView.getHeight() / 2f), 0, 0);
                        recyclerView.setTranslationY(recyclerView.getHeight());
                        recyclerView.setAdapter(adapter);
                        recyclerView.animate().translationY(0).setListener(null);
                        return false;
                    }
                }
        );
    }
}
