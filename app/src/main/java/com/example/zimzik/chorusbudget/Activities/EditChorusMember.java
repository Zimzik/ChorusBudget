package com.example.zimzik.chorusbudget.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.AppDB;
import com.example.zimzik.chorusbudget.Room.Member;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditChorusMember extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText etFirstName, etSecondName, etPhoneNumber;
    private TextView tvBirthday;
    private Member member;
    private AppDB db;
    private Date birthday;
    public static final String DATEPICKER_TAG = "datepicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chorus_member);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edit member");
        }
        db = AppDB.getsInstance(this);
        etFirstName = findViewById(R.id.et_em_first_name);
        etSecondName = findViewById(R.id.et_em_second_name);
        etPhoneNumber = findViewById(R.id.et_em_phone_number);
        tvBirthday = findViewById(R.id.tv_em_birthday);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        tvBirthday.setOnClickListener(v -> {
            datePickerDialog.setVibrate(isVibrate());
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        });

        Gson gson = new Gson();
        member = gson.fromJson(getIntent().getStringExtra("member"), Member.class);
        birthday = new Date(member.getBirthday());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
        tvBirthday.setText(sdf.format(birthday));
        etFirstName.setText(member.getFirstName());
        etSecondName.setText(member.getLastName());
        etPhoneNumber.setText(String.valueOf(member.getPhoneNumber()));

        findViewById(R.id.save_changes_em_btn).setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString();
            String secondName = etSecondName.getText().toString();
            String stringPhoneNumber = etPhoneNumber.getText().toString();
            etFirstName.setError(null);
            etSecondName.setError(null);
            etPhoneNumber.setError(null);

            if (firstName.isEmpty() || secondName.isEmpty() || stringPhoneNumber.isEmpty()) {
                if (firstName.isEmpty()) {
                    etFirstName.setError("This field is empty!");
                }
                if (secondName.isEmpty()) {
                    etSecondName.setError("This field is empty!");
                }
                if (stringPhoneNumber.isEmpty()) {
                    etPhoneNumber.setError("This field is empty");
                }
            } else {
                member.setFirstName(firstName);
                member.setLastName(secondName);
                member.setPhoneNumber(Long.valueOf(stringPhoneNumber));
                member.setBirthday(birthday.getTime());
                Thread applyChanges = new Thread(() -> {
                    db.memberDao().update(member);
                });
                applyChanges.start();
                try {
                    applyChanges.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                String newMember = gson.toJson(member);
                intent.putExtra("member", newMember);
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        birthday = calendar.getTime();
        tvBirthday.setText(new SimpleDateFormat("dd/MM/yyy").format(birthday));
    }

    private boolean isVibrate() {
        return true;
    }
}
