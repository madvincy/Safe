package com.example.madvincy.safe.parkinghistory.historyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parkinghistory.HistorySingleActivity;



public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView parkingId;
    public TextView time,endtime;
    public HistoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        parkingId = (TextView) itemView.findViewById(R.id.parkingId);
        time = (TextView) itemView.findViewById(R.id.time);
        endtime= (TextView) itemView.findViewById(R.id.endtime);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString("parkingId", parkingId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
