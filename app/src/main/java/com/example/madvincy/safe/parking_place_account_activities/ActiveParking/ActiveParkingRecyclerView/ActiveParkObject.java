package com.example.madvincy.safe.parking_place_account_activities.ActiveParking.ActiveParkingRecyclerView;

public class ActiveParkObject {
    private String parkingId;
    private String time;

    public ActiveParkObject(String parkingId, String time){
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
