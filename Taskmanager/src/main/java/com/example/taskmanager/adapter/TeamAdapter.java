package com.example.taskmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.ViewActivity;
import com.example.taskmanager.model.LoginDetails;
import com.example.taskmanager.model.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TicketHolder> implements Filterable {
    private List<Team> arrayList;
    private List<Team> filteredList;
    private Context context;
    List<LoginDetails> loginList;

    public TeamAdapter(List<Team> arrayList, Context context, List<LoginDetails> loginList) {
        this.arrayList = arrayList;
        this.filteredList = arrayList;
        this.context = context;
        this.loginList = loginList;
    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_adapter, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new TicketHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TicketHolder holder, int i) {
        Random r = new Random();
        int red = r.nextInt(255 + 1);
        int green = r.nextInt(255 + 1);
        int blue = r.nextInt(255 + 1);
        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(Color.rgb(red, green, blue));
        holder.tv_id.setBackground(draw);
        holder.tv_id.setText(filteredList.get(i).getTEAMID());
        holder.tv_team_name.setText(filteredList.get(i).getTEAMNAME());

        if (i == 0) {
            holder.layout.setVisibility(View.GONE);
            holder.tv_id.setVisibility(View.GONE);
            holder.tv_team_name.setVisibility(View.GONE);
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
                    List<Team> filterlist = new ArrayList<>();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Team mrDetails = arrayList.get(i);
                        if (mrDetails.getTEAMNAME().contains(search)) {
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
                filteredList = (List<Team>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    //------------------------------------------------------------------------------------------------------------------------------
    public class TicketHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_id)
        TextView tv_id;
        @BindView(R.id.txt_team_name)
        TextView tv_team_name;
        @BindView(R.id.lin_adapter)
        LinearLayout layout;

        private TicketHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Intent intent = new Intent(context, ViewActivity.class);
            intent.putExtra("TEAMID", arrayList.get(pos).getTEAMID());
            intent.putExtra("loginList", (Serializable) loginList);
            context.startActivity(intent);
        }
    }
}
