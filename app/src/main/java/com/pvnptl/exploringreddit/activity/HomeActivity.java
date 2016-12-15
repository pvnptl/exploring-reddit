package com.pvnptl.exploringreddit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pvnptl.exploringreddit.ProjectUtils;
import com.pvnptl.exploringreddit.R;
import com.pvnptl.exploringreddit.fragment.SubredditFragment;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SubredditFragment.OnFragmentInteractionListener {

    private static final String ACTION_NOTIFICATION = "android.intent.action.NOTIFICATION";
    private static final String ACTION_SEARCH_SUBREDDIT = "android.intent.action.SHORTCUT_SUBREDDIT";
    private static final String ACTION_AWW = "android.intent.action.SHORTCUT_AWW";
    private static final String ACTION_CATS = "android.intent.action.SHORTCUT_CATS";
    private static final String ACTION_PICS = "android.intent.action.SHORTCUT_PICS";

    String[] mSubreddits = new String[]{
            "Frontpage",
            "all",
            "alternativeart",
            "aww",
            "adviceanimals",
            "cats",
            "gifs",
            "images",
            "photoshopbattles",
            "pics",
            "hmmm"
    };
    private String mCurrentSubreddit;
    private MenuItem mPreviousMenuItem;
    private Fragment mSubredditFragment;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("pvn", "onCreate");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (ButterKnife.findById(this, R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                mCurrentSubreddit = savedInstanceState.getString("subreddit");
            } else {
                mCurrentSubreddit = mSubreddits[0].toLowerCase();
                if (!ProjectUtils.isOnline(this)) {
                    showSnackbarMessage(getString(R.string.device_offline));
                }
                // Create a new Fragment to be placed in the activity layout
                mSubredditFragment = SubredditFragment.newInstance(mCurrentSubreddit);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mSubredditFragment).commit();
            }

            getSupportActionBar().setTitle(mCurrentSubreddit);
            // Supporting only hot posts from reddit
            toolbar.setSubtitle("Hot");
            updateQuickAccessSubredditList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When activity is launched from notification or app shortcuts click in Accessibility service
        Intent intent = getIntent();

        if (intent != null) {
            if (ACTION_NOTIFICATION.equals(intent.getAction())) {
                String extra = intent.getStringExtra("subredditname");
                // Default to r/frontpage
                mCurrentSubreddit = extra != null ? extra : mSubreddits[0];
                replaceFragment(mCurrentSubreddit);
                setNavMenuItemSelection();
            } else if (ACTION_SEARCH_SUBREDDIT.equals(intent.getAction())) {
                showSubredditInputDialog();
            } else if (ACTION_AWW.equals(intent.getAction())) {
                mCurrentSubreddit = "aww";
                replaceFragment(mCurrentSubreddit);
                setNavMenuItemSelection();
            } else if (ACTION_CATS.equals(intent.getAction())) {
                mCurrentSubreddit = "cats";
                replaceFragment(mCurrentSubreddit);
                setNavMenuItemSelection();
            } else if (ACTION_PICS.equals(intent.getAction())) {
                mCurrentSubreddit = "pics";
                replaceFragment(mCurrentSubreddit);
                setNavMenuItemSelection();
            }
            setIntent(null);
        }
    }

    private void updateQuickAccessSubredditList() {
        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem submenuItem = menu.getItem(1);
        SubMenu submenu = submenuItem.getSubMenu();

        for (int i = 0; i < mSubreddits.length; i++) {
            submenu.add(mSubreddits[i]);
            if (mCurrentSubreddit.equalsIgnoreCase(mSubreddits[i])) {
                submenuItem = submenu.getItem(i);
                submenuItem.setChecked(true);
                submenuItem.setCheckable(true);
                mPreviousMenuItem = submenuItem;
            }
        }
    }

    private void setNavMenuItemSelection() {
        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem submenuItem = menu.getItem(1);
        SubMenu submenu = submenuItem.getSubMenu();
        for (int i = 0; i < submenu.size(); i++) {
            if (mCurrentSubreddit.equalsIgnoreCase(submenu.getItem(i).getTitle().toString())) {
                submenuItem = submenu.getItem(i);
                submenuItem.setChecked(true);
                submenuItem.setCheckable(true);
                if (mPreviousMenuItem != null) {
                    mPreviousMenuItem.setChecked(false);
                }
                mPreviousMenuItem = submenuItem;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if (!ProjectUtils.isOnline(this)) {
                showSnackbarMessage(getString(R.string.device_offline));
            } else {
                replaceFragment(mCurrentSubreddit);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mPreviousMenuItem != null) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = item;

        int id = item.getItemId();

        item.setCheckable(true);
        item.setChecked(true);

        if (id == R.id.nav_subreddit) {
            showSubredditInputDialog();
        } else {
            // Menu item name is same as subreddit name
            replaceFragment(item.getTitle().toString());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSubredditInputDialog() {
        final AlertDialog subredditInputDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.view_subreddit)
                .setView(R.layout.layout_subreddit_input)
                .setPositiveButton(getString(R.string.view), null)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        subredditInputDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        subredditInputDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                ProjectUtils.showSoftKeyboard(ButterKnife.findById((AlertDialog) dialog, R.id.cancelled_reason_editText));

                Button doneButton = subredditInputDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                doneButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText subredditNameEditText = ButterKnife.findById((AlertDialog) dialog, R.id.cancelled_reason_editText);
                        if (TextUtils.isEmpty(subredditNameEditText.getText())) {
                            subredditNameEditText.setError(getString(R.string.enter_a_subreddit));
                        } else {
                            ProjectUtils.hideSoftKeyboard(subredditNameEditText);
                            dialog.dismiss();
                            replaceFragment(subredditNameEditText.getText().toString());
                        }
                    }
                });
            }
        });
        subredditInputDialog.show();
    }

    private void replaceFragment(String subredditName) {
        mCurrentSubreddit = subredditName.toLowerCase();

        dismissSnackbar();

        if (!ProjectUtils.isOnline(this)) {
            showSnackbarMessage(getString(R.string.device_offline));
        }

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        getSupportActionBar().setTitle(mCurrentSubreddit);
        // Supporting only hot posts from reddit
        toolbar.setSubtitle("Hot");

        // Create fragment and give it an argument specifying the subreddit it should show
        mSubredditFragment = SubredditFragment.newInstance(subredditName);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, mSubredditFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(String imageURL) {
        Intent intent = new Intent(HomeActivity.this, FullScreenImageActivity.class);
        intent.putExtra(FullScreenImageActivity.IMAGE_URL, imageURL);
        startActivity(intent);
    }

    @Override
    public void onError(String message) {
        showSnackbarMessage(message);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("subreddit", mCurrentSubreddit);
    }

    private void showSnackbarMessage(String message) {
        CoordinatorLayout coordinatorLayout = ButterKnife.findById(this, R.id.coordinator_layout);
        dismissSnackbar();
        mSnackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);

        mSnackbar.show();

    }

    private void dismissSnackbar() {
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
    }

}
