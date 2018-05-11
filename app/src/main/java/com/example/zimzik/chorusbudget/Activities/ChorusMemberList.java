package com.example.zimzik.chorusbudget.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.zimzik.chorusbudget.Adapters.MemberListAdapter;
import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.AppDB;
import com.example.zimzik.chorusbudget.Room.Member;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChorusMemberList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView lvMembers;
    private SwipeRefreshLayout swipeRefreshLayout;
    MemberListAdapter memberListAdapter;

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
        memberListAdapter = refreshList();
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
        if (id == R.id.search_member) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private MemberListAdapter refreshList() {
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

        RecyclerView recyclerView = findViewById(R.id.rv_members);
        MemberListAdapter listAdapter = new MemberListAdapter(this, memberList);
        recyclerView.setAdapter(listAdapter);
        return listAdapter;
    }

    @Override
    public void onRefresh() {
        refreshList();
        swipeRefreshLayout.setRefreshing(false);
    }
}


