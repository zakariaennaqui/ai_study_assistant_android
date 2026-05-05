package com.example.ai_study_assistant_android.ui.result;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ai_study_assistant_android.R;
import com.example.ai_study_assistant_android.model.StudySession;
import com.google.android.material.button.MaterialButton;

public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_SESSION = "extra_session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        StudySession session = (StudySession) getIntent()
                .getSerializableExtra(EXTRA_SESSION);

        if (session == null) {
            finish();
            return;
        }

        // Toolbar
        TextView tvSubject = findViewById(R.id.tv_subject);
        TextView tvTypeLabel = findViewById(R.id.tv_type_label);
        MaterialButton btnBack = findViewById(R.id.btn_back);

        String subject = session.getSubject();
        tvSubject.setText((subject != null && !subject.isEmpty()) ? subject : "Result");

        String type = session.getType() != null ? session.getType() : "SUMMARY";
        tvTypeLabel.setText(type);

        int labelColor;
        switch (type) {
            case "QUIZ":
                labelColor = getColor(R.color.color_badge_quiz); break;
            case "FLASHCARDS":
                labelColor = getColor(R.color.color_badge_flashcards); break;
            default:
                labelColor = getColor(R.color.color_badge_summary); break;
        }
        tvTypeLabel.setTextColor(labelColor);

        btnBack.setOnClickListener(v -> finish());

        // Load the correct result fragment
        if (savedInstanceState == null) {
            Fragment fragment;
            switch (type) {
                case "QUIZ":
                    fragment = QuizFragment.newInstance(session);
                    break;
                case "FLASHCARDS":
                    fragment = FlashcardsFragment.newInstance(session);
                    break;
                default:
                    fragment = SummaryFragment.newInstance(session);
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.result_container, fragment)
                    .commit();
        }
    }
}
