package fr.tvbarthel.intentshare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple activity used to allow the user to choose a target activity for the sharing intent.
 */
public class TargetChooserActivity extends AppCompatActivity
        implements TargetActivityAdapter.Listener, View.OnClickListener, TargetActivityManager.ResolveListener {

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

    /**
     * Root view of the activity
     */
    private View rootView;

    /**
     * List of sharing target activity.
     */
    private List<TargetActivity> targetActivities;

    /**
     * Duration in milli.
     */
    private long animationDuration;

    /**
     * Interpolator for element which enter the screen.
     */
    private Interpolator inInterpolator;

    /**
     * Interpolator for element which leave the screen.
     */
    private Interpolator outInterpolator;

    /**
     * View used as background since recycler view padding can't allow to used
     * recycler background directly.
     */
    private View background;

    /**
     * Used to know if the activity state has been restored after a saved instance.
     */
    private boolean stateRestored;

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
            ((Activity) context).overridePendingTransition(-1, -1);
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

        setContentView(R.layout.isl_activity_target_chooser);
        rootView = findViewById(R.id.activity_target_chooser_root_view);
        recyclerView = ((RecyclerView) findViewById(R.id.activity_target_chooser_recycler_list));
        stickyTitle = ((TargetActivityHeaderView) findViewById(R.id.activity_chooser_sticky_title));
        stickyShadow = findViewById(R.id.activity_chooser_sticky_title_shadow);
        background = findViewById(R.id.activity_target_chooser_background);

        targetActivities = new ArrayList<>();
        selectedTargetActivity = null;
        listenerNotified = false;

        Resources resources = getResources();
        animationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime);

        rootView.setOnClickListener(this);
        rootView.setAlpha(0f);

        stateRestored = savedInstanceState != null;
        setUpRecyclerView(savedInstanceState);
        setUpStickyTitle();

        targetActivityManager = new TargetActivityManager();
        targetActivityManager.resolveTargetActivities(this, this, intentShare.comparatorProvider.provideComparator());

        inInterpolator = new DecelerateInterpolator();
        outInterpolator = new AccelerateInterpolator();
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
        if (!listenerNotified && !isChangingConfigurations()) {
            IntentShareListener.notifySharingCanceled(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (selectedTargetActivity != null) {
            IntentShareListener.notifySharingCompleted(this, selectedTargetActivity.getPackageName());
        } else if (!isChangingConfigurations()) {
            IntentShareListener.notifySharingCanceled(this);
        }
        listenerNotified = true;
    }

    @Override
    public void onClick(View v) {
        finishAnimated();
    }

    @Override
    public void onTargetActivitiesResolved(@NonNull ArrayList<TargetActivity> targetActivities) {
        this.targetActivities.addAll(targetActivities);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTargetActivitySelected(@NonNull TargetActivity targetActivity) {
        selectedTargetActivity = targetActivity;
        targetActivityManager.startTargetActivity(this, targetActivity, intentShare);
        finish();
    }

    @Override
    public void onLabelResolved(TargetActivity targetActivity) {
        adapter.notifyTargetActivityChanged(targetActivity);
    }


    private void setUpRecyclerView(Bundle savedInstance) {
        recyclerView.setLayoutManager(LayoutManagerFactory.buildLayoutManager(this));
        targetActivities = new ArrayList<>();
        adapter = new TargetActivityAdapter(
                targetActivities,
                intentShare.chooserTitle,
                intentShare.iconLoader
        );
        adapter.setListener(this);

        targetActivityViewHeight = getResources().getDimensionPixelSize(R.dimen.isl_target_activity_view_height);

        if (savedInstance != null) {
            currentRecyclerScrollY = savedInstance.getInt(SAVED_CURRENT_SCROLL_Y, 0);
        } else {
            currentRecyclerScrollY = 0;
        }

        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int totalHeight = adapter.getItemCount() * targetActivityViewHeight;
                        int maxStartingHeight = (int) (recyclerView.getHeight() / 2.5f);
                        int startingHeight = Math.min(totalHeight, maxStartingHeight);
                        recyclerPaddingTop = recyclerView.getHeight() - startingHeight;
                        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerPaddingTop,
                                recyclerView.getPaddingRight(), 0);
                        recyclerView.setTranslationY(recyclerView.getHeight());
                        int backgroundTranslationY = Math.max(0, recyclerPaddingTop - currentRecyclerScrollY);
                        background.setTranslationY(recyclerView.getHeight() + backgroundTranslationY);
                        recyclerView.setAdapter(adapter);
                        if (stateRestored) {
                            rootView.setAlpha(1f);
                            recyclerView.setTranslationY(0);
                            background.setTranslationY(backgroundTranslationY);
                        } else {
                            rootView.animate()
                                    .alpha(1f)
                                    .setDuration(animationDuration)
                                    .setInterpolator(inInterpolator)
                                    .setListener(null);
                            recyclerView.animate()
                                    .translationY(0)
                                    .setDuration(animationDuration)
                                    .setInterpolator(inInterpolator)
                                    .setListener(null);
                            background.animate()
                                    .translationY(backgroundTranslationY)
                                    .setDuration(animationDuration)
                                    .setInterpolator(inInterpolator)
                                    .setListener(null);
                        }
                        return false;
                    }
                }
        );
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
                        } else if (currentRecyclerScrollY < recyclerPaddingTop) {
                            if (isStickyTitleDisplayed) {
                                isStickyTitleDisplayed = false;
                                stickyTitle.setVisibility(View.INVISIBLE);
                                stickyShadow.setVisibility(View.INVISIBLE);
                            }
                            background.setTranslationY(recyclerPaddingTop - currentRecyclerScrollY);

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
                .alpha(0)
                .setDuration(animationDuration)
                .setInterpolator(outInterpolator)
                .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     super.onAnimationEnd(animation);
                                     TargetChooserActivity.this.finish();
                                     TargetChooserActivity.this.overridePendingTransition(-1, -1);
                                 }
                             }
                );
        recyclerView.animate()
                .translationY(rootView.getHeight())
                .setDuration(animationDuration)
                .setInterpolator(outInterpolator)
                .setListener(null);
        background.animate()
                .translationY(background.getTranslationY() + rootView.getHeight())
                .setDuration(animationDuration)
                .setInterpolator(outInterpolator)
                .setListener(null);
        stickyShadow.animate()
                .translationY(rootView.getHeight())
                .setDuration(animationDuration)
                .setInterpolator(outInterpolator)
                .setListener(null);
        stickyTitle.animate()
                .translationY(rootView.getHeight())
                .setDuration(animationDuration)
                .setInterpolator(outInterpolator)
                .setListener(null);
    }
}
