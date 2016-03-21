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
    }

    @Override
    public void onBackPressed() {
        finishAnimated();
    }

    @Override
    public void onClick(View v) {
        finishAnimated();
    }

    @Override
    public void onTargetActivitySelected(TargetActivity targetActivity) {
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
