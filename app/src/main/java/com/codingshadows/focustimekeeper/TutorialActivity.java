package com.codingshadows.focustimekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager slideViewPager;
    private LinearLayout dotsLayout;
    private TextView prevBT;
    private TextView nextBT;
    private int currentPage;
    private SliderAdapter sliderAdapter;

    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        slideViewPager = findViewById(R.id.slideViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(this);
        slideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        slideViewPager.addOnPageChangeListener(viewListener);

        prevBT = findViewById(R.id.prevBtn);
        nextBT = findViewById(R.id.nextBtn);

        prevBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideViewPager.setCurrentItem(currentPage - 1);
            }
        });

        nextBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideViewPager.setCurrentItem(currentPage + 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8266;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.circleRed));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.backgroundWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;
            if (position == 0) {
                prevBT.setEnabled(false);
                prevBT.setVisibility(View.INVISIBLE);
                nextBT.setEnabled(true);
                nextBT.setText("Urm.");
                prevBT.setText("");
            } else if (position == dots.length - 1) {
                prevBT.setEnabled(true);
                prevBT.setVisibility(View.VISIBLE);
                nextBT.setEnabled(true);
                nextBT.setText("Inchide");
                prevBT.setText("Inapoi");
            } else {
                prevBT.setEnabled(true);
                prevBT.setVisibility(View.VISIBLE);
                nextBT.setEnabled(true);
                nextBT.setText("Urm.");
                prevBT.setText("Inapoi");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}
