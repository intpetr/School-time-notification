package com.suli.rk;

import java.time.LocalDateTime;

public class period {

    public int hour = 0;
    public int minute = 0;
    public int endinghour = 0;
    public int endingminute = 0;
    public String hunArticle ="";

    public period(int startinghour, int startingminute, String article){
        hour = startinghour;
        minute = startingminute;
        hunArticle = article;

        if (minute + 45 >= 60){
            int leftover = minute + 45 - 60;
            endingminute = leftover;
            endinghour = hour + 1;
        }
        else{
            endinghour = hour;
            endingminute = minute +45;
        }

    }

    public LocalDateTime getstartldt(){
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),hour,minute);
    }

    public LocalDateTime getendingldt(){
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),endinghour,endingminute);
    }

    public String getHunArticle(){
        return hunArticle;
    }

}
