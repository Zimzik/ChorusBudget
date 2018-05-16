package com.example.zimzik.chorusbudget.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.chorusbudget.Activities.ChorusMemberList;
import com.example.zimzik.chorusbudget.R;
import com.example.zimzik.chorusbudget.Room.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> implements Filterable {
    private List<Member> membersList;
    private List<Member> filteredMembersList;
    private ClickAction<Member> onClickListener;
    private ClickAction<Member> onContextMenuClick;

    public MemberListAdapter(List<Member> membersList, ClickAction<Member> onClickListener, ClickAction<Member> onContextMenuClick) {
        this.membersList = membersList;
        this.filteredMembersList = membersList;
        this.onClickListener = onClickListener;
        this.onContextMenuClick = onContextMenuClick;
    }


    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberListAdapter.ViewHolder holder, int position) {
        final Member member = filteredMembersList.get(position);
        holder.name.setText(member.toString());
        holder.age.setText(String.format("%d y.o.", ChorusMemberList.calculateAge(member.getBirthday())));
        holder.digit.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(view.getContext(), holder.digit);
            menu.inflate(R.menu.context_menu);
            menu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.cmb_delete) {
                    onContextMenuClick.call(member);
                }
                return false;
            });
            menu.show();
        });
        holder.itemView.setOnClickListener(v -> onClickListener.call(member));
    }

    @Override
    public int getItemCount() {
        return filteredMembersList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    filteredMembersList = membersList;
                } else {
                    ArrayList<Member> filteredList = new ArrayList<>();
                    for(Member m: membersList) {
                        if(m.toString().toLowerCase().contains(charString)) {
                            filteredList.add(m);
                        }
                    }
                    filteredMembersList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMembersList;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredMembersList = (List<Member>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView name, age, digit;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            age = view.findViewById(R.id.tv_age);
            digit = view.findViewById(R.id.tv_option_digit);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }
    }

    public interface ClickAction<T> {
        void call(T object);
    }
}
