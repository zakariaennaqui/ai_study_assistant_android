package com.example.ai_study_assistant_android.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ai_study_assistant_android.R;
import com.example.ai_study_assistant_android.network.TokenManager;
import com.example.ai_study_assistant_android.ui.auth.LoginActivity;
import com.example.ai_study_assistant_android.ui.generate.GenerateFragment;
import com.example.ai_study_assistant_android.ui.history.HistoryFragment;
import com.example.ai_study_assistant_android.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TokenManager tokenManager;

    // Keep fragment instances so state is preserved on tab switch
    private final HomeFragment homeFragment = new HomeFragment();
    private final GenerateFragment generateFragment = new GenerateFragment();
    private final HistoryFragment historyFragment = new HistoryFragment();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            // Add all fragments, show only home
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, historyFragment, "history").hide(historyFragment)
                    .add(R.id.fragment_container, generateFragment, "generate").hide(generateFragment)
                    .add(R.id.fragment_container, homeFragment, "home")
                    .commit();
            activeFragment = homeFragment;
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchTo(homeFragment);
            } else if (id == R.id.nav_generate) {
                switchTo(generateFragment);
            } else if (id == R.id.nav_history) {
                switchTo(historyFragment);
            }
            return true;
        });
    }

    private void switchTo(Fragment fragment) {
        if (fragment == activeFragment)
            return;
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();
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
