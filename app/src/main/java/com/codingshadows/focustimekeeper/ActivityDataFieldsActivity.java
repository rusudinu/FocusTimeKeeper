package com.codingshadows.focustimekeeper;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class ActivityDataFieldsActivity extends AppCompatActivity {
    private int selectedImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_fields);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Intent intent = getIntent();
        final int activityNumber = Integer.valueOf(intent.getStringExtra("message"));
        String date = intent.getStringExtra("date");


        //ImageView ivVectorImage = findViewById(R.id.friendsIcon);
        //ivVectorImage.setColorFilter(getResources().getColor(R.color.circleRed));
        //WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        getWindow().setElevation(10);
        getWindow().setLayout((int)(width*.8), (int)(height*.7));

        TextView titleTV = findViewById(R.id.titleAddDataTextView);
        titleTV.setText("Adauga o activitate noua" + '\n' + date);





        //region select hour
        final EditText startHourET = findViewById(R.id.startHourEditText);
        startHourET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityDataFieldsActivity.this,R.style.Theme_AppCompat_DayNight_DarkActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        startHourET.setText(hourOfDay+ ":" + checkDigit(minutes));
                    }
                }, 0, 0, true);
                timePickerDialog.setTitle("Activitatea incepe la: ");
                timePickerDialog.show();
            }
        });

        startHourET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(startHourET.isFocused())
                {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityDataFieldsActivity.this,R.style.Theme_AppCompat_DayNight_DarkActionBar, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            startHourET.setText(hourOfDay+ ":" + checkDigit(minutes));
                        }
                    }, 0, 0, true);
                    timePickerDialog.setTitle("Activitatea incepe la: ");
                    timePickerDialog.show();
                }
            }
        });

        final EditText endHourET = findViewById(R.id.endHourEditText);
        endHourET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityDataFieldsActivity.this, R.style.Theme_AppCompat_DayNight_DarkActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        endHourET.setText(hourOfDay+ ":" + checkDigit(minutes));
                    }
                }, 0, 0, true);
                timePickerDialog.setTitle("Activitatea se termina la: ");
                timePickerDialog.show();
            }
        });
        endHourET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(endHourET.hasFocus())
                {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityDataFieldsActivity.this, R.style.Theme_AppCompat_DayNight_DarkActionBar, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            endHourET.setText(hourOfDay+ ":" + checkDigit(minutes));
                        }
                    }, 0, 0, true);
                    timePickerDialog.setTitle("Activitatea se termina la: ");
                    timePickerDialog.show();
                }
            }
        });


        //endregion

        Button addActivity = findViewById(R.id.addActivityButton);
        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText detailsET  = findViewById(R.id.detaliiEditText);
                String details = detailsET.getText().toString();

                EditText startHourET = findViewById(R.id.startHourEditText);
                String startHour = startHourET.getText().toString();

                EditText endHourET = findViewById(R.id.endHourEditText);
                String endHour = endHourET.getText().toString();

                EditText activityTitleET = findViewById(R.id.activityTitle);
                String activityTitle = activityTitleET.getText().toString();

                Class_PassActivityDetails.activityNumber = activityNumber;
                Class_PassActivityDetails.details = details;
                Class_PassActivityDetails.startHour = startHour;
                Class_PassActivityDetails.endHour = endHour;
                Class_PassActivityDetails.title = activityTitle;

                finish();
            }
        });
    }
    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void changeSelectedImage()
    {

    }
}
