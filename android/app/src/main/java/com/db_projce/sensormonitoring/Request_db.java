package com.db_projce.sensormonitoring;

public class Request_db {

    //DB 맞춰서 수정 하면 됩니다. -> 우선 수정 해둠

    private String time;
    private int temp;//온도
    private int hum;//습도
    private int pm1_0;//pm1.0
    private int pm2_5;//pm2.5
    private int pm10;//pm10

    public String getTime(){
        return time;
    }

    public int getTemp(){
        return temp;
    }

    public int getHum(){
        return hum;
    }

    public int getDust(){
        return pm1_0;
    }

    public int getDust2(){
        return pm2_5;
    }

    public int getDust3(){
        return pm10;
    }

}
