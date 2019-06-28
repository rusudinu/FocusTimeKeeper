package com.codingshadows.focustimekeeper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    int pastDay = 0;
    private static String[] sortVector = new String[10000];
    private TextView quoteTV;
    private static String UID = "";
    private int spamProtect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        quoteTV = findViewById(R.id.quoteTextView);

        getUID();
        getQuote();
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
        Date date2 = new Date();
        String date3 = formatter2.format(date2);
        //getActivities(date3 + "Astazi");

        final TextView showDateTV = findViewById(R.id.titleTextView);
        showDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        showDateTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                spamProtect = 0;
                ConstraintLayout lyt = findViewById(R.id.mConstraintLayout);
                lyt.removeAllViews();
                count = 0;
                for (int i = 1; i < sortVector.length; i++)
                    sortVector[i] = "";
                getActivities(showDateTV.getText().toString());
            }
        });

//region showcalendar listener
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

                /*
                ConstraintLayout lyt = findViewById(R.id.mConstraintLayout);
                lyt.removeAllViews();
                count = 0;
                for (int i = 1; i < sortVector.length; i++)
                    sortVector[i] = "";
                getActivities(showDateTV.getText().toString());
                */
            }
        };


//endregion

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        Date date = new Date();
        String currentDate = formatter.format(date);
        showDateTV.setText("Astazi, " + currentDate);

        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operateMenu();
            }
        });

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
                MainMenuActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.setTitle("Please select the date : ");
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void operateMenu() {
        Intent intent = new Intent(MainMenuActivity.this, MenuActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    //region draw title hours etc
    private int count = 0;

    private void drawLine() {
        count++;
        ConstraintLayout layout = findViewById(R.id.mConstraintLayout);
        ConstraintSet set = new ConstraintSet();
        View whiteLine = new View(this);
        whiteLine.setId(count);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 1);
        whiteLine.setLayoutParams(layoutParams);
        whiteLine.setBackgroundColor(Color.parseColor("#FFFFFF"));
        layout.addView(whiteLine, 0);
        set.clone(layout);
        if (count == 1) {
            set.connect(whiteLine.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 1);
            set.connect(whiteLine.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
            set.connect(whiteLine.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        } else {
            set.connect(whiteLine.getId(), ConstraintSet.TOP, count - 2, ConstraintSet.BOTTOM, 30);
            set.connect(whiteLine.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 60);
            set.connect(whiteLine.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 60);
        }
        set.applyTo(layout);
    }


    private void drawTitle(String title) {
        count++;
        ConstraintLayout layout = findViewById(R.id.mConstraintLayout);
        ConstraintSet set = new ConstraintSet();
        TextView view = new TextView(this);
        view.setText(title);
        view.setTextColor(Color.parseColor("#FFFFFF"));
        view.setTextSize(24);
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
        ConstraintLayout layout = findViewById(R.id.mConstraintLayout);
        ConstraintSet set = new ConstraintSet();
        TextView view = new TextView(this);
        view.setText(time);
        view.setTextColor(Color.parseColor("#FFFFFF"));
        view.setTextSize(22);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.muli_semibold);
        view.setTypeface(typeface);
        view.setId(count);
        layout.addView(view, 0);
        set.clone(layout);
        set.connect(view.getId(), ConstraintSet.TOP, count - 1, ConstraintSet.BOTTOM, 15);
        set.connect(view.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 20);
        set.applyTo(layout);
    }

    private void drawDetails(String details) {
        count++;
        ConstraintLayout layout = findViewById(R.id.mConstraintLayout);
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
        set.connect(view.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 10);
        set.applyTo(layout);
    }

    private void drawComplete(String docID) {
        count++;
        ConstraintLayout layout = findViewById(R.id.mConstraintLayout);
        ConstraintSet set = new ConstraintSet();
        Switch view = new Switch(this);
        view.setId(count);
        view.setTag(docID);
        layout.addView(view, 0);
        set.clone(layout);
        set.connect(view.getId(), ConstraintSet.TOP, count - 3, ConstraintSet.BOTTOM, 10);
        set.connect(view.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 20);
        set.connect(view.getId(), ConstraintSet.BOTTOM, count - 1, ConstraintSet.TOP, 10);
        set.applyTo(layout);
        view.setOnClickListener(this);
    }

//endregion

    private void drawViews(String title, String timePeriod, String details, String docID) {
        drawLine();
        drawTitle(title);
        drawTime(timePeriod);
        drawDetails(details);
        drawComplete(docID);
    }

    @Override
    public void onClick(View v) {
        int idd = v.getId();
        Switch sw = findViewById(idd);
        String docID = sw.getTag().toString();
        deleteDoc(docID);
    }

    private void deleteDoc(String docID) {
//region handle the date
        TextView et = findViewById(R.id.titleTextView);
        String date = et.getText().toString();
        Calendar calendar = Calendar.getInstance();
        Date calendarDate = new Date();

        if (date.contains("Astazi")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = new Date();
            String dateS = formatter.format(todayDate);
            try {
                calendarDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateS);
            } catch (ParseException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                calendarDate = formatter.parse(date);
            } catch (ParseException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate = formatter.format(calendarDate);
        stringDate = stringDate.replace("/", "");
        final String stringDateServer = stringDate;
        //endregion
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PROGRAM").document(getUID()).collection(stringDateServer).document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(MainMenuActivity.this, "sters cu succes!", Toast.LENGTH_SHORT).show();
                ConstraintLayout lyt = findViewById(R.id.mConstraintLayout);
                lyt.removeAllViews();
                count = 0;
                TextView et = findViewById(R.id.titleTextView);
                String date = et.getText().toString();
                getActivities(date);
            }
        });
    }


    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();
        return user.getUid();
    }


    @SuppressLint("SimpleDateFormat")
    private void getActivities(final String date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 1; i < sortVector.length; i++)
            sortVector[i] = "";
        //region fix date
        Calendar calendar = Calendar.getInstance();
        Date calendarDate = new Date();

        if (date.contains("Astazi")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = new Date();
            String dateS = formatter.format(todayDate);
            try {
                calendarDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateS);
            } catch (ParseException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            //am deja 23/05/2019 o transform in data o setez in calendar si iau day of the week de pe ea
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                calendarDate = formatter.parse(date);
            } catch (ParseException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate = formatter.format(calendarDate);
        stringDate = stringDate.replace("/", "");
        final String stringDateServer = stringDate;
        calendar.setTime(calendarDate);
        final int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //endregion

        db.collection("PROGRAM").document(getUID()).collection(stringDateServer).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int x = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        x++;
                        //getActivity(stringDateServer, document.getId());
                        sortActivities(stringDateServer, document.getId());
                    }
                    if (x == 0) {
                        if(spamProtect >=3) // >= the number of attempts to get data
                        {
                          return;
                        }
                        else
                        {
                            copyPresetToDate(stringDateServer, dayOfTheWeek);
                            spamProtect++;
                        }

                        new CountDownTimer(1500, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                getActivities(date);
                            }
                        }.start();
                    } else {
                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                showActivities();
                                Log.d("SHOW ACTIVITIES", "THIS IS ANNOYING LMAO HELP ME PLEASE QUICK");
                            }
                        }.start();
                    }
                } else {
                    Toast.makeText(MainMenuActivity.this, "Task failed - attempting a fix ... ", Toast.LENGTH_SHORT).show();
                    copyPresetToDate(stringDateServer, dayOfTheWeek);
                }
            }
        });

    }

    //region copy from one date to another
    private void copyPresetToDate(final String date, int dayOfTheWeek) {
        if (dayOfTheWeek == 1) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Luni").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Luni").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        } else if (dayOfTheWeek == 2) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Marti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Marti").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        } else if (dayOfTheWeek == 3) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Miercuri").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Miercuri").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        } else if (dayOfTheWeek == 4) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Joi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Joi").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        } else if (dayOfTheWeek == 5) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Vineri").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Vineri").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        } else if (dayOfTheWeek == 6) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Sambata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Sambata").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        } else if (dayOfTheWeek == 7) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(getUID()).collection("Duminica").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //final String docContents = getDocStrings(document.getId(), "Miercuri");
                            db.collection("PROGRAM").document(getUID()).collection("Duminica").document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    createDocument(date, documentSnapshot.get("Title").toString(), documentSnapshot.get("Time").toString(), documentSnapshot.get("Details").toString());
                                }
                            });
                        }
                    }
                }
            });
        }

    }

    private void createDocument(String date, String title, String time, String details) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("Title", title);
        userData.put("Time", time);
        userData.put("Details", details);
        db.collection("PROGRAM").document(getUID()).collection(date).document().set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

