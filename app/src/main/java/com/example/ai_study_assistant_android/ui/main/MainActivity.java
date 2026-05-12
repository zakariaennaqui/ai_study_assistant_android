package com.example.ai_study_assistant_android.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ai_study_assistant_android.R;
import com.example.ai_study_assistant_android.network.TokenManager;
import com.example.ai_study_assistant_android.ui.auth.LoginActivity;
import com.example.ai_study_assistant_android.ui.generate.GenerateFragment;
import com.example.ai_study_assistant_android.ui.history.HistoryFragment;
import com.example.ai_study_assistant_android.ui.home.HomeFragment;
import com.example.ai_study_assistant_android.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "home";
    private static final String TAG_GENERATE = "generate";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_PROFILE = "profile";
    private static final String STATE_ACTIVE_FRAGMENT_TAG = "active_fragment_tag";

    private BottomNavigationView bottomNav;
    private TokenManager tokenManager;

    private HomeFragment homeFragment;
    private GenerateFragment generateFragment;
    private HistoryFragment historyFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProfileFragment.applySavedTheme(this);
        super.onCreate(savedInstanceState);

        tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);

        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            generateFragment = new GenerateFragment();
            historyFragment = new HistoryFragment();
            profileFragment = new ProfileFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, profileFragment, TAG_PROFILE).hide(profileFragment)
                    .add(R.id.fragment_container, historyFragment, TAG_HISTORY).hide(historyFragment)
                    .add(R.id.fragment_container, generateFragment, TAG_GENERATE).hide(generateFragment)
                    .add(R.id.fragment_container, homeFragment, TAG_HOME)
                    .commit();
            activeFragment = homeFragment;
        } else {
            homeFragment = (HomeFragment) fm.findFragmentByTag(TAG_HOME);
            generateFragment = (GenerateFragment) fm.findFragmentByTag(TAG_GENERATE);
            historyFragment = (HistoryFragment) fm.findFragmentByTag(TAG_HISTORY);
            profileFragment = (ProfileFragment) fm.findFragmentByTag(TAG_PROFILE);

            String savedTag = savedInstanceState.getString(STATE_ACTIVE_FRAGMENT_TAG, TAG_HOME);
            activeFragment = fm.findFragmentByTag(savedTag);
            if (activeFragment == null) {
                activeFragment = homeFragment != null ? homeFragment : fm.findFragmentByTag(TAG_HOME);
            }
            syncBottomNavWithTag(savedTag);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchTo(homeFragment);
            } else if (id == R.id.nav_generate) {
                switchTo(generateFragment);
            } else if (id == R.id.nav_history) {
                switchTo(historyFragment);
            } else if (id == R.id.nav_profile) {
                switchTo(profileFragment);
            }
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeFragment != null && activeFragment.getTag() != null) {
            outState.putString(STATE_ACTIVE_FRAGMENT_TAG, activeFragment.getTag());
        }
    }

    private void syncBottomNavWithTag(String tag) {
        int itemId = R.id.nav_home;
        if (TAG_GENERATE.equals(tag)) {
            itemId = R.id.nav_generate;
        } else if (TAG_HISTORY.equals(tag)) {
            itemId = R.id.nav_history;
        } else if (TAG_PROFILE.equals(tag)) {
            itemId = R.id.nav_profile;
        }
        bottomNav.setSelectedItemId(itemId);
    }

    private void switchTo(Fragment fragment) {
        if (fragment == null) return;
        if (fragment == activeFragment) return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (activeFragment != null) {
            ft.hide(activeFragment);
        }
        ft.show(fragment).commit();
        activeFragment = fragment;
    }

    /** Called from HomeFragment quick-action cards */
    public void openGenerateWithType(String type) {
        generateFragment.setPreselectedType(type);
        bottomNav.setSelectedItemId(R.id.nav_generate);
    }

    public void logout() {
        tokenManager.clear();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
