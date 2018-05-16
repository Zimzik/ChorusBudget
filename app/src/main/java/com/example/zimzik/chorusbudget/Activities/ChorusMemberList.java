package com.example.zimzik.chorusbudget.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.zimzik.chorusbudget.Adapters.MemberListAdapter;
import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.AppDB;
import com.example.zimzik.chorusbudget.Room.Member;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChorusMemberList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView lvMembers;
    private SwipeRefreshLayout swipeRefreshLayout;
    MemberListAdapter memberListAdapter;
    RecyclerView recyclerView;
    AppDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_member_list);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chorus member list");
        }
        db = AppDB.getsInstance(this);
        recyclerView = findViewById(R.id.rv_members);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        memberListAdapter = refreshList();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        memberListAdapter = refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.member_list_menu, menu);
        MenuItem item = menu.findItem(R.id.search_member);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                memberListAdapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_new_member) {
            startActivity(new Intent(this, NewChorusMember.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private MemberListAdapter refreshList() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<Member>> future = executorService.submit(() -> db.memberDao().getAllMembers());
        List<Member> memberList = null;
        try {
            memberList = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        Collections.sort(memberList, (m1, m2) -> m1.toString().compareToIgnoreCase(m2.toString()));

        MemberListAdapter listAdapter = new MemberListAdapter(memberList, m -> {
            Intent intent = new Intent(ChorusMemberList.this, CurrentMember.class);
            Gson gson = new Gson();
            String myJson = gson.toJson(m);
            intent.putExtra("member", myJson);
            startActivity(intent);
        }, m -> deleteMemberFromDB(m));
        recyclerView.setAdapter(listAdapter);
        return listAdapter;
    }

    @Override
    public void onRefresh() {
        refreshList();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void deleteMemberFromDB(Member m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Are you shure to delete member %s %s from DB?", m.getLastName(), m.getFirstName()));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
            Thread deleteMember = new Thread(() -> db.memberDao().delete(m));
            deleteMember.start();
            try {
                deleteMember.join();
                refreshList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    public static int calculateAge(long birthday) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTime(new Date(birthday));
        dob.add(Calendar.DAY_OF_MONTH, -1);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) age--;
        return age;
    }
}


