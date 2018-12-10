package com.example.madvincy.safe.parking_place_account_activities.CarParkHistory.ParkingHistoryRecyclerView;

public class ParkHistoryObject {
    private String parkingId;
    private String time;

    public ParkHistoryObject(String parkingId, String time){
        this.parkingId = parkingId;
        this.time = time;
    }

    public String getParkingId(){return parkingId;}
    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }
}
