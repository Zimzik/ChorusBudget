package com.example.zimzik.chorusbudget.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.AppDB;
import com.example.zimzik.chorusbudget.Room.Member;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewChorusMember extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private TextView tvBirthday;
    public static final String DATEPICKER_TAG = "datepicker";
    private Date birthday;
    private AppDB db;
    private EditText etFirstName, etSecondName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chorus_member);
        Context that = this;
        //Init EditTexts
        etFirstName = findViewById(R.id.et_first_name);
        etSecondName = findViewById(R.id.et_second_name);

         //Init DB
        db = AppDB.getsInstance(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("New member");
        }

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        tvBirthday = findViewById(R.id.tv_birthday);
        tvBirthday.setOnClickListener(v -> {
            datePickerDialog.setVibrate(isVibrate());
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        });

        // Save button implementation
        findViewById(R.id.save_new_member_btn).setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString();
            String secondName = etSecondName.getText().toString();
            String stringBirthday = tvBirthday.getText().toString();
            etFirstName.setError(null);
            etSecondName.setError(null);
            tvBirthday.setError(null);

            if (firstName.isEmpty() || secondName.isEmpty() || stringBirthday.equals("Birthday")) {
                if (firstName.isEmpty()) {
                    etFirstName.setError("This field is empty!");
                }
                if (secondName.isEmpty()) {
                    etSecondName.setError("This field is empty!");
                }
                if (stringBirthday.equals("Birthday")) {
                    tvBirthday.setError("This field is empty");
                }
            } else {
                //Create new thread to save data on DB
                Thread insert = new Thread(() -> db.memberDao().insertAll(new Member(firstName, secondName, birthday.getTime())));
                insert.start();
                try {
                    insert.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Successfully data saved message
                Toast.makeText(that, "New member succesfully saved on DB", Toast.LENGTH_LONG).show();

                // Clear all fileds and errors
                etFirstName.setError(null);
                etSecondName.setError(null);
                tvBirthday.setError(null);
                etFirstName.setText("");
                etSecondName.setText("");
                tvBirthday.setText(R.string.birthday);
            }
        });
    }

    private boolean isVibrate() {
        return true;
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        birthday = calendar.getTime();
        tvBirthday.setText(new SimpleDateFormat("dd/MM/yyy").format(birthday));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        //Toast.makeText(this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
