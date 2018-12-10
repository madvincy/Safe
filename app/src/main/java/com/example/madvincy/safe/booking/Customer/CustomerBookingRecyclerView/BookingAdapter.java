package com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parkinghistory.historyRecyclerView.HistoryViewHolders;


import java.util.List;

/**
 * Created by manel on 03/04/2017.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingViewHolder> {

    private List<BookingObject> itemList;
    private Context context;

    public BookingAdapter(List<BookingObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        BookingViewHolder rcv = new BookingViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bookingId.setText(itemList.get(position).getBookingId());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
    }


    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
