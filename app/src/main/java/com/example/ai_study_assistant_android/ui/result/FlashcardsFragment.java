package com.example.ai_study_assistant_android.ui.result;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ai_study_assistant_android.R;
import com.example.ai_study_assistant_android.model.Flashcard;
import com.example.ai_study_assistant_android.model.StudySession;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FlashcardsFragment extends Fragment {

    private static final String ARG_SESSION = "session";

    private List<Flashcard> flashcards;
    private int currentIndex = 0;
    private boolean isShowingFront = true;

    private TextView tvCounter, tvFront, tvBack, tvHint;
    private View cardFront, cardBack, flCard;
    private MaterialButton btnPrev, btnNext;

    private AnimatorSet flipToBack, flipToFront;

    public static FlashcardsFragment newInstance(StudySession session) {
        FlashcardsFragment f = new FlashcardsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SESSION, session);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flashcards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCounter = view.findViewById(R.id.tv_counter);
        tvFront = view.findViewById(R.id.tv_front);
        tvBack = view.findViewById(R.id.tv_back);
        tvHint = view.findViewById(R.id.tv_hint);
        cardFront = view.findViewById(R.id.card_front);
        cardBack = view.findViewById(R.id.card_back);
        flCard = view.findViewById(R.id.fl_card);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);

        StudySession session = getArguments() != null
                ? (StudySession) getArguments().getSerializable(ARG_SESSION) : null;

        if (session == null || session.getFlashcards() == null || session.getFlashcards().isEmpty()) {
            tvFront.setText("No flashcards available.");
            return;
        }

        flashcards = session.getFlashcards();

        // Camera distance to avoid clipping during flip
        float scale = requireContext().getResources().getDisplayMetrics().density;
        cardFront.setCameraDistance(8000 * scale);
        cardBack.setCameraDistance(8000 * scale);

        // Load flip animators
        flipToBack = (AnimatorSet) AnimatorInflater.loadAnimator(requireContext(), R.animator.flip_out);
        AnimatorSet flipToBackIn = (AnimatorSet) AnimatorInflater.loadAnimator(requireContext(), R.animator.flip_in);
        flipToBack.setTarget(cardFront);
        flipToBackIn.setTarget(cardBack);

        flipToFront = (AnimatorSet) AnimatorInflater.loadAnimator(requireContext(), R.animator.flip_out);
        AnimatorSet flipToFrontIn = (AnimatorSet) AnimatorInflater.loadAnimator(requireContext(), R.animator.flip_in);
        flipToFront.setTarget(cardBack);
        flipToFrontIn.setTarget(cardFront);

        flCard.setOnClickListener(v -> flipCard());

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showCard(true);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < flashcards.size() - 1) {
                currentIndex++;
                showCard(true);
            }
        });

        showCard(false);
    }

    private void showCard(boolean resetFace) {
        if (resetFace) {
            isShowingFront = true;
            cardFront.setRotationY(0f);
            cardBack.setRotationY(180f);
            cardFront.setAlpha(1f);
            cardBack.setAlpha(1f);
        }

        Flashcard card = flashcards.get(currentIndex);
        tvFront.setText(card.getFront());
        tvBack.setText(card.getBack());
        tvCounter.setText(getString(R.string.flashcard_counter,
                currentIndex + 1, flashcards.size()));

        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < flashcards.size() - 1);
    }

    private void flipCard() {
        float scale = requireContext().getResources().getDisplayMetrics().density;

        if (isShowingFront) {
            // Front → Back
            AnimatorSet outAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                    requireContext(), R.animator.flip_out);
            AnimatorSet inAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                    requireContext(), R.animator.flip_in);
            outAnim.setTarget(cardFront);
            inAnim.setTarget(cardBack);
            cardFront.setCameraDistance(8000 * scale);
            cardBack.setCameraDistance(8000 * scale);
            outAnim.start();
            inAnim.start();
            isShowingFront = false;
            tvHint.setText("Tap to flip back");
        } else {
            // Back → Front
            AnimatorSet outAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                    requireContext(), R.animator.flip_out);
            AnimatorSet inAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                    requireContext(), R.animator.flip_in);
            outAnim.setTarget(cardBack);
            inAnim.setTarget(cardFront);
            cardFront.setCameraDistance(8000 * scale);
            cardBack.setCameraDistance(8000 * scale);
            outAnim.start();
            inAnim.start();
            isShowingFront = true;
            tvHint.setText(getString(R.string.flashcard_tap_hint));
        }
    }
}
