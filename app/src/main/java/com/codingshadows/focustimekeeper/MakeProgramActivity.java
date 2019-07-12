package com.codingshadows.focustimekeeper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MakeProgramActivity extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    int pastDay = 0;
    int activityNumber = 1;
    String dropdownSelection = "";

    private String tag = "MakeProgramActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_program);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView addNewActivityIMV = findViewById(R.id.addActivityImageView);
        addNewActivityIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewActivity();
            }
        });

        EditText dataET = findViewById(R.id.dateEditText);
        dataET.setVisibility(View.INVISIBLE);


        final Spinner dropdown = findViewById(R.id.denumireProgramSpinner);
        String[] items = new String[]{"Luni", "Marti", "Miercuri", "Joi", "Vineri", "Sambata", "Duminica", "Program special"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);


        TextView deleteActivityBT = findViewById(R.id.deleteActivities);
        deleteActivityBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dayD = dropdownSelection;
                if (dayD.equals("Program special")) return;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("PROGRAM").document(getUID()).collection(dayD).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                deleteDocument(dayD, document.getId());
                            }
                            Toast.makeText(MakeProgramActivity.this, "Activitatile au fost sterse!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dropdownSelection = dropdown.getSelectedItem().toString();
                if (dropdownSelection.equals("Program special")) {
                    //arat data
                    EditText dataET = findViewById(R.id.dateEditText);
                    dataET.setVisibility(View.VISIBLE);
                } else {
                    EditText dataET = findViewById(R.id.dateEditText);
                    dataET.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //region select the date
        final EditText selectDateET = findViewById(R.id.dateEditText);
        selectDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = 0;
                if (pastDay != 0) {
                    day = pastDay;
                } else day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        MakeProgramActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.setTitle("Please select the date : ");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        selectDateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (selectDateET.hasFocus()) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = 0;
                    if (pastDay != 0) {
                        day = pastDay;
                    } else day = cal.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog dialog = new DatePickerDialog(
                            MakeProgramActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year, month, day);
                    dialog.setTitle("Please select the date : ");
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
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
                selectDateET.setText(date);
            }
        };
        //endregion
    }


    private void deleteDocument(final String day, String docID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PROGRAM").document(getUID()).collection(day).document(docID).delete();
    }


    private void addNewActivity() {
        EditText selectDateET = findViewById(R.id.dateEditText);
        if (selectDateET.getText().toString().equals("") && dropdownSelection.equals("Program special")) {
            showNotification("Trebuie sa introduceti o data! Apoi puteti introduce activitati noi.");
        } else if (dropdownSelection.equals("Program special")) {
            String date = selectDateET.getText().toString();
            showActivityProperties(date);
        } else showActivityProperties(dropdownSelection);
    }


    private void addToServer(String title, String startTime, String endTime, String details) {
        if (dropdownSelection.equals("Program special")) {
            String date = "";
            EditText dateET = findViewById(R.id.dateEditText);
            date = dateET.getText().toString();
            date = date.replace("/", "");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> userData = new HashMap<>();
            userData.put("Title", title);
            userData.put("Time", startTime + " -> " + endTime);
            userData.put("Details", details);

            db.collection("PROGRAM").document(getUID()).collection(date).document().set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void documentReference) {
                    showNotification("Program adaugat cu succes (pe server)!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            updateActivitiesData(date);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> userData = new HashMap<>();
            userData.put("Title", title);
            userData.put("Time", startTime + " -> " + endTime);
            userData.put("Details", details);

            try {
                db.collection("PROGRAM").document(getUID()).collection(dropdownSelection).document().set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void documentReference) {
                        showNotification("Program adaugat cu succes (pe server)!");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            } catch (Exception e) {
                 Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            updateActivitiesData(dropdownSelection);
        }
    }


    private void updateActivitiesData(final String collectionID)
    {
        Log.e(tag, "updateActivitiesData");
        getActivitiesData(collectionID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(gotActivitiesData) db.collection("PROGRAM").document(getUID()).collection(collectionID).document("PERFORMANCE").update("Activities total", activitiesTotal + 1);
        else {
            new CountDownTimer(100, 300) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    updateActivitiesDataRecurring(collectionID);
                }
            }.start();
        }
    }

    private void updateActivitiesDataRecurring(final String collectionID){
        Log.e(tag, "recurring");
        if(gotActivitiesData)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection(collectionID).document("PERFORMANCE").update("Activities total", activitiesTotal + 1);
        }
        else {
            new CountDownTimer(100, 300) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    updateActivitiesDataRecurring(collectionID);
                }
            }.start();
        }

    }

    int activitiesTotal = 0;
    private boolean gotActivitiesData = false;

    private void getActivitiesData(final String collectionID) // TODO THIS ?????
    {
        Log.e(tag, "getActivitiesData");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PROGRAM").document(getUID()).collection(collectionID).document("PERFORMANCE").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("Activities total") != null)
                {
                    Log.e(tag, "activities total is not null");
                    String actTS = documentSnapshot.get("Activities total").toString();
                    activitiesTotal = Integer.valueOf(actTS);
                }
                else if (documentSnapshot.get("Activities completed") == null)
                {
                    Log.e(tag, "activities completed is null");
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("Activities total", 0 + "");
                    userData.put("Activities completed", 0 + "");
                    db.collection("PROGRAM").document(getUID()).collection(collectionID).document("PERFORMANCE").set(userData);
                }
                else if (!documentSnapshot.exists())
                {
                    Log.e(tag, "the document does not exist");
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("Activities total", 0 + "");
                    userData.put("Activities completed", 0 + "");
                    db.collection("PROGRAM").document(getUID()).collection(collectionID).document("PERFORMANCE").set(userData);
                }
            }

        });
        gotActivitiesData = true;
    }


    private void getData() {
        if (activityNumber == Class_PassActivityDetails.activityNumber) {
            drawViews(Class_PassActivityDetails.title, Class_PassActivityDetails.startHour, Class_PassActivityDetails.endHour, Class_PassActivityDetails.details);
            addToServer(Class_PassActivityDetails.title, Class_PassActivityDetails.startHour, Class_PassActivityDetails.endHour, Class_PassActivityDetails.details);
            activityNumber++;
        }
    }

    //region drawViews

    private void drawViews(String title, String startTime, String endTime, String details) {
        String time = startTime + " -> " + endTime;

        drawLine();
        drawTitle(title);
        drawTime(time);
        drawDetails(details);
    }

    private int count = 0;

    private void drawLine() {
        count++;
        ConstraintLayout layout = findViewById(R.id.constraintLayoutMakeProgram);
        ConstraintSet set = new ConstraintSet();
        View whiteLine = new View(this);
        whiteLine.setId(count);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 1);
        whiteLine.setLayoutParams(layoutParams);
        whiteLine.setBackgroundColor(Color.parseColor("#FFFFFF"));
        layout.addView(whiteLine, 0);
        set.clone(layout);
        if (count == 1) {
            ImageView lv = findViewById(R.id.addActivityImageView);
            set.connect(whiteLine.getId(), ConstraintSet.TOP, lv.getId(), ConstraintSet.BOTTOM, 10);
            set.connect(whiteLine.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
            set.connect(whiteLine.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        } else {
            set.connect(whiteLine.getId(), ConstraintSet.TOP, count - 1, ConstraintSet.BOTTOM, 30);
            set.connect(whiteLine.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
            set.connect(whiteLine.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        }
        set.applyTo(layout);
    }


    private void drawTitle(String title) {
        count++;
        ConstraintLayout layout = findViewById(R.id.constraintLayoutMakeProgram);
        ConstraintSet set = new ConstraintSet();
        TextView view = new TextView(this);
        view.setText(title);
        view.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface typeface = ResourcesCompat.getFont(this, R.font.muli_extrabold);
        view.setTypeface(typeface);
        view.setId(count);
        layout.addView(view, 0);
        set.clone(layout);
        set.connect(view.getId(), ConstraintSet.TOP, count - 1, ConstraintSet.BOTTOM, 10);
        set.connect(view.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
        set.connect(view.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        set.applyTo(layout);
    }

    private void drawTime(String time) {
        count++;
        ConstraintLayout layout = findViewById(R.id.constraintLayoutMakeProgram);
        ConstraintSet set = new ConstraintSet();
        TextView view = new TextView(this);
        view.setText(time);
        view.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface typeface = ResourcesCompat.getFont(this, R.font.muli_semibold);
        view.setTypeface(typeface);
        view.setId(count);
        layout.addView(view, 0);
        set.clone(layout);
        set.connect(view.getId(), ConstraintSet.TOP, count - 1, ConstraintSet.BOTTOM, 15);
        set.connect(view.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
        set.connect(view.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        set.applyTo(layout);
    }

    private void drawDetails(String details) {
        count++;
        ConstraintLayout layout = findViewById(R.id.constraintLayoutMakeProgram);
        ConstraintSet set = new ConstraintSet();
        TextView view = new TextView(this);
        view.setText(details);
        view.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface typeface = ResourcesCompat.getFont(this, R.font.muli_semibold);
        view.setTypeface(typeface);
        view.setId(count);
        layout.addView(view, 0);
        set.clone(layout);
        set.connect(view.getId(), ConstraintSet.TOP, count - 1, ConstraintSet.BOTTOM, 15);
        set.connect(view.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
        set.connect(view.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        set.applyTo(layout);
    }

//endregion

    private void showNotification(String notifText) {
        Intent intent = new Intent(MakeProgramActivity.this, BottomNotificaionBar.class);
        intent.putExtra("message", notifText);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void showActivityProperties(String date) {
        Intent intent = new Intent(MakeProgramActivity.this, ActivityDataFieldsActivity.class);
        String activityNumberStr = String.valueOf(activityNumber);
        intent.putExtra("message", activityNumberStr);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

}
