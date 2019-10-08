package com.android.chocobar.starpedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.chocobar.starpedia.Model.People;
import com.android.chocobar.starpedia.PeopleActivity;
import com.android.chocobar.starpedia.R;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.VHolder> {

    List<People> results;

    public PeopleAdapter(List<People> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_view, viewGroup, false);
        VHolder vHolder = new VHolder(view);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder vHolder, final int i) {
        vHolder.tvPeople.setText(results.get(i).getName());
        vHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context ctx = view.getContext();
                Intent toPeople = new Intent(ctx, PeopleActivity.class);
                toPeople.putExtra("_PEOPLE", results.get(i).getUrl());
                ctx.startActivity(toPeople);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class VHolder extends RecyclerView.ViewHolder {
        TextView tvPeople;

        public VHolder(View itemView){
            super(itemView);
            tvPeople = itemView.findViewById(R.id.tv_people);
        }
    }
}
