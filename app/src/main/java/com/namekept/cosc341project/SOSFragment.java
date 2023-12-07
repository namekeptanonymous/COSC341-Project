package com.namekept.cosc341project;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SOSFragment extends Fragment {

    ConstraintLayout constraintLayout;

    Button button;

    TextView textView3;

    TextView textView4;

    TextView textView5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_s_o_s, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayout = view.findViewById(R.id.layout);

        button = view.findViewById(R.id.button2);

        textView3 = view.findViewById(R.id.textView3);

        textView4 = view.findViewById(R.id.textView4);

        textView5 = view.findViewById(R.id.textView5);

        Handler handler = new Handler();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a ValueAnimator to animate the background color change
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                        getResources().getColor(android.R.color.transparent),
                        getResources().getColor(android.R.color.holo_red_light)
                );
                colorAnimation.setDuration(1500);

                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        constraintLayout.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });

                // Start the color animation
                colorAnimation.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView3.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.VISIBLE);
                        textView5.setVisibility(View.VISIBLE);
                    }
                }, 1500);
            }
        });
    }
}