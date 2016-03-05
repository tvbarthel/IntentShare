package fr.tvbarthel.intentshare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Simple activity used to allow the user to choose a target activity for the sharing intent.
 */
public class TargetChooserActivity extends AppCompatActivity
        implements TargetActivityAdapter.Listener, View.OnClickListener {

    /**
     * Extra key used to pass param to the activity.
     */
    private static final String EXTRA_INTENT_SHARE = "tca_extra_intent_share";

    /**
     * Recycler view used to display the list of target application.
     */
    private RecyclerView recyclerView;

    /**
     * Adapter used to display target activity inside a list.
     */
    private TargetActivityAdapter adapter;

    /**
     * Values to share retrieved from intent extras.
     */
    private TargetActivityManager targetActivityManager;

    /**
     * Date which must be shared.
     */
    private IntentShare intentShare;

    /**
     * Height in of a {@link TargetActivityView}
     */
    private int targetActivityViewHeight;

    /**
     * Target activity selected by the user.
     */
    private TargetActivity selectedTargetActivity;

    /**
     * Used to know if listener has been notified.
     */
    private boolean listenerNotified;

    /**
     * Simple activity used to allow the user to choose a target activity for the sharing intent.
     *
     * @param context     context used to start the activity.
     * @param intentShare data to share.
     */
    public static void start(Context context, IntentShare intentShare) {
        Intent intent = new Intent(context, TargetChooserActivity.class);
        intent.putExtra(EXTRA_INTENT_SHARE, intentShare);
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

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_INTENT_SHARE)) {
            throw new IllegalArgumentException("Fail to start activity due to missing mandatory extras."
                    + "Use start activity pattern.");
        }
        intentShare = extras.getParcelable(EXTRA_INTENT_SHARE);

        setContentView(R.layout.activity_target_chooser);
        findViewById(R.id.activity_target_chooser_root_view).setOnClickListener(this);
        recyclerView = ((RecyclerView) findViewById(R.id.activity_target_chooser_recycler_list));

        targetActivityManager = new TargetActivityManager();
        targetActivityManager.resolveTargetActivities(this);
        setUpRecyclerView();

        selectedTargetActivity = null;
        listenerNotified = false;
    }

    @Override
    public void onBackPressed() {
        finishAnimated();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!listenerNotified) {
            IntentShareListener.notifySharingCanceled(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (selectedTargetActivity != null) {
            IntentShareListener.notifySharingCompleted(this, selectedTargetActivity.getPackageName());
        } else {
            IntentShareListener.notifySharingCanceled(this);
        }
        listenerNotified = true;
    }

    @Override
    public void onClick(View v) {
        finishAnimated();
    }

    @Override
    public void onTargetActivitySelected(TargetActivity targetActivity) {
        selectedTargetActivity = targetActivity;
        Intent intent = targetActivityManager.buildTargetActivityIntent(targetActivity, intentShare);
        startActivity(intent);
        finish();
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
                targetActivityManager.getTargetActivities(),
                getString(R.string.default_sharing_label)
        );
        adapter.setListener(this);

        targetActivityViewHeight = getResources().getDimensionPixelSize(R.dimen.target_activity_view_height);

        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                        int totalHeight = adapter.getItemCount() * targetActivityViewHeight;
                        int maxStartingHeight = (int) (recyclerView.getHeight() / 3f);
                        int startingHeight = Math.min(totalHeight, maxStartingHeight);


                        recyclerView.setPadding(0, recyclerView.getHeight() - startingHeight, 0, 0);
                        recyclerView.setTranslationY(recyclerView.getHeight());
                        recyclerView.setAdapter(adapter);
                        recyclerView.animate().translationY(0).setListener(null);
                        return false;
                    }
                }
        );
    }

    private void finishAnimated() {
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
}