//endregion


    private void showActivities() {
        for (int i = 1; i < sortVector.length; i++) {
            try {
                if (!sortVector[i].equals("")) {
                    String data = sortVector[i];
                    String[] splitData;
                    splitData = data.split("\\*");
                    drawViews(splitData[0], splitData[1], splitData[2], splitData[3]);
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sortActivities(String date, final String docID) {
        SortActivitiesAsyncTask task = new SortActivitiesAsyncTask();
        task.execute(date, docID);
    }


    private void getQuote() {
        QuoteOfDayAsyncTask task = new QuoteOfDayAsyncTask(this);
        task.execute(10);
    }


    private static class QuoteOfDayAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<MainMenuActivity> mainMenuActivityWeakReference;

        QuoteOfDayAsyncTask(MainMenuActivity activity) {
            mainMenuActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(Integer... ints) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("QUOTE").document("TODAY").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String quote = Objects.requireNonNull(documentSnapshot.get("Quote")).toString();
                    onPostExecute(quote);
                }
            });
            return "finished";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //finished executing
            MainMenuActivity activity = mainMenuActivityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.quoteTV.setVisibility(View.VISIBLE);
            if (!s.equals("finished")) {
                activity.quoteTV.setText(s);
            }
        }
    }

    private static class SortActivitiesAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PROGRAM").document(UID).collection(strings[0]).document(strings[1]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String sHour = Objects.requireNonNull(documentSnapshot.get("Time")).toString();
                    String[] vect;
                    String splitter = " -> ";
                    vect = sHour.split(splitter);
                    sHour = vect[0];
                    String sHourNumber = sHour.replace(":", "");
                    sortVector[Integer.valueOf(sHourNumber)] = Objects.requireNonNull(documentSnapshot.get("Title")).toString() + "*" + Objects.requireNonNull(documentSnapshot.get("Time")).toString() + "*" + Objects.requireNonNull(documentSnapshot.get("Details")).toString() + "*" + documentSnapshot.getId();
                }
            });
            return "finished";
        }
    }

}



