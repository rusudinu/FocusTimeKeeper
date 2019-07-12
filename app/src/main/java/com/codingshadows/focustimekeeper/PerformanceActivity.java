package com.codingshadows.focustimekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
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

        dateTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { //empty everything
                activitiesCompleted = 0;
                totalActivities = 0;
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
                TextView showDateTV = findViewById(R.id.titleTextView);
                if (day == cal.get(Calendar.DAY_OF_MONTH)) showDateTV.setText("Astazi, " + date);
                else showDateTV.setText(date);
                currentDateSelected = date.replace("/", "");
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
                }
            }
        });
    }

    private void showData() {

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
        dialog.setTitle("Please select the date : ");
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
