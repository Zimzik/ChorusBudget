package com.example.zimzik.chorusbudget.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.Member;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Member> members;

    public MemberListAdapter(Context context, List<Member> members) {
        this.inflater = LayoutInflater.from(context);
        this.members = members;
    }


    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberListAdapter.ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.name.setText(member.toString());
        holder.age.setText(calculateAge(member.getBirthday()) + " y.o.");
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, age;
        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            age = view.findViewById(R.id.tv_age);
        }
    }

    private int calculateAge(long birthday) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTime(new Date(birthday));
        dob.add(Calendar.DAY_OF_MONTH, -1);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) age--;
        return age;
    }
}
