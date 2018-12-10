package com.example.madvincy.safe.parkinghistory.historyRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madvincy.safe.R;


import java.util.List;

/**
 * Created by manel on 03/04/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolders> {

    private List<HistoryObject> itemList;
    private Context context;

    public HistoryAdapter(List<HistoryObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public HistoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        HistoryViewHolders rcv = new HistoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolders holder, final int position) {
        holder.parkingId.setText(itemList.get(position).getParkingId());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
        if(itemList.get(position).getTime()!=null){
            holder.endtime.setText(itemList.get(position).getEndTime());
        }
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}