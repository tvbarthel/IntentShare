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
     * Key used to save the current scroll during rotation.
     */
    private static final String SAVED_CURRENT_SCROLL_Y = "tca_saved_instance_key_current_scroll";

    /**
     * Recycler view used to display the list of target application.
     */
    private RecyclerView recyclerView;

    /**
     * Padding top applied to the recycler view.
     */
    private int recyclerPaddingTop;

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
     * Sticky header view displayed to keep an eye on the contextual action when the
     * header inside the list is hidden.
     */
    private TargetActivityHeaderView stickyTitle;

    /**
     * Sticky header shadow.
     */
    private View stickyShadow;

    /**
     * Boolean used to know if the sticky header is displayed.
     */
    private boolean isStickyTitleDisplayed;

    /**
     * Used to keep current recycler scroll y up to date.
     */
    private int currentRecyclerScrollY;
    private View rootView;

    /**
     * Simple activity used to allow the user to choose a target activity for the sharing intent.
     *
     * @param context     context used to start the activity.
     * @param intentShare data to share
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
        rootView = findViewById(R.id.activity_target_chooser_root_view);
        recyclerView = ((RecyclerView) findViewById(R.id.activity_target_chooser_recycler_list));
        stickyTitle = ((TargetActivityHeaderView) findViewById(R.id.activity_chooser_sticky_title));
        stickyShadow = findViewById(R.id.activity_chooser_sticky_title_shadow);

        targetActivityManager = new TargetActivityManager();
        targetActivityManager.resolveTargetActivities(this);

        selectedTargetActivity = null;
        listenerNotified = false;

        rootView.setOnClickListener(this);

        setUpRecyclerView(savedInstanceState);
        setUpStickyTitle();
    }

    @Override
    public void onBackPressed() {
        finishAnimated();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_CURRENT_SCROLL_Y, currentRecyclerScrollY);
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
        targetActivityManager.startTargetActivity(this, targetActivity, intentShare);
        finish();
    }


    private void setUpRecyclerView(Bundle savedInstance) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        adapter = new TargetActivityAdapter(
                targetActivityManager.getTargetActivities(),
                intentShare.chooserTitle,
                intentShare.iconLoader
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
                        recyclerPaddingTop = recyclerView.getHeight() - startingHeight;
                        recyclerView.setPadding(0, recyclerPaddingTop, 0, 0);
                        recyclerView.setTranslationY(recyclerView.getHeight());
                        recyclerView.setAdapter(adapter);
                        recyclerView.animate().translationY(0).setListener(null);
                        return false;
                    }
                }
        );
        if (savedInstance != null) {
            currentRecyclerScrollY = savedInstance.getInt(SAVED_CURRENT_SCROLL_Y, 0);
        } else {
            currentRecyclerScrollY = 0;
        }
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        currentRecyclerScrollY += dy;
                        if (!isStickyTitleDisplayed && currentRecyclerScrollY >= recyclerPaddingTop) {
                            isStickyTitleDisplayed = true;
                            stickyTitle.setVisibility(View.VISIBLE);
                            stickyShadow.setVisibility(View.VISIBLE);
                        } else if (isStickyTitleDisplayed && currentRecyclerScrollY < recyclerPaddingTop) {
                            isStickyTitleDisplayed = false;
                            stickyTitle.setVisibility(View.INVISIBLE);
                            stickyShadow.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        );
    }

    private void setUpStickyTitle() {
        stickyTitle.setVisibility(View.INVISIBLE);
        stickyShadow.setVisibility(View.INVISIBLE);
        stickyTitle.setModel(intentShare.chooserTitle);
        isStickyTitleDisplayed = false;
    }

    private void finishAnimated() {
        rootView.animate()
                .translationY(rootView.getHeight())
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
