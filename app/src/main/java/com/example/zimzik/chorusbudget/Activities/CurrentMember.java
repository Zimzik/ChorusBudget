package com.example.zimzik.chorusbudget.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.Member;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentMember extends AppCompatActivity {

    TextView tvName, tvAge, tvPhoneNumber, tvCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_member);

        tvName = findViewById(R.id.cm_tv_name);
        tvAge = findViewById(R.id.cm_tv_age);
        tvPhoneNumber = findViewById(R.id.cm_tv_phone_number);
        tvCash = findViewById(R.id.cm_tv_cash);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Member");
        }

        Gson gson = new Gson();
        Member member = gson.fromJson(getIntent().getStringExtra("member"), Member.class);
        String name = member.getLastName() + " " + member.getFirstName();
        String age = String.format("%s (%d)", new SimpleDateFormat("dd.MM.yyyy").format(new Date(member.getBirthday())).toString(), ChorusMemberList.calculateAge(member.getBirthday()));
        int phoneNumber = member.getPhoneNumber();
        tvName.setText(name);
        tvAge.setText(age);
        tvPhoneNumber.setText(String.valueOf(phoneNumber));
        tvCash.setText("100 ₴");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cm_menu_edit) {

        } else if (item.getItemId() == R.id.cm_menu_delete) {

        }
        return super.onOptionsItemSelected(item);
    }
}
