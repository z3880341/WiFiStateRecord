package com.example.user.wifistaterecord.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WiFIStateData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String time;

    public String state;
}
