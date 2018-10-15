package com.example.user.wifistaterecord.room;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public abstract class WiFiStarteDao {

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    public abstract void insert(WiFIStateData... data);

    @Delete
    public abstract void delete(List<WiFIStateData> data);// @Delete = 删除

    @Query("select * from WiFIStateData")
    //@Query = 查询 ,这里的注释括号里的内容代表这查询的关键词,可以用于筛查想要的数据。
    public abstract List<WiFIStateData> getAll();


}
