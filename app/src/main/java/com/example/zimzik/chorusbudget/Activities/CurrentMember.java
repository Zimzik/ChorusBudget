package com.example.zimzik.chorusbudget.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.AppDB;
import com.example.zimzik.chorusbudget.Room.Member;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentMember extends AppCompatActivity {

    private TextView tvName, tvAge, tvPhoneNumber, tvCash;
    private AppDB db;
    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_member);

        db = AppDB.getsInstance(this);
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
        member = gson.fromJson(getIntent().getStringExtra("member"), Member.class);
        setAllFields(member);
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
            Intent intent = new Intent(this, EditChorusMember.class);
            Gson gson = new Gson();
            String myJson = gson.toJson(member);
            intent.putExtra("member", myJson);
            startActivityForResult(intent, 1);
        } else if (item.getItemId() == R.id.cm_menu_delete) {
            onDeleteMemberClick(member);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        Gson gson = new Gson();
        member = gson.fromJson(data.getStringExtra("member"), Member.class);
        setAllFields(member);
        Toast.makeText(this, "Changes applied", Toast.LENGTH_LONG).show();
    }

    private void onDeleteMemberClick(Member m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Are you shure to delete member %s %s from DB?", m.getLastName(), m.getFirstName()));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
            Thread deleteMember = new Thread(() -> db.memberDao().delete(m));
            deleteMember.start();
            try {
                deleteMember.join();
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    private void setAllFields(Member member) {
        String name = member.getLastName() + " " + member.getFirstName();
        String age = String.format("%s (%d)", new SimpleDateFormat("dd.MM.yyyy").format(new Date(member.getBirthday())).toString(), ChorusMemberList.calculateAge(member.getBirthday()));
        long phoneNumber = member.getPhoneNumber();
        tvName.setText(name);
        tvAge.setText(age);
        tvPhoneNumber.setText(String.valueOf(phoneNumber));
        tvCash.setText("100 ₴");
    }
}
