package com.example.taskmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.TicketHolder> implements Filterable {
    private List<Status> arrayList;
    private List<Status> filteredList;
    private Context context;

    public StatusAdapter(List<Status> arrayList, Context context) {
        this.arrayList = arrayList;
        this.filteredList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_adapter, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new TicketHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TicketHolder holder, int i) {
        holder.tv_name.setText(filteredList.get(i).getEMPNAME());
        holder.tv_date.setText(filteredList.get(i).getDATE());
        holder.tv_time.setText(filteredList.get(i).getTIME());
        holder.tv_status.setText(filteredList.get(i).getSTATUS());

        if (filteredList.get(i).getMANAGEMENT().equals("1")) {
            holder.layout.setBackground(context.getResources().getDrawable(R.drawable.bg_message));
        } else {
            holder.layout.setBackground(context.getResources().getDrawable(R.drawable.bg_message1));
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    //------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                if (search.isEmpty())
                    filteredList = arrayList;
                else {
                    List<Status> filterlist = new ArrayList<>();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Status mrDetails = arrayList.get(i);
                        if (mrDetails.getDATE().contains(search)) {
                            filterlist.add(mrDetails);
                        }
                    }
                    filteredList = filterlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Status>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    //------------------------------------------------------------------------------------------------------------------------------
    public class TicketHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_name)
        TextView tv_name;
        @BindView(R.id.txt_date)
        TextView tv_date;
        @BindView(R.id.txt_time)
        TextView tv_time;
        @BindView(R.id.txt_status)
        TextView tv_status;
        @BindView(R.id.lin_adapter)
        LinearLayout layout;


        private TicketHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
