package com.example.zimzik.chorusbudget.Activities;

import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.AppDB;
import com.example.zimzik.chorusbudget.Room.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChorusMemberList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView lvMembers;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_member_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chorus member list");
        }
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        arrayAdapter = refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayAdapter = refreshList();
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
                arrayAdapter.getFilter().filter(s);
                return false;
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
        if (id == R.id.search_member) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private ArrayAdapter refreshList() {
        lvMembers = findViewById(R.id.lv_members);
        AppDB db = AppDB.getsInstance(this);
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
        ArrayAdapter<Member> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, memberList);
        lvMembers.setAdapter(arrayAdapter);
        return arrayAdapter;
    }

    @Override
    public void onRefresh() {
        refreshList();
        swipeRefreshLayout.setRefreshing(false);
    }
}
