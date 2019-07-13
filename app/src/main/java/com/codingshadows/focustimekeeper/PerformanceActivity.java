package com.codingshadows.focustimekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class PerformanceActivity extends AppCompatActivity {
    private int activitiesCompleted = 0;
    private int totalActivities = 0;
    private String currentDateSelected = "";
    private String tag = "PerformanceActivity";
    int pastDay = 0;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        final TextView dateTV = findViewById(R.id.selectDateTextView);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        Date date = new Date();
        final String currentDate = formatter.format(date);
        dateTV.setText("Astazi, " + currentDate);
        getData(currentDate.replace("/", ""));


        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        dateTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (dateTV.hasFocus()) {
                    showCalendar();
                }
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = "";

                pastDay = day;
                if (day <= 9 && month <= 9) {
                    date = "0" + day + "/" + "0" + month + "/" + year;
                } else if (day <= 9 || month <= 9) {
                    if (month <= 9) {
                        date = day + "/" + "0" + month + "/" + year;
                    }
                    if (day <= 9) {
                        date = "0" + day + "/" + month + "/" + year;
                    }
                } else date = day + "/" + month + "/" + year;
                Calendar cal = Calendar.getInstance();
                TextView showDateTV = findViewById(R.id.selectDateTextView);
                if (day == cal.get(Calendar.DAY_OF_MONTH)) showDateTV.setText("Astazi, " + date);
                else showDateTV.setText(date);
                currentDateSelected = date.replace("/", "");
                tempCompleted = 0;
                tempPercentage = 0;

                new CountDownTimer(20, 1) { // astept sa se termine animatia de disparitie a calendarului pentru a putea incepe "animatia" procentului
                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        getData(currentDateSelected);
                        Log.e(tag, currentDateSelected);
                    }
                }.start();
            }

        };
    }


    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    private void getData(String dayID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PROGRAM").document(getUID()).collection(dayID).document("PERFORMANCE").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("Activities completed") != null && documentSnapshot.get("Activities total") != null) {
                    String actCompleted = documentSnapshot.get("Activities completed").toString();
                    String actTotal = documentSnapshot.get("Activities total").toString();
                    activitiesCompleted = Integer.valueOf(actCompleted);
                    totalActivities = Integer.valueOf(actTotal);
                    Log.e(tag, "got data");
                    dataFound = true;
                    showData();
                } else {
                    dataFound = false;
                    TextView ptv = findViewById(R.id.percentageTextView);
                    ptv.setText("n/a");
                    final ProgressBar progressBar = findViewById(R.id.progressBar2);
                    progressBar.setProgress(0);
                }
            }
        });
    }


    int tempCompleted = 0;
    double tempPercentage = 0;
    boolean dataFound = false;

    private void showData() // animate
    {
        Log.e(tag, "show data");
        if (dataFound && (tempCompleted * 50 < activitiesCompleted * 50)) {
            Log.e(tag, "in if");
            final ProgressBar progressBar = findViewById(R.id.progressBar2);
            progressBar.setMax(totalActivities * 50);
            final TextView ptv = findViewById(R.id.percentageTextView);

            new CountDownTimer(30, 1) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    if (tempCompleted < activitiesCompleted) {
                        tempCompleted++;
                    }
                    tempPercentage = tempCompleted * 100 / totalActivities;
                    String toDisplay = tempPercentage + " %" + "  (" + tempCompleted + "/" + totalActivities + ")";
                    ptv.setText(toDisplay);
                    progressBar.setProgress(tempCompleted * 50);
                    if (tempPercentage >= 70) {
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    } else if (tempPercentage >= 50) {
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                    } else {
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    }
                    if (dataFound && (tempCompleted * 50 < activitiesCompleted * 50)) showData();
                    else dataFound = false;
                }
            }.start();
        } else if (tempCompleted > activitiesCompleted) {
            tempCompleted = 0;
            tempPercentage = 0;
            dataFound = false;
        }
    }

    private void showCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = 0;

        if (pastDay != 0) {
            day = pastDay;
        } else day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                PerformanceActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.setTitle("Selecteaza o data: ");
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
