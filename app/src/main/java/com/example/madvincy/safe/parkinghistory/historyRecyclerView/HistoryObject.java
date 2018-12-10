package com.example.madvincy.safe.parkinghistory.historyRecyclerView;

/**
 * Created by madvincy on 1/12/2018.
 */

public class  HistoryObject {
    private String parkingId;
    private String time;
    private String endtime;

    public HistoryObject(String rideId, String time, String endtime){
        this.parkingId = parkingId;
        this.time = time;
        this.endtime = endtime;
    }

    public String getParkingId(){return parkingId;}
    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }

    public String getEndTime(){return endtime;}
    public void setEndTime(String endtime) {
        this.endtime = endtime;
    }
}
