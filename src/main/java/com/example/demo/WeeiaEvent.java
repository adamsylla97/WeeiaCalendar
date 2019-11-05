package com.example.demo;

public class WeeiaEvent {

    private String eventName;
    private String eventDay;

    WeeiaEvent(String eventName, String eventDay){
        this.eventDay = eventDay;
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "["+eventDay+" "+eventName+"]";
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDay() {
        return eventDay;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDay(String eventDay) {
        this.eventDay = eventDay;
    }
}
